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
package biblivre.circulation.reservation;

import biblivre.administration.indexing.IndexingGroupBO;
import biblivre.administration.indexing.IndexingGroupDTO;
import biblivre.cataloging.HttpRequestSearchHelper;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.circulation.user.PagedUserSearchWebHelper;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserSearchDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.enums.ActionResult;
import biblivre.search.SearchException;
import java.util.List;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class Handler extends AbstractHandler {
    private UserBO userBO;
    private BiblioRecordBO biblioRecordBO;
    private ReservationBO reservationBO;
    private biblivre.circulation.user.Handler userHandler;
    private IndexingGroupBO indexingGroupBO;
    @Autowired private PagedUserSearchWebHelper pagedUserSearchWebHelper;

    public void search(ExtendedRequest request, ExtendedResponse response) {
        String searchParameters = request.getString("search_parameters");

        SearchQueryDTO searchQuery = new SearchQueryDTO(searchParameters);

        AuthorizationPoints authorizationPoints = request.getAuthorizationPoints();

        RecordType recordType = RecordType.BIBLIO;

        SearchDTO search = biblioRecordBO.search(searchQuery, authorizationPoints);

        if (search.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
        }

        if (CollectionUtils.isEmpty(search)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }

        reservationBO.populateReservationInfoByBiblio(search);

        List<IndexingGroupDTO> groups = indexingGroupBO.getGroups(recordType);

        try {
            put("search", search.toJSONObject());

            for (IndexingGroupDTO group : groups) {
                accumulate("indexing_groups", group.toJSONObject());
            }
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        Integer defaultSortableGroupId =
                indexingGroupBO.getDefaultSortableGroupId(biblioRecordBO.getRecordType());

        SearchDTO search =
                HttpRequestSearchHelper.paginate(request, biblioRecordBO, defaultSortableGroupId);

        if (search == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");

            return;
        }

        biblioRecordBO.paginateSearch(search, request.getAuthorizationPoints());

        if (CollectionUtils.isEmpty(search)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }

        reservationBO.populateReservationInfoByBiblio(search);

        put("search", search.toJSONObject());

        List<IndexingGroupDTO> groups = indexingGroupBO.getGroups(RecordType.BIBLIO);

        for (IndexingGroupDTO group : groups) {
            accumulate("indexing_groups", group.toJSONObject());
        }
    }

    public void userSearch(ExtendedRequest request, ExtendedResponse response) {
        var pagedSearchDTO = pagedUserSearchWebHelper.getPagedUserSearchDTO(request);

        try {
            DTOCollection<UserDTO> userList = userBO.search(pagedSearchDTO);

            if (userList.isEmpty()) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }

            DTOCollection<ReservationListDTO> list = new DTOCollection<>();
            list.setPaging(userList.getPaging());

            for (UserDTO user : userList) {
                list.add(this.populateReservationList(user));
            }

            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    private ReservationListDTO populateReservationList(UserDTO user) {
        List<ReservationInfoDTO> infos = reservationBO.listReservationInfo(user);

        ReservationListDTO reservationList = new ReservationListDTO();
        reservationList.setUser(user);
        reservationList.setId(user.getId());
        reservationList.setReservationInfoList(infos);
        return reservationList;
    }

    public void reserve(ExtendedRequest request, ExtendedResponse response) {
        int recordId = request.getInteger("record_id");
        int userId = request.getInteger("user_id");

        BiblioRecordDTO record = (BiblioRecordDTO) biblioRecordBO.get(recordId, RecordBO.MARC_INFO);

        UserDTO user = userBO.get(userId);

        int reservationId = reservationBO.reserve(record, user, request.getLoggedUserId());

        if (reservationId > 0) {
            this.setMessage(ActionResult.SUCCESS, "circulation.reservation.reserve_success");

            ReservationDTO reservation = reservationBO.get(reservationId);
            ReservationInfoDTO info = new ReservationInfoDTO();
            info.setReservation(reservation);
            info.setBiblio(record);
            info.setUser(user);

            try {
                put("data", info.toJSONObject());
                put("full_data", true);
            } catch (JSONException e) {
                this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
            }

        } else {
            this.setMessage(ActionResult.WARNING, "circulation.reservation.reserve_failure");
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        int reserveId = request.getInteger("id");

        boolean success = reservationBO.delete(reserveId);

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "circulation.reservation.delete_success");
        } else {
            this.setMessage(ActionResult.WARNING, "circulation.reservation.delete_failure");
        }
    }

    public void selfSearch(ExtendedRequest request, ExtendedResponse response) {

        String searchParameters = request.getString("search_parameters");

        SearchQueryDTO searchQuery = new SearchQueryDTO(searchParameters);

        AuthorizationPoints authorizationPoints = request.getAuthorizationPoints();

        SearchDTO search = biblioRecordBO.search(searchQuery, authorizationPoints);

        if (search.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
        }

        if (CollectionUtils.isEmpty(search)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }

        List<IndexingGroupDTO> groups = indexingGroupBO.getGroups(RecordType.BIBLIO);

        try {
            put("search", search.toJSONObject());

            for (IndexingGroupDTO group : groups) {
                accumulate("indexing_groups", group.toJSONObject());
            }
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void selfReserve(ExtendedRequest request, ExtendedResponse response) {

        try {
            int userId = request.getInteger("user_id");
            int loggedUser = request.getLoggedUserId();
            int dbUserId = userBO.getUserByLoginId(loggedUser).getId();

            if (userId != dbUserId) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }
        } catch (NumberFormatException nfe) {
            this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
            return;
        }

        this.reserve(request, response);
    }

    public void selfOpen(ExtendedRequest request, ExtendedResponse response) {

        String searchParameters = request.getString("search_parameters");
        UserSearchDTO searchDto = new UserSearchDTO(searchParameters);

        try {
            int userId = Integer.parseInt(searchDto.getQuery());
            int loggedUser = request.getLoggedUserId();
            UserDTO readerUser = userBO.getUserByLoginId(loggedUser);

            if (readerUser == null) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }

            int dbUserId = readerUser.getId();

            if (userId != dbUserId) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }
        } catch (NumberFormatException nfe) {
            this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
            return;
        }

        var pagedSearchDTO = pagedUserSearchWebHelper.getPagedUserSearchDTO(request);

        try {
            DTOCollection<UserDTO> userList = userBO.search(pagedSearchDTO);

            if (userList.isEmpty()) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }

            DTOCollection<ReservationListDTO> list = new DTOCollection<>();
            list.setPaging(userList.getPaging());

            for (UserDTO user : userList) {
                list.add(this.populateReservationList(user));
            }

            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    public void selfDelete(ExtendedRequest request, ExtendedResponse response) {
        int reservationId = request.getInteger("id");
        int loggedUser = request.getLoggedUserId();

        ReservationDTO reservationDto = reservationBO.get(reservationId);

        if (reservationDto == null) {
            this.setMessage(ActionResult.WARNING, "circulation.reservation.delete_failure");
            return;
        }

        int userId = reservationDto.getUserId();

        if (userId != loggedUser) {
            this.setMessage(ActionResult.WARNING, "circulation.reservation.delete_failure");
            return;
        }

        this.delete(request, response);
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    @Autowired
    public void setReservationBO(ReservationBO reservationBO) {
        this.reservationBO = reservationBO;
    }

    @Autowired
    public void setUserHandler(biblivre.circulation.user.Handler userHandler) {
        this.userHandler = userHandler;
    }

    @Autowired
    public void setIndexingGroupBO(IndexingGroupBO indexingGroupBO) {
        this.indexingGroupBO = indexingGroupBO;
    }
}
