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

import java.io.*;
import java.nio.file.Files;

public class DiskFile extends BiblivreFile {
    private final File file;

    public DiskFile(File file, String contentType) {
        this.file = file;
        this.setContentType(contentType);
        if (file != null) {
            this.setName(file.getName());
            this.setSize(file.length());
            this.setLastModified(file.lastModified());
        }
    }

    @Override
    public void close() {}

    @Override
    public boolean exists() {
        return this.file != null && this.file.exists();
    }

    @Override
    public void copy(OutputStream out, long start, long size) throws IOException {
        if (this.file == null) {
            return;
        }

        try (InputStream input = Files.newInputStream(this.file.toPath())) {
            long skipped = input.skip(start);

            if (skipped != start) {
                throw new IOException("premature end of file when skipping to " + start);
            }

            input.transferTo(out);
        }
    }

    public boolean delete() {
        return this.file != null && this.file.delete();
    }
}
