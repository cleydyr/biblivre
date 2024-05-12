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
package biblivre.digitalmedia;

import biblivre.core.AbstractBO;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import jakarta.annotation.Nonnull;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DigitalMediaBO extends AbstractBO {
    protected DigitalMediaDAO digitalMediaDAO;

    public Integer save(MemoryFile file) {
        return this.digitalMediaDAO.save(file);
    }

    public BiblivreFile load(int id, String name) {
        return this.digitalMediaDAO.load(id, name);
    }

    public void delete(Integer fileId, String fileName) {
        this.digitalMediaDAO.delete(fileId);
    }

    public List<DigitalMediaDTO> list() {
        return this.digitalMediaDAO.list();
    }

    public long importFile(File file) {
        return this.digitalMediaDAO.importFile(file);
    }

    public Optional<BiblivreFile> parseFromBase64(String base64) {
        return fetchDBFileWithWindowsEncoding(base64)
                .or(() -> fetchDBFileWithEncoding(base64, Constants.DEFAULT_CHARSET));
    }

    private @Nonnull Optional<BiblivreFile> fetchDBFileWithEncoding(String id, Charset charset) {
        var decodedId = DigitalMediaEncodingUtil.decode(id, charset);

        return decodedId.map(pair -> digitalMediaDAO.load(pair.getLeft(), pair.getRight()));
    }

    private Optional<BiblivreFile> fetchDBFileWithWindowsEncoding(String id) {
        return fetchDBFileWithEncoding(id, Constants.WINDOWS_CHARSET);
    }

    @Autowired
    public void setDigitalMediaDAO(DigitalMediaDAO digitalMediaDAO) {
        this.digitalMediaDAO = digitalMediaDAO;
    }
}
