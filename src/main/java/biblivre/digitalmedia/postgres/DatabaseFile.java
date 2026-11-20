/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.digitalmedia.postgres;

import biblivre.core.file.BiblivreFile;
import biblivre.core.utils.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.postgresql.largeobject.LargeObject;

public final class DatabaseFile extends BiblivreFile {

    private Connection connection;
    private LargeObject largeObject;

    public DatabaseFile() {}

    public DatabaseFile(Connection connection, LargeObject largeObject) {
        this.setConnection(connection);
        this.setLargeObject(largeObject);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public LargeObject getLargeObject() {
        return this.largeObject;
    }

    public void setLargeObject(LargeObject largeObject) {
        this.largeObject = largeObject;
    }

    @Override
    public boolean exists() {
        LargeObject input = this.getLargeObject();

        return input != null;
    }

    @Override
    public void copy(OutputStream out, long start, long size) throws IOException {
        LargeObject input = this.getLargeObject();

        if (input == null) {
            return;
        }

        byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
        int read;

        try {
            if (input.size() == size) {
                while ((read = input.read(buffer, 0, Constants.DEFAULT_BUFFER_SIZE)) > 0) {
                    out.write(buffer, 0, read);
                }
            } else {
                input.seek((int) start);
                long toRead = size;

                while ((read = input.read(buffer, 0, Constants.DEFAULT_BUFFER_SIZE)) > 0) {
                    if ((toRead -= read) > 0) {
                        out.write(buffer, 0, read);
                    } else {
                        out.write(buffer, 0, (int) toRead + read);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            LargeObject largeObject = this.getLargeObject();

            if (largeObject != null) {
                this.setLargeObject(null);

                largeObject.close();
            }

            Connection con = getConnection();

            if (con != null) {
                this.setConnection(null);

                con.commit();

                if (!con.isClosed()) {
                    con.close();
                }
            }
        } catch (SQLException sqlException) {
            throw new IOException(sqlException);
        }
    }

    /**
     * A stream over the large object, rewound to its first byte. Skipping seeks on the server.
     * Closing the stream leaves the large object open; {@link #close()} closes it.
     */
    @Override
    public InputStream openStream() throws IOException {
        LargeObject input = this.getLargeObject();

        if (input == null) {
            throw new IOException("DatabaseFile has no large object");
        }

        try {
            return new LargeObjectInputStream(input);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean isSeekable() {
        return true;
    }

    private static final class LargeObjectInputStream extends InputStream {

        private final LargeObject largeObject;
        private final long size;

        LargeObjectInputStream(LargeObject largeObject) throws SQLException {
            this.largeObject = largeObject;
            this.size = largeObject.size64();
            largeObject.seek64(0, LargeObject.SEEK_SET);
        }

        @Override
        public int read() throws IOException {
            byte[] single = new byte[1];

            return this.read(single, 0, 1) == -1 ? -1 : single[0] & 0xFF;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            Objects.checkFromIndexSize(offset, length, buffer.length);

            if (length == 0) {
                return 0;
            }

            try {
                int read = this.largeObject.read(buffer, offset, length);

                return read > 0 ? read : -1;
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }

        @Override
        public long skip(long count) throws IOException {
            if (count <= 0) {
                return 0;
            }

            try {
                long position = this.largeObject.tell64();
                long target = Math.min(this.size, position + count);

                this.largeObject.seek64(target, LargeObject.SEEK_SET);

                return target - position;
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void close() {
            // DatabaseFile.close() closes the large object and its connection.
        }
    }
}
