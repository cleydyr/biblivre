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
package biblivre.core.file;

import biblivre.core.utils.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MemoryFile extends BiblivreFile {

    private InputStream inputStream;

    public MemoryFile() {}

    public MemoryFile(String name, String contentType, long size, InputStream inputStream) {
        this.setName(name);
        this.setContentType(contentType);
        this.setSize(size);
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    @Override
    public boolean exists() {
        return inputStream != null;
    }

    @Override
    public void copy(OutputStream out, long start, long size) throws IOException {
        try (InputStream input = this.inputStream) {

            if (input == null) {
                return;
            }

            if (start != 0) {
                throw new IOException(
                        "MemoryFile doesn't implements seek. Start parameter must be 0");
            }

            byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
            int read;

            while ((read = input.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
    }
}
