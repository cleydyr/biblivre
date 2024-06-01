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

import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.lending.LendingInfoDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.reservation.ReservationInfoDTO;
import biblivre.circulation.reservation.ReservationListDTO;
import biblivre.core.AbstractDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.MemoryFile;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.digitalmedia.DigitalMediaEncodingUtil;
import biblivre.search.SearchException;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private UserBO userBO;
    private LendingBO lendingBO;
    private LendingFineBO lendingFineBO;
    private ReservationBO reservationBO;
    private DigitalMediaBO digitalMediaBO;
    private UserFieldBO userFieldBO;
    private PagedUserSearchWebHelper pagedUserSearchWebHelper;

    public void open(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        UserDTO user = userBO.get(id);

        if (user == null) {
            this.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
            return;
        }

        try {
            put("user", user.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void search(ExtendedRequest request, ExtendedResponse response) {
        try {

            var pagedSearchDTO = pagedUserSearchWebHelper.getPagedUserSearchDTO(request);

            DTOCollection<UserDTO> userList = userBO.search(pagedSearchDTO);

            if (userList.isEmpty()) {
                setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
            }

            put("search", userList.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "circulation.error.search_error");
        }
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        this.search(request, response);
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {

        Integer id = request.getInteger("id");

        UserDTO user;

        if (id != 0) {
            user = userBO.get(id);
            if (user == null) {
                this.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
                return;
            }
        } else {
            user = new UserDTO();
        }

        user.setName(request.getString("name"));
        user.setStatus(request.getEnum(UserStatus.class, "status", UserStatus.ACTIVE));

        user.setType(request.getInteger("type"));

        user.setCreatedBy(request.getLoggedUserId());

        for (UserFieldDTO userField : userFieldBO.getFields()) {
            String key = userField.getKey();
            if (request.hasParameter(key)) {
                user.addField(key, request.getString(key));
            }
        }

        String photoData = request.getString("photo_data");

        if (StringUtils.isNotBlank(photoData)) {
            byte[] arr = Base64.getDecoder().decode(photoData);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arr);

            MemoryFile file =
                    new MemoryFile(
                            user.getName() + ".jpeg",
                            MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            arr.length,
                            byteArrayInputStream);

            Integer serial = digitalMediaBO.save(file);

            String photoId = DigitalMediaEncodingUtil.getEncodedId(serial, file.getName());

            String oldPhotoId = user.getPhotoId();

            if (StringUtils.isNotBlank(photoId)) {
                user.setPhotoId(photoId);

                if (StringUtils.isNotBlank(oldPhotoId)) {
                    String decodedId = new String(Base64.getDecoder().decode(oldPhotoId));
                    String[] splitId = decodedId.split(":");

                    if (splitId.length == 2 && StringUtils.isNumeric(splitId[0])) {
                        // Try to remove the file from Biblivre DB

                        digitalMediaBO.delete(Integer.valueOf(splitId[0]), splitId[1]);
                    }
                }
            }
        }

        try {
            UserDTO saved = userBO.save(user);

            if (saved != null) {
                if (id == 0) {
                    this.setMessage(ActionResult.SUCCESS, "circulation.users.success.saved");
                } else {
                    this.setMessage(ActionResult.SUCCESS, "circulation.users.success.update");
                }

                put("data", saved.toJSONObject());
                put("full_data", true);
            } else {
                this.setMessage(ActionResult.WARNING, "circulation.users.error.saved");
            }
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        UserDTO user = userBO.get(id);

        String action = (user.getStatus() == UserStatus.INACTIVE) ? "delete" : "disable";

        try {
            boolean success = false;

            if ("delete".equals(action)) {
                success = userBO.delete(user);
            } else {
                success = userBO.updateUserStatus(id, UserStatus.INACTIVE);
            }

            if (success) {
                this.setMessage(ActionResult.SUCCESS, "circulation.users.success." + action);
            } else {
                this.setMessage(ActionResult.WARNING, "circulation.users.failure." + action);
            }
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "circulation.users.failure." + action);
        }
    }

    public void loadTabData(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");
        String tab = request.getString("tab");

        UserDTO user = userBO.get(id);

        if (user == null) {
            this.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
            return;
        }

        DTOCollection<AbstractDTO> data = new DTOCollection<>();

        switch (tab) {
            case "lendings" -> {
                Collection<LendingInfoDTO> list =
                        lendingBO.populateLendingInfo(lendingBO.listLendings(user), false);
                list.addAll(lendingBO.populateLendingInfo(lendingBO.listHistory(user), false));
                data.addAll(list);
            }
            case "reservations" -> {
                List<ReservationInfoDTO> infos = reservationBO.listReservationInfo(user);
                ReservationListDTO reservationList = new ReservationListDTO();
                reservationList.setUser(user);
                reservationList.setId(user.getId());
                reservationList.setReservationInfoList(infos);
                data.add(reservationList);
            }
            case "fines" -> {
                List<LendingFineDTO> fines = lendingFineBO.listLendingFines(user);
                data.addAll(fines);
            }
            default -> {
                this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
                return;
            }
        }

        try {
            put("data", data.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void block(ExtendedRequest request, ExtendedResponse response) {
        Integer userId = request.getInteger("user_id");

        try {
            boolean success = userBO.updateUserStatus(userId, UserStatus.BLOCKED);
            if (success) {
                this.setMessage(ActionResult.SUCCESS, "circulation.users.success.block");
            } else {
                this.setMessage(ActionResult.WARNING, "circulation.users.failure.block");
            }
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    public void unblock(ExtendedRequest request, ExtendedResponse response) {
        Integer userId = request.getInteger("user_id");

        try {
            boolean success = userBO.updateUserStatus(userId, UserStatus.ACTIVE);
            if (success) {
                this.setMessage(ActionResult.SUCCESS, "circulation.users.success.unblock");
            } else {
                this.setMessage(ActionResult.WARNING, "circulation.users.failure.unblock");
            }
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
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
    public void setLendingFineBO(LendingFineBO lendingFineBO) {
        this.lendingFineBO = lendingFineBO;
    }

    @Autowired
    public void setReservationBO(ReservationBO reservationBO) {
        this.reservationBO = reservationBO;
    }

    @Autowired
    public void setDigitalMediaBO(DigitalMediaBO digitalMediaBO) {
        this.digitalMediaBO = digitalMediaBO;
    }

    @Autowired
    public void setUserFieldBO(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }

    @Autowired
    public void setPagedUserSearchWebHelper(PagedUserSearchWebHelper pagedUserSearchWebHelper) {
        this.pagedUserSearchWebHelper = pagedUserSearchWebHelper;
    }
}
