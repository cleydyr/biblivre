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
import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserStatus;
import biblivre.core.AbstractBO;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.CalendarUtils;
import biblivre.core.utils.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationBO extends AbstractBO {
    private ReservationDAO reservationDAO;
    private UserBO userBO;
    private BiblioRecordBO biblioRecordBO;
    private UserTypeBO userTypeBO;
    private IndexingGroupBO indexingGroupBO;
    private ConfigurationBO configurationBO;

    public boolean deleteExpired() {
        return this.reservationDAO.deleteExpired();
    }

    public ReservationDTO get(Integer id) {
        return this.reservationDAO.get(id);
    }

    public List<ReservationDTO> get(RecordDTO record) {
        return this.reservationDAO.list(
                null, record, indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));
    }

    public int countReserved(RecordDTO record) {
        return this.reservationDAO.count(null, record);
    }

    public int countReserved(UserDTO user) {
        return this.reservationDAO.count(user, null);
    }

    public List<Integer> listReservedRecordIds(UserDTO user) {
        List<Integer> reservedRecords = new ArrayList<>();
        List<ReservationDTO> list =
                this.reservationDAO.list(
                        user, null, indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));

        for (ReservationDTO dto : list) {
            reservedRecords.add(dto.getRecordId());
        }

        return reservedRecords;
    }

    public List<ReservationDTO> list(UserDTO user) {
        List<ReservationDTO> list =
                this.reservationDAO.list(
                        user, null, indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));

        for (ReservationDTO dto : list) {
            BiblioRecordDTO record =
                    (BiblioRecordDTO) biblioRecordBO.get(dto.getRecordId(), RecordBO.MARC_INFO);

            dto.setTitle(record.getTitle());
            dto.setAuthor(record.getAuthor());
        }

        return list;
    }

    public List<ReservationInfoDTO> listReservationInfo(UserDTO user) {
        List<ReservationDTO> list =
                this.reservationDAO.list(
                        user, null, indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));
        List<ReservationInfoDTO> result = new ArrayList<>();

        for (ReservationDTO dto : list) {
            ReservationInfoDTO info = new ReservationInfoDTO();
            info.setReservation(dto);

            BiblioRecordDTO record =
                    (BiblioRecordDTO) biblioRecordBO.get(dto.getRecordId(), RecordBO.MARC_INFO);
            info.setBiblio(record);

            result.add(info);
        }

        return result;
    }

    public List<ReservationInfoDTO> list() {
        List<ReservationDTO> list =
                this.reservationDAO.list(
                        indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));
        List<ReservationInfoDTO> result = new ArrayList<>();

        for (ReservationDTO dto : list) {
            ReservationInfoDTO info = new ReservationInfoDTO();

            BiblioRecordDTO record =
                    (BiblioRecordDTO) biblioRecordBO.get(dto.getRecordId(), RecordBO.MARC_INFO);
            info.setBiblio(record);

            dto.setTitle(record.getTitle());
            dto.setAuthor(record.getAuthor());
            info.setReservation(dto);

            UserDTO user = userBO.get(dto.getUserId());
            info.setUser(user);
        }

        return result;
    }

    public boolean delete(Integer id) {
        return this.reservationDAO.delete(id);
    }

    public void delete(Integer userId, Integer recordId) {
        this.reservationDAO.delete(userId, recordId);
    }

    public void checkReservation(RecordDTO record, UserDTO user) {

        // User cannot be blocked
        if (UserStatus.BLOCKED.equals(user.getStatus())
                || UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new ValidationException("cataloging.reservation.error.blocked_user");
        }

        // The lending limit (total number of materials that can be lent to a
        // specific user) must be preserved
        if (!this.checkUserReservationLimit(user)) {
            throw new ValidationException("cataloging.reservation.error.limit_exceeded");
        }
    }

    public boolean checkUserReservationLimit(UserDTO user) {
        UserTypeDTO type = userTypeBO.get(user.getType());

        int limit = (type != null) ? type.getReservationLimit() : 1;
        int count = this.reservationDAO.count(user, null);

        return count < limit;
    }

    public int reserve(RecordDTO record, UserDTO user, int createdBy) {
        this.checkReservation(record, user);

        ReservationDTO reservation = new ReservationDTO();
        reservation.setRecordId(record.getId());
        reservation.setUserId(user.getId());

        UserTypeDTO type = userTypeBO.get(user.getType());

        Date today = new Date();
        int days = (type != null) ? type.getReservationTimeLimit() : 7;
        Date expires =
                CalendarUtils.calculateExpectedReturnDate(
                        today,
                        days,
                        configurationBO.getIntArray(Constants.CONFIG_BUSINESS_DAYS, "2,3,4,5,6"));

        reservation.setExpires(expires);

        return this.reservationDAO.insert(reservation);
    }

    public void populateReservationInfoByBiblio(SearchDTO search) {
        Set<Integer> users = new HashSet<>();
        Set<Integer> records = new HashSet<>();

        for (RecordDTO record : search) {
            records.add(record.getId());
        }

        Map<Integer, List<ReservationDTO>> reservationsMap = this.getReservationsMap(records);
        for (Entry<Integer, List<ReservationDTO>> entry : reservationsMap.entrySet()) {
            for (ReservationDTO dto : entry.getValue()) {
                Integer userid = dto.getUserId();
                if (userid != null) {
                    users.add(userid);
                }
            }
        }

        Map<Integer, UserDTO> usersMap = new HashMap<>();
        if (!users.isEmpty()) {
            usersMap = userBO.map(users);
        }

        // Join data
        for (RecordDTO record : search) {
            List<ReservationDTO> reservations = reservationsMap.get(record.getId());

            if (reservations == null) {
                continue;
            }

            DTOCollection<ReservationInfoDTO> infoList = new DTOCollection<>();
            for (ReservationDTO reservation : reservations) {
                ReservationInfoDTO info = new ReservationInfoDTO();

                info.setReservation(reservation);
                if (reservation.getUserId() != null) {
                    info.setUser(usersMap.get(reservation.getUserId()));
                }

                infoList.add(info);
            }
            record.addExtraData("reservationInfo", infoList);
        }
    }

    public Map<Integer, List<ReservationDTO>> getReservationsMap(Set<Integer> recordIds) {
        return this.reservationDAO.getReservationsMap(recordIds);
    }

    @Autowired
    public void setReservationDAO(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
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
    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }

    @Autowired
    public void setIndexingGroupBO(IndexingGroupBO indexingGroupBO) {
        this.indexingGroupBO = indexingGroupBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
