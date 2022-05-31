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
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.legacy.entity.AccessCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component("biblivre.administration.accesscards.Handler")
@Scope("prototype")
public class Handler extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private AccessCardBO accessCardBO;

    private ObjectMapper jacksonObjectMapper;

    @Autowired
    public Handler(AccessCardBO accessCardBO, ObjectMapper jacksonObjectMapper) {
        this.accessCardBO = accessCardBO;

        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void search(ExtendedRequest request, ExtendedResponse response) {
        Page<AccessCard> page = this.searchHelper(request, response, this);

        try {
            this.json.put("search", toJSONObject(page));
        } catch (JSONException jsonException) {
            logger.error("Couldn't parse json object", jsonException);

            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        } catch (JsonProcessingException jsonProcessingException) {
            logger.error("Couldn't desserialize json object", jsonProcessingException);
        }
    }

    public Page<AccessCard> searchHelper(
            ExtendedRequest request, ExtendedResponse response, AbstractHandler handler) {
        String searchParameters = request.getString("search_parameters");

        String query = null;

        Optional<AccessCardStatus> oStatus = null;

        try {
            JSONObject json = new JSONObject(searchParameters);

            query = json.optString("query");

            oStatus = AccessCardStatus.fromString(json.optString("status"));
        } catch (JSONException jsonException) {
            logger.error("Couldn't parse json object", jsonException);

            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");

            return null;
        }

        Integer limit =
                request.getInteger(
                        "limit", Configurations.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));

        Integer offset = (request.getInteger("page", 1) - 1) * limit;

        Page<AccessCard> page = accessCardBO.search(query, oStatus.orElse(null), limit, offset);

        if (page.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.no_card_found");
        }

        return page;
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        this.search(request, response);
    }

    public void open(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        Optional<AccessCard> oCard = accessCardBO.get(id);

        if (oCard.isEmpty()) {
            this.setMessage(
                    ActionResult.WARNING, "administration.accesscards.error.card_not_found");
        }

        AccessCard card = oCard.get();

        try {
            this.json.put("card", toJSONObject(card));
        } catch (JSONException | JsonProcessingException exception) {
            logger.error("Couldn't parse json object", exception);

            this.setMessage(ActionResult.WARNING, "error.invalid_json");
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

        boolean success = false;

        AccessCard accessCard = null;

        if (StringUtils.isNotBlank(code)) {
            accessCard = this.createCard(request, code, status);

            success = accessCardBO.save(accessCard);
        } else {
            Collection<AccessCard> cards =
                    accessCardBO.saveCardList(
                            prefix, suffix, start, end, request.getLoggedUserId(), status);

            if (cards != null) {
                accessCard = cards.stream().findFirst().get();

                success = true;
            } else {
                success = false;
            }
        }

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "administration.accesscards.success.save");
        } else {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.save");
        }

        try {
            this.json.put("data", toJSONObject(accessCard));

            this.json.put("full_data", true);
        } catch (JSONException | JsonProcessingException exception) {
            logger.error("Couldn't parse json object", exception);

            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        if (accessCardBO.removeCard(id)) {
            this.setMessage(ActionResult.SUCCESS, "administration.accesscards.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "administration.accesscards.error.delete");
        }
    }

    public void changeStatus(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        AccessCardStatus status = request.getEnum(AccessCardStatus.class, "status");

        Optional<AccessCard> oAccessCard = accessCardBO.get(id);

        if (!oAccessCard.isPresent()) {
            this.setMessage(
                    ActionResult.SUCCESS, "administration.accesscards.errro.no.such.access.card");
        }

        AccessCard accessCard = oAccessCard.get();

        accessCard.setModifiedBy(request.getLoggedUserId());

        accessCard.setAcessCardStatus(status);

        if (accessCardBO.update(accessCard)) {
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
            this.json.put("data", toJSONObject(accessCard));

            this.json.put("full_data", true);
        } catch (JSONException | JsonProcessingException exception) {
            logger.error("Couldn't parse json object", exception);

            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

    private JSONObject toJSONObject(Object bean) throws JsonProcessingException {
        return new JSONObject(jacksonObjectMapper.writeValueAsString(bean));
    }

    private final AccessCard createCard(
            ExtendedRequest request, String code, AccessCardStatus status) {
        AccessCard accessCard = new AccessCard();

        LocalDateTime now = LocalDateTime.now();

        int loggedUserId = request.getLoggedUserId();

        accessCard.setCode(code);
        accessCard.setAcessCardStatus(status);
        accessCard.setCreatedBy(loggedUserId);
        accessCard.setCreated(now);
        accessCard.setCreatedBy(loggedUserId);
        accessCard.setModified(now);
        accessCard.setModifiedBy(loggedUserId);

        return accessCard;
    }
}
