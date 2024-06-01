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
package biblivre.administration.accesscards;

import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private AccessCardBO accessCardBO;
    @Autowired private PagedAccessCardSearchWebHelper pagedAccessCardSearchWebHelper;

    public void search(ExtendedRequest request, ExtendedResponse response) {
        var pagedAccessCardSearchDTO =
                pagedAccessCardSearchWebHelper.getPagedAccessCardSearchDTO(request);

        DTOCollection<AccessCardDTO> accessCards = accessCardBO.search(pagedAccessCardSearchDTO);

        if (accessCards.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.no_card_found");
        }

        try {
            put("search", accessCards.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        this.search(request, response);
    }

    public void open(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        AccessCardDTO card = accessCardBO.get(id);

        if (card == null) {
            this.setMessage(
                    ActionResult.WARNING, "administration.accesscards.error.card_not_found");
            return;
        }

        try {
            put("card", card.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {
        AccessCardStatus status =
                request.getEnum(AccessCardStatus.class, "status", AccessCardStatus.AVAILABLE);
        String code = request.getString("code");
        String prefix = request.getString("prefix");
        String start = request.getString("start");
        String end = request.getString("end");
        String suffix = request.getString("suffix");

        boolean success;
        AccessCardDTO returnDto = null;

        if (StringUtils.isNotBlank(code)) {
            returnDto = this.createCard(request, code, status);
            success = accessCardBO.save(returnDto);
        } else {
            List<AccessCardDTO> list =
                    accessCardBO.saveCardList(
                            prefix, suffix, start, end, request.getLoggedUserId(), status);
            if (list != null) {
                returnDto = list.get(0);
                success = true;
            } else {
                success = false;
            }
        }

        if (success && returnDto != null) {
            this.setMessage(ActionResult.SUCCESS, "administration.accesscards.success.save");
        } else {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.save");

            return;
        }

        try {
            put("data", returnDto.toJSONObject());
            put("full_data", true);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        AccessCardDTO dto = new AccessCardDTO();
        dto.setId(id);
        dto.setModifiedBy(request.getLoggedUserId());
        if (accessCardBO.removeCard(dto)) {
            this.setMessage(ActionResult.SUCCESS, "administration.accesscards.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.delete");
        }
    }

    public void changeStatus(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");
        AccessCardStatus status = request.getEnum(AccessCardStatus.class, "status");

        AccessCardDTO dto = accessCardBO.get(id);
        dto.setModifiedBy(request.getLoggedUserId());
        dto.setStatus(status);
        if (accessCardBO.update(dto)) {
            if (status == AccessCardStatus.BLOCKED
                    || status == AccessCardStatus.IN_USE_AND_BLOCKED) {
                this.setMessage(ActionResult.SUCCESS, "administration.accesscards.success.block");
            } else {
                this.setMessage(ActionResult.SUCCESS, "administration.accesscards.success.unblock");
            }
        } else {
            if (status == AccessCardStatus.BLOCKED
                    || status == AccessCardStatus.IN_USE_AND_BLOCKED) {
                this.setMessage(ActionResult.WARNING, "administration.accesscards.error.block");
            } else {
                this.setMessage(ActionResult.WARNING, "administration.accesscards.error.unblock");
            }
        }

        try {
            put("data", dto.toJSONObject());
            put("full_data", true);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    private AccessCardDTO createCard(
            ExtendedRequest request, String code, AccessCardStatus status) {
        AccessCardDTO dto = new AccessCardDTO();
        dto.setCode(code);
        dto.setStatus(status);
        dto.setCreatedBy(request.getLoggedUserId());
        return dto;
    }

    @Autowired
    public void setAccessCardBO(AccessCardBO accessCardBO) {
        this.accessCardBO = accessCardBO;
    }
}
