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
package biblivre.circulation.user_cards;

import biblivre.circulation.user.UserBO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.labels.print.LabelPrintDTO;
import biblivre.search.SearchException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private UserBO userBO;

    public void createPdf(ExtendedRequest request, ExtendedResponse response) {
        LabelPrintDTO print = getLabelPrintDTO(request);

        if (print == null) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        String printId = UUID.randomUUID().toString();

        request.setScopedSessionAttribute(printId, print);

        try {
            put("uuid", printId);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void downloadPdf(ExtendedRequest request, ExtendedResponse response) {

        String printId = request.getString("id");
        LabelPrintDTO dto = (LabelPrintDTO) request.getScopedSessionAttribute(printId);
        final DiskFile exportFile = userBO.printUserCardsToPDF(dto, request.getTranslationsMap());

        try {
            userBO.markAsPrinted(dto.getIds());
        } catch (SearchException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        this.setFile(exportFile);

        this.setCallback(exportFile::delete);
    }

    private LabelPrintDTO getLabelPrintDTO(ExtendedRequest request) {
        LabelPrintDTO print = new LabelPrintDTO();

        try {
            String idList = request.getString("id_list");
            String[] idArray = idList.split(",");
            Set<Integer> ids = new HashSet<>();
            for (String s : idArray) {
                ids.add(Integer.valueOf(s));
            }
            print.setIds(ids);
            print.setOffset(request.getInteger("offset"));
            print.setWidth(request.getFloat("width"));
            print.setHeight(request.getFloat("height"));
            print.setColumns(request.getInteger("columns"));
            print.setRows(request.getInteger("rows"));

            return print;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }
}
