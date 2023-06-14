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
package biblivre.circulation.user;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.lending.LendingBO;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Validator extends AbstractValidator {
    private AccessControlBO accessControlBO;
    private UserBO userBO;
    private LendingBO lendingBO;
    private UserTypeBO userTypeBO;
    private UserFieldBO userFieldBO;

    public void validateSave(
            AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
        DateFormat dateFormat = new SimpleDateFormat(request.getLocalizedText("format.date"));
        DateFormat dateTimeFormat =
                new SimpleDateFormat(request.getLocalizedText("format.datetime"));

        ValidationException ex = new ValidationException("error.form_invalid_values");

        UserStatus status = request.getEnum(UserStatus.class, "status");
        if (status == null) {
            ex.addError("status", "field.error.required");
        }

        String name = request.getString("name");
        if (StringUtils.isBlank(name)) {
            ex.addError("name", "field.error.required");
        }

        if (name.contains(":")) {
            ex.addError("name", "circulation.error.invalid_user_name");
        }

        Integer type = request.getInteger("type");
        if (userTypeBO.get(type) == null) {
            ex.addError("type", "field.error.required");
        }

        for (UserFieldDTO userField : userFieldBO.getFields()) {
            String key = userField.getKey();
            String param = request.getString(key);

            if (userField.isRequired() && StringUtils.isBlank(param)) {
                ex.addError(key, "field.error.required");
                continue;
            }

            int maxLength = userField.getMaxLength();
            if (maxLength > 0 && param.length() > maxLength) {
                ex.addError(key, "field.error.max_length:::" + maxLength);
                continue;
            }

            switch (userField.getType()) {
                case NUMBER -> {
                    if (StringUtils.isNotBlank(param) && !NumberUtils.isDigits(param)) {
                        ex.addError(key, "field.error.digits_only");
                    }
                }
                case DATE -> {
                    if (StringUtils.isNotBlank(param)) {
                        try {
                            dateFormat.parse(param);
                        } catch (Exception e) {
                            ex.addError(
                                    key,
                                    "field.error.date:::"
                                            + request.getLocalizedText(
                                                    "format.date_user_friendly"));
                        }
                    }
                }
                case DATETIME -> {
                    if (StringUtils.isNotBlank(param)) {
                        try {
                            dateTimeFormat.parse(param);
                        } catch (Exception e) {
                            ex.addError(
                                    key,
                                    "field.error.date:::"
                                            + request.getLocalizedText(
                                                    "format.datetime_user_friendly"));
                        }
                    }
                }
                default -> {}
            }
        }

        if (ex.hasErrors()) {
            handler.setMessage(ex);
        }
    }

    public void validateDelete(
            AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {

        Integer id = request.getInteger("id");

        UserDTO user = userBO.get(id);

        if (user == null) {
            handler.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
            return;
        }

        if (lendingBO.getCurrentLendingsCount(user) > 0) {
            handler.setMessage(ActionResult.WARNING, "circulation.error.delete.user_has_lendings");
            return;
        }

        if (accessControlBO.getByUserId(user.getId()) != null) {
            handler.setMessage(
                    ActionResult.WARNING, "circulation.error.delete.user_has_accesscard");
            return;
        }

        int loginId = user.getLoginId();
        int loggedId = request.getLoggedUserId();
        if (loginId == loggedId) {
            // MESSAGE_FAILED_CANNOT_DELETE_YOU_ARE_USING_THIS_ACCOUNT
            handler.setMessage(
                    ActionResult.WARNING, "circulation.error.you_cannot_delete_yourself");
        }
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
    public void setLendingBO(LendingBO lendingBO) {
        this.lendingBO = lendingBO;
    }

    @Autowired
    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }

    @Autowired
    public void setUserFieldBO(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }
}
