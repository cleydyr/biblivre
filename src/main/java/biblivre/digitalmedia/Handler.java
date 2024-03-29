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

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {

    private DigitalMediaBO digitalMediaBO;

    public void download(ExtendedRequest request, ExtendedResponse response) {

        String id = request.getString("id").replaceAll("_", "\\\\");

        String fileId = null;
        String fileName = null;

        BiblivreFile file = _tryFetchingDBFileWithWindowsEncoding(id, fileId, fileName);

        if (file == null) {
            file = _tryFetchingDBFileWithEncoding(id, fileId, fileName, Constants.DEFAULT_CHARSET);
        }

        if (file == null) {
            this.setReturnCode(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        this.setFile(file);

        this.setCallback(file::close);
    }

    public void upload(ExtendedRequest request, ExtendedResponse response) {

        MemoryFile file = request.getFile("file");

        if (file == null) {
            this.setMessage(ActionResult.WARNING, "digitalmedia.error.no_file_uploaded");
            return;
        }

        Integer serial = digitalMediaBO.save(file);

        String encodedId = DigitalMediaEncodingUtil.getEncodedId(serial, file.getName());

        if (StringUtils.isNotBlank(encodedId)) {
            put("id", encodedId);
        } else {
            this.setMessage(ActionResult.WARNING, "digitalmedia.error.file_could_not_be_saved");
        }
    }

    private BiblivreFile _tryFetchingDBFileWithEncoding(
            String id, String fileId, String fileName, Charset charset) {
        String decodedId = new String(Base64.getDecoder().decode(id), charset);

        String[] splitId = decodedId.split(":");
        if (splitId.length == 2 && StringUtils.isNumeric(splitId[0])) {
            fileId = splitId[0];
            fileName = splitId[1];
        }

        if (!StringUtils.isNumeric(fileId) || StringUtils.isBlank(fileName)) {
            this.setReturnCode(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return digitalMediaBO.load(Integer.parseInt(fileId), fileName);
    }

    private BiblivreFile _tryFetchingDBFileWithWindowsEncoding(
            String id, String fileId, String fileName) {
        return _tryFetchingDBFileWithEncoding(id, fileId, fileName, Constants.WINDOWS_CHARSET);
    }

    @Autowired
    public void setDigitalMediaBO(DigitalMediaBO digitalMediaBO) {
        this.digitalMediaBO = digitalMediaBO;
    }
}
