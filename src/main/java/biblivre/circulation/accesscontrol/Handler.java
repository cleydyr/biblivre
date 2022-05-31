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
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.PagingDTO;
import biblivre.core.enums.ActionResult;
import biblivre.legacy.entity.AccessCard;
import biblivre.spring.SpringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component("biblivre.circulation.accesscontrol.Handler")
public class Handler extends AbstractHandler {
    private AccessCardBO accessCardBO;
    private AccessControlBO accessControlBO;
    private UserBO userBO;

    @Autowired
    public Handler(
            ObjectMapper objectMapper,
            AccessCardBO accessCardBO,
            AccessControlBO accessControlBO,
            UserBO userBO) {
        super(objectMapper);
        this.accessCardBO = accessCardBO;
        this.accessControlBO = accessControlBO;
        this.userBO = userBO;
    }

    public void userSearch(ExtendedRequest request, ExtendedResponse response) {
        WebApplicationContext applicationContext = SpringUtils.getWebApplicationContext(request);

        biblivre.circulation.user.Handler userHandler =
                applicationContext.getBean(biblivre.circulation.user.Handler.class);

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
                dto.setAccessCard(accessCardBO.get(dto.getAccessCardId()).get());
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
        WebApplicationContext applicationContext = SpringUtils.getWebApplicationContext(request);

        biblivre.administration.accesscards.Handler cardHandler =
                applicationContext.getBean(biblivre.administration.accesscards.Handler.class);

        Page<AccessCard> accessCards = cardHandler.searchHelper(request, response, this);

        if (accessCards == null) {
            return;
        }

        DTOCollection<AccessControlDTO> list = new DTOCollection<>();

        int recordCount = (int) accessCards.getTotalElements();

        int pageCount = accessCards.getTotalPages();

        int recordsPerPage = recordCount / pageCount;

        int recordOffset = recordsPerPage * accessCards.getNumber();

        list.setPaging(new PagingDTO(recordCount, recordsPerPage, recordOffset));

        for (AccessCard card : accessCards) {
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
            this.json.put("search", list.toJSONObject(objectMapper));
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
}
