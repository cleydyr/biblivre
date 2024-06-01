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
package biblivre.circulation.lending;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.user.PagedUserSearchWebHelper;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.PagingDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.search.SearchException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class Handler extends AbstractHandler {
    private UserBO userBO;
    private BiblioRecordBO biblioRecordBO;
    private HoldingBO holdingBO;
    private LendingBO lendingBO;
    private UserTypeBO userTypeBO;
    private LendingFineBO lendingFineBO;
    private ReservationBO reservationBO;
    @Autowired private PagedUserSearchWebHelper pagedUserSearchWebHelper;

    public void search(ExtendedRequest request, ExtendedResponse response) {

        String searchParameters = request.getString("search_parameters");
        String query;
        boolean lentOnly;

        try {
            JSONObject json = new JSONObject(searchParameters);
            query = json.optString("query");
            lentOnly = json.optBoolean("holding_list_lendings");
        } catch (JSONException je) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        Integer limit =
                request.getInteger(
                        "limit", configurationBO.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
        int offset = (request.getInteger("page", 1) - 1) * limit;

        DTOCollection<HoldingDTO> holdingList =
                holdingBO.search(query, RecordDatabase.MAIN, lentOnly, offset, limit);

        if (CollectionUtils.isEmpty(holdingList)) {
            this.setMessage(ActionResult.WARNING, "circulation.lending.no_holding_found");
            return;
        }

        DTOCollection<LendingInfoDTO> lendingInfo =
                lendingBO.populateLendingInfoByHolding(holdingList);

        try {
            put("search", lendingInfo.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void userSearch(ExtendedRequest request, ExtendedResponse response) {
        try {
            var pagedSearchDTO = pagedUserSearchWebHelper.getPagedUserSearchDTO(request);

            DTOCollection<UserDTO> userList = userBO.search(pagedSearchDTO);

            if (userList.isEmpty()) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }

            DTOCollection<LendingListDTO> list = new DTOCollection<>();
            list.setPaging(userList.getPaging());

            for (UserDTO user : userList) {
                list.add(this.populateLendingList(user));
            }

            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    private LendingListDTO populateLendingList(UserDTO user) {
        LendingListDTO lendingList = new LendingListDTO();

        lendingList.setUser(user);
        lendingList.setId(user.getId());

        List<LendingDTO> lendings = lendingBO.listUserLendings(user);

        List<LendingInfoDTO> infos = new ArrayList<>();

        for (LendingDTO lending : lendings) {
            HoldingDTO holding =
                    (HoldingDTO) holdingBO.get(lending.getHoldingId(), RecordBO.MARC_INFO);

            BiblioRecordDTO biblio =
                    (BiblioRecordDTO) biblioRecordBO.get(holding.getRecordId(), RecordBO.MARC_INFO);

            LendingInfoDTO info = new LendingInfoDTO();

            info.setLending(lending);
            info.setHolding(holding);
            info.setBiblio(biblio);

            infos.add(info);

            // CHECK FOR LENDING FINES
            Integer daysLate = lendingFineBO.calculateLateDays(lending);
            if (daysLate > 0) {
                UserTypeDTO userType = userTypeBO.get(user.getType());
                Float dailyFine = userType.getFineValue();

                lending.setDaysLate(daysLate);
                lending.setDailyFine(dailyFine);
                lending.setEstimatedFine(dailyFine * daysLate);
            }
        }
        lendingList.setLendingInfo(infos);

        lendingList.setReservedRecords(reservationBO.listReservedRecordIds(user));

        return lendingList;
    }

    public void lend(ExtendedRequest request, ExtendedResponse response) {

        Integer holdingId = request.getInteger("holding_id");
        Integer userId = request.getInteger("user_id");

        HoldingDTO holding = (HoldingDTO) holdingBO.get(holdingId, RecordBO.MARC_INFO);

        UserDTO user = userBO.get(userId);

        boolean success = lendingBO.doLend(holding, user, request.getLoggedUserId());

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "circulation.lending.lend_success");

            BiblioRecordDTO biblio =
                    (BiblioRecordDTO) biblioRecordBO.get(holding.getRecordId(), RecordBO.MARC_INFO);
            LendingDTO lending = lendingBO.getCurrentLending(holding);
            LendingInfoDTO info = new LendingInfoDTO();
            info.setLending(lending);
            info.setHolding(holding);
            info.setBiblio(biblio);
            info.setUser(user);

            try {
                put("data", info.toJSONObject());
                put("full_data", true);
            } catch (JSONException e) {
                this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
            }

        } else {
            this.setMessage(ActionResult.WARNING, "circulation.lending.lend_failure");
        }
    }

    public void renewLending(ExtendedRequest request, ExtendedResponse response) {

        Integer lendingId = request.getInteger("id");

        LendingDTO lending = lendingBO.get(lendingId);

        Integer holdingId = lending.getHoldingId();
        Integer userId = lending.getUserId();

        lending.setCreatedBy(request.getLoggedUserId());
        boolean success = lendingBO.doRenew(lending);

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "circulation.lending.renew_success");

            HoldingDTO holding = (HoldingDTO) holdingBO.get(holdingId, RecordBO.MARC_INFO);
            BiblioRecordDTO biblio =
                    (BiblioRecordDTO) biblioRecordBO.get(holding.getRecordId(), RecordBO.MARC_INFO);
            UserDTO user = userBO.get(userId);

            LendingDTO newLending = lendingBO.getCurrentLending(holding);

            LendingInfoDTO info = new LendingInfoDTO();
            info.setLending(newLending);
            info.setHolding(holding);
            info.setBiblio(biblio);
            info.setUser(user);

            try {
                put("data", info.toJSONObject());
                put("full_data", true);
            } catch (JSONException e) {
                this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
            }

        } else {
            this.setMessage(ActionResult.WARNING, "circulation.lending.renew_failure");
        }
    }

    public void returnLending(ExtendedRequest request, ExtendedResponse response) {
        Integer lendingId = request.getInteger("id");
        Float fineValue = request.getFloat("fine");
        boolean paid = request.getBoolean("paid");

        LendingDTO lending = lendingBO.get(lendingId);
        boolean success = lendingBO.doReturn(lending, fineValue, paid);

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "circulation.lending.return_success");
        } else {
            this.setMessage(ActionResult.WARNING, "circulation.lending.return_failure");
        }
    }

    public void payFine(ExtendedRequest request, ExtendedResponse response) {

        Integer fineId = request.getInteger("fine_id");
        boolean exempt = request.getBoolean("exempt", false);

        LendingFineDTO dto = lendingFineBO.getById(fineId);
        if (exempt) {
            dto.setValue(0f);
        }

        boolean success = lendingFineBO.update(dto);
        if (success) {
            this.setMessage(ActionResult.SUCCESS, "circulation.lending.fine.success_pay_fine");
        } else {
            this.setMessage(ActionResult.WARNING, "circulation.lending.fine.failure_pay_fine");
        }
    }

    public void listAll(ExtendedRequest request, ExtendedResponse response) {

        Integer limit =
                request.getInteger(
                        "limit", configurationBO.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
        Integer offset = request.getInteger("offset", 0);

        DTOCollection<LendingInfoDTO> list = new DTOCollection<>();

        Collection<LendingInfoDTO> lendingInfoList = lendingBO.listLendings(offset, limit);

        list.addAll(lendingInfoList);

        PagingDTO paging = new PagingDTO(lendingBO.countLendings(), limit, offset);
        list.setPaging(paging);

        if (list.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "circulation.lending.no_lending_found");
            return;
        }

        try {
            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void printReceipt(ExtendedRequest request, ExtendedResponse response) {

        String idList = request.getString("id_list");

        String[] idArray = idList.split(",");
        List<Integer> ids = new ArrayList<>();
        try {
            for (String s : idArray) {
                ids.add(Integer.valueOf(s));
            }
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
        }

        try {
            String receipt = lendingBO.generateReceipt(ids, request.getTranslationsMap());

            put("receipt", receipt);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
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
    public void setHoldingBO(HoldingBO holdingBO) {
        this.holdingBO = holdingBO;
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
    public void setLendingFineBO(LendingFineBO lendingFineBO) {
        this.lendingFineBO = lendingFineBO;
    }

    @Autowired
    public void setReservationBO(ReservationBO reservationBO) {
        this.reservationBO = reservationBO;
    }
}
