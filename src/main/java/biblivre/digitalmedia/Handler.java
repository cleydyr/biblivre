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
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {

    private DigitalMediaBO digitalMediaBO;

    public void download(ExtendedRequest request, ExtendedResponse response) {

        String id = request.getString("id").replaceAll("_", "\\\\");

        Optional<BiblivreFile> file = digitalMediaBO.parseFromBase64(id);

        if (file.isEmpty()) {
            this.setReturnCode(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        file.ifPresentOrElse(
                f -> {
                    this.setFile(f);

                    this.setCallback(f::close);
                },
                () -> {
                    this.setReturnCode(HttpServletResponse.SC_NOT_FOUND);
                });
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

    @Autowired
    public void setDigitalMediaBO(DigitalMediaBO digitalMediaBO) {
        this.digitalMediaBO = digitalMediaBO;
    }
}
