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

    public InputStream getInputStream() {
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
                throw new IllegalArgumentException(
                        "MemoryFile doesn't implement seek. Start parameter must be 0");
            }

            input.transferTo(out);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
    }
}
