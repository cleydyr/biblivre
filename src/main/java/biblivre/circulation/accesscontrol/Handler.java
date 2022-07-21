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
package biblivre.circulation.accesscontrol;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import java.util.Date;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("biblivre.circulation.accesscontrol.Handler")
public class Handler extends AbstractHandler {
    private AccessCardBO accessCardBO;
    private AccessControlBO accessControlBO;
    private UserBO userBO;
    private biblivre.circulation.user.Handler userHandler;
    private biblivre.administration.accesscards.Handler cardHandler;

    public void userSearch(ExtendedRequest request, ExtendedResponse response) {
        DTOCollection<UserDTO> userList = userHandler.searchHelper(request, response, this);

        if (userList == null) {
            return;
        }

        DTOCollection<AccessControlDTO> list = new DTOCollection<>();
        list.setPaging(userList.getPaging());

        for (UserDTO user : userList) {
            AccessControlDTO dto = accessControlBO.getByUserId(user.getId());
            if (dto == null) {
                dto = new AccessControlDTO();
                dto.setUserId(user.getId());
            }

            dto.setId(user.getId());
            dto.setUser(user);

            if (dto.getAccessCardId() != null) {
                dto.setAccessCard(accessCardBO.get(dto.getAccessCardId()));
            }

            list.add(dto);
        }

        if (list.size() == 0) {
            this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
            return;
        }

        try {
            this.json.put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

    public void cardSearch(ExtendedRequest request, ExtendedResponse response) {
        DTOCollection<AccessCardDTO> cardList = cardHandler.searchHelper(request, response, this);

        if (cardList == null) {
            return;
        }

        DTOCollection<AccessControlDTO> list = new DTOCollection<>();
        list.setPaging(cardList.getPaging());

        for (AccessCardDTO card : cardList) {
            AccessControlDTO dto = accessControlBO.getByCardId(card.getId());
            if (dto == null) {
                dto = new AccessControlDTO();
                dto.setAccessCardId(card.getId());
            }

            dto.setId(card.getId());
            dto.setAccessCard(card);

            if (dto.getUserId() != null) {
                dto.setUser(userBO.get(dto.getUserId()));
            }

            list.add(dto);
        }

        if (list.size() == 0) {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.no_card_found");
            return;
        }

        try {
            this.json.put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

    public void bind(ExtendedRequest request, ExtendedResponse response) {
        Integer cardId = request.getInteger("card_id");
        Integer userId = request.getInteger("user_id");

        AccessControlDTO dto = new AccessControlDTO();
        dto.setAccessCardId(cardId);
        dto.setUserId(userId);
        dto.setCreatedBy(request.getLoggedUserId());
        dto.setArrivalTime(new Date());

        if (accessControlBO.lendCard(dto)) {
            this.setMessage(ActionResult.SUCCESS, "circulation.accesscards.lend.success");
            try {
                dto = accessControlBO.getByCardId(cardId);
                accessControlBO.populateDetails(dto);
                dto.setId(cardId);
                this.json.put("data", dto.toJSONObject());
                this.json.put("full_data", true);
            } catch (JSONException e) {
                this.setMessage(ActionResult.WARNING, "error.invalid_json");
                return;
            }
        } else {
            this.setMessage(ActionResult.WARNING, "circulation.accesscards.lend.error");
        }
    }

    public void unbind(ExtendedRequest request, ExtendedResponse response) {
        Integer cardId = request.getInteger("card_id");
        Integer userId = request.getInteger("user_id");

        AccessControlDTO dto = new AccessControlDTO();
        dto.setAccessCardId(cardId);
        dto.setUserId(userId);
        dto.setModifiedBy(request.getLoggedUserId());
        dto.setDepartureTime(new Date());

        if (accessControlBO.returnCard(dto)) {
            this.setMessage(ActionResult.SUCCESS, "circulation.accesscards.return.success");
            try {
                accessControlBO.populateDetails(dto);
                dto.setId(cardId);
                this.json.put("data", dto.toJSONObject());
                this.json.put("full_data", true);
            } catch (JSONException e) {
                this.setMessage(ActionResult.WARNING, "error.invalid_json");
                return;
            }
        } else {
            this.setMessage(ActionResult.WARNING, "circulation.accesscards.return.error");
        }
    }

    @Autowired
    public void setAccessCardBO(AccessCardBO accessCardBO) {
        this.accessCardBO = accessCardBO;
    }

    @Autowired
    public void setAccessControlBO(AccessControlBO accessControlBO) {
        this.accessControlBO = accessControlBO;
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setUserHandler(biblivre.circulation.user.Handler userHandler) {
        this.userHandler = userHandler;
    }

    @Autowired
    public void setCardHandler(biblivre.administration.accesscards.Handler cardHandler) {
        this.cardHandler = cardHandler;
    }
}
