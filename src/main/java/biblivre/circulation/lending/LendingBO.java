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
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserStatus;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.enums.PrinterType;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.CalendarUtils;
import biblivre.core.utils.CharPool;
import biblivre.core.utils.Constants;
import biblivre.core.utils.StringPool;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class LendingBO {
    private UserBO userBO;
    private HoldingBO holdingBO;
    private BiblioRecordBO biblioRecordBO;
    private UserTypeBO userTypeBO;
    private LendingFineBO lendingFineBO;
    private ReservationBO reservationBO;
    private LendingDAO lendingDAO;
    private ITemplateEngine templateEngine;
    private ConfigurationBO configurationBO;

    public LendingDTO get(Integer lendingId) {
        return this.lendingDAO.get(lendingId);
    }

    public boolean isLent(HoldingDTO holding) {
        return lendingDAO.getCurrentLending(holding) != null;
    }

    public boolean wasEverLent(HoldingDTO holding) {
        List<LendingDTO> history = this.lendingDAO.listHistory(holding);
        return !history.isEmpty();
    }

    public Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> ids) {
        return this.lendingDAO.getCurrentLendingMap(ids);
    }

    public void checkLending(HoldingDTO holding, UserDTO user) {

        // User cannot be blocked
        if (UserStatus.BLOCKED.equals(user.getStatus())
                || UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new ValidationException("cataloging.lending.error.blocked_user");
        }

        // The material must be available
        if (!HoldingAvailability.AVAILABLE.equals(holding.getAvailability())) {
            throw new ValidationException("cataloging.lending.error.holding_unavailable");
        }

        // The material can't be already lent
        if (this.isLent(holding)) {
            throw new ValidationException("cataloging.lending.error.holding_is_lent");
        }

        // The lending limit (total number of materials that can be lent to a
        // specific user) must be preserved
        if (this.isUserLendingsBeyondLimit(user, false)) {
            throw new ValidationException("cataloging.lending.error.limit_exceeded");
        }
    }

    public void checkRenew(HoldingDTO holding, UserDTO user) {
        // User cannot be blocked
        if (UserStatus.BLOCKED.equals(user.getStatus())
                || UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new ValidationException("cataloging.lending.error.blocked_user");
        }

        // The material must be available
        if (!HoldingAvailability.AVAILABLE.equals(holding.getAvailability())) {
            throw new ValidationException("cataloging.lending.error.holding_unavailable");
        }

        // The lending limit (total number of materials that can be lent to a
        // specific user) must be preserved
        if (this.isUserLendingsBeyondLimit(user, true)) {
            throw new ValidationException("cataloging.lending.error.limit_exceeded");
        }
    }

    public boolean isUserLendingsBeyondLimit(UserDTO user, boolean renew) {
        UserTypeDTO type = userTypeBO.get(user.getType());
        Integer lendingLimit = (type != null) ? type.getLendingLimit() : 1;
        Integer count = this.lendingDAO.getCurrentLendingsCount(user);

        return renew ? (count > lendingLimit) : (count >= lendingLimit);
    }

    public Integer getCurrentLendingsCount(UserDTO user) {
        return this.lendingDAO.getCurrentLendingsCount(user);
    }

    public boolean doLend(HoldingDTO holding, UserDTO user, int createdBy) {
        this.checkLending(holding, user);

        LendingDTO lending = new LendingDTO();
        lending.setHoldingId(holding.getId());
        lending.setUserId(user.getId());

        UserTypeDTO type = userTypeBO.get(user.getType());

        Date today = new Date();
        int days = (type != null) ? type.getLendingTimeLimit() : 7;

        Date expectedReturnDate =
                CalendarUtils.calculateExpectedReturnDate(
                        today,
                        days,
                        configurationBO.getIntArray(Constants.CONFIG_BUSINESS_DAYS, "2,3,4,5,6"));
        lending.setExpectedReturnDate(expectedReturnDate);

        if (this.lendingDAO.doLend(lending)) {
            reservationBO.delete(user.getId(), holding.getRecordId());
            return true;
        } else {
            return false;
        }
    }

    public boolean doReturn(LendingDTO lending, Float fineValue, boolean paid) {
        this.lendingDAO.doReturn(lending.getId());

        if (fineValue > 0) {
            lendingFineBO.createFine(lending, fineValue, paid);
        }

        return true;
    }

    public boolean doRenew(LendingDTO lending) {
        UserDTO userDto = userBO.get(lending.getUserId());
        if (userDto == null) {
            throw new ValidationException("cataloging.lending.error.user_not_found");
        }

        HoldingDTO holding = (HoldingDTO) holdingBO.get(lending.getHoldingId());
        this.checkRenew(holding, userDto);

        UserTypeDTO type = userTypeBO.get(userDto.getType());

        Date today = new Date();
        int days = (type != null) ? type.getLendingTimeLimit() : 7;

        Date expectedReturnDate =
                CalendarUtils.calculateExpectedReturnDate(
                        today,
                        days,
                        configurationBO.getIntArray(Constants.CONFIG_BUSINESS_DAYS, "2,3,4,5,6"));

        lending.setExpectedReturnDate(expectedReturnDate);

        return this.lendingDAO.doRenew(
                lending.getId(), lending.getExpectedReturnDate(), lending.getCreatedBy());
    }

    public Collection<LendingDTO> listHistory(UserDTO user) {
        List<LendingDTO> list = this.lendingDAO.listHistory(user);
        DTOCollection<LendingDTO> collection = new DTOCollection<>();
        collection.addAll(list);
        return collection;
    }

    public DTOCollection<LendingDTO> listLendings(UserDTO user) {
        List<LendingDTO> list = this.lendingDAO.listLendings(user);
        DTOCollection<LendingDTO> collection = new DTOCollection<>();
        collection.addAll(list);
        return collection;
    }

    public List<LendingDTO> listUserLendings(UserDTO user) {
        return this.lendingDAO.listLendings(user);
    }

    public Collection<LendingInfoDTO> listLendings(int offset, int limit) {
        List<LendingDTO> list = this.lendingDAO.listLendings(offset, limit);
        return this.populateLendingInfo(list);
    }

    public Integer countHistory(UserDTO user) {
        return this.lendingDAO.countHistory(user);
    }

    public Integer countLendings(UserDTO user) {
        return this.lendingDAO.countLendings(user);
    }

    public DTOCollection<LendingInfoDTO> populateLendingInfoByHolding(
            DTOCollection<HoldingDTO> holdingList) {
        Set<Integer> users = new HashSet<>();
        Set<Integer> records = new HashSet<>();
        Set<Integer> holdings = new HashSet<>();

        for (HoldingDTO holding : holdingList) {
            holdings.add(holding.getId());

            Integer recordId = holding.getRecordId();
            if (recordId != null) {
                records.add(recordId);
            }
        }

        Map<Integer, LendingDTO> lendingsMap = this.getCurrentLendingMap(holdings);
        for (Entry<Integer, LendingDTO> entry : lendingsMap.entrySet()) {
            Integer userid = entry.getValue().getUserId();
            if (userid != null) {
                users.add(userid);
            }
        }

        Map<Integer, RecordDTO> recordsMap = new HashMap<>();
        if (!records.isEmpty()) {
            recordsMap = biblioRecordBO.map(records, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
        }

        Map<Integer, UserDTO> usersMap = new HashMap<>();
        if (!users.isEmpty()) {
            usersMap = userBO.map(users);
        }

        // Join data
        DTOCollection<LendingInfoDTO> collection = new DTOCollection<>();
        collection.setPaging(holdingList.getPaging());

        for (HoldingDTO holding : holdingList) {
            LendingInfoDTO info = new LendingInfoDTO();

            Integer holdingId = holding.getId();
            Integer recordId = holding.getRecordId();

            info.setHolding(holding);

            if (recordId != null) {
                info.setBiblio((BiblioRecordDTO) recordsMap.get(recordId));
            }

            LendingDTO lending = lendingsMap.get(holdingId);
            if (lending != null) {
                info.setLending(lending);

                if (lending.getUserId() != null) {
                    UserDTO user = usersMap.get(lending.getUserId());
                    UserTypeDTO userType = userTypeBO.get(user.getType());
                    user.setUsertypeName(userType.getName());
                    info.setUser(user);

                    Integer daysLate = lendingFineBO.calculateLateDays(lending);
                    if (daysLate > 0) {
                        Float dailyFine = userType.getFineValue();
                        lending.setDaysLate(daysLate);
                        lending.setDailyFine(dailyFine);
                        lending.setEstimatedFine(dailyFine * daysLate);
                    }
                }
            }

            collection.add(info);
        }

        return collection;
    }

    public Collection<LendingInfoDTO> populateLendingInfo(Collection<LendingDTO> list) {
        return this.populateLendingInfo(list, true);
    }

    public Collection<LendingInfoDTO> populateLendingInfo(
            Collection<LendingDTO> list, boolean populateUser) {

        DTOCollection<LendingInfoDTO> collection = new DTOCollection<>();

        Set<Integer> userIds = new HashSet<>();
        Set<Integer> holdingIds = new HashSet<>();
        Set<Integer> recordIds = new HashSet<>();

        for (LendingDTO lending : list) {
            if (populateUser && lending.getUserId() != null) {
                userIds.add(lending.getUserId());
            }

            if (lending.getHoldingId() != null) {
                holdingIds.add(lending.getHoldingId());
            }
        }

        Map<Integer, UserDTO> users = new HashMap<>();
        if (!userIds.isEmpty()) {
            users = userBO.map(userIds);
        }

        Map<Integer, RecordDTO> holdings = new HashMap<>();
        if (!holdingIds.isEmpty()) {
            holdings = holdingBO.map(holdingIds);
        }

        for (RecordDTO holding : holdings.values()) {
            recordIds.add(((HoldingDTO) holding).getRecordId());
        }

        Map<Integer, RecordDTO> records = new HashMap<>();
        if (!recordIds.isEmpty()) {
            records = biblioRecordBO.map(recordIds, RecordBO.MARC_INFO);
        }

        for (LendingDTO lending : list) {
            UserDTO user = users.get(lending.getUserId());
            HoldingDTO holding = (HoldingDTO) holdings.get(lending.getHoldingId());
            BiblioRecordDTO record =
                    (holding != null) ? (BiblioRecordDTO) records.get(holding.getRecordId()) : null;

            LendingInfoDTO info = new LendingInfoDTO();
            info.setLending(lending);
            info.setUser(user);
            info.setHolding(holding);
            info.setBiblio(record);

            collection.add(info);
        }

        return collection;
    }

    public LendingDTO getLatest(int holdingSerial, int userId) {
        return this.lendingDAO.getLatest(holdingSerial, userId);
    }

    public Integer countLendings() {
        return this.lendingDAO.countLendings();
    }

    public Collection<LendingDTO> listLendings(Collection<Integer> lendingsIds) {
        Collection<LendingDTO> lendingList = new ArrayList<>();

        for (Integer id : lendingsIds) {
            lendingList.add(this.get(id));
        }

        return lendingList;
    }

    public String generateReceipt(List<Integer> lendingsIds, TranslationsMap i18n) {
        PrinterType printerType =
                PrinterType.fromString(
                        configurationBO.getString(Constants.CONFIG_LENDING_PRINTER_TYPE));
        int columns = 24;

        if (printerType != null) {
            switch (printerType) {
                case PRINTER_40_COLUMNS -> columns = 40;
                case PRINTER_80_COLUMNS -> columns = 80;
                case PRINTER_COMMON -> columns = 0;
                default -> {}
            }
        }

        if (columns == 0) {
            return this.generateTableReceipt(lendingsIds, i18n);
        } else {
            return this.generateTxtReceipt(lendingsIds, i18n, columns);
        }
    }

    private String generateTxtReceipt(
            List<Integer> lendingsIds, TranslationsMap i18n, int columns) {
        DateFormat receiptDateFormat = new SimpleDateFormat(i18n.getText("format.datetime"));

        // Sets the time zone of the formatter to the one configured for the JVM through the TZ
        // environment variable
        receiptDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

        Collection<LendingDTO> lendings = this.listLendings(lendingsIds);

        if (lendings == null || lendings.isEmpty()) {
            return StringPool.BLANK;
        }

        Collection<LendingInfoDTO> lendingInfo = this.populateLendingInfo(lendings);

        if (lendingInfo == null || lendingInfo.isEmpty()) {
            return StringPool.BLANK;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("<pre>\n");
        receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns)).append(CharPool.NEW_LINE);
        receipt.append(CharPool.ASTERISK)
                .append(StringUtils.repeat(CharPool.SPACE, columns - 2))
                .append("*\n");

        String libraryName = configurationBO.getString("general.title");

        receipt.append("* ").append(StringUtils.center(libraryName, columns - 4)).append(" *\n");
        receipt.append(CharPool.ASTERISK)
                .append(StringUtils.repeat(CharPool.SPACE, columns - 2))
                .append("*\n");
        receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns)).append(CharPool.NEW_LINE);
        receipt.append(StringUtils.center(receiptDateFormat.format(new Date()), columns))
                .append(CharPool.NEW_LINE);
        receipt.append(CharPool.NEW_LINE);

        if (!lendingInfo.isEmpty()) {

            LendingInfoDTO firstEntry = lendingInfo.iterator().next();

            UserDTO user = firstEntry.getUser();

            String nameLabel = i18n.getText("circulation.user_field.name");
            String userName = StringEscapeUtils.escapeHtml4(user.getName());
            String idLabel = i18n.getText("circulation.user_field.id");
            String enrollment = user.getEnrollment();

            receipt.append(nameLabel).append(CharPool.COLON);
            if (nameLabel.length() + userName.length() + 2 > columns) {
                receipt.append(CharPool.NEW_LINE);
                receipt.append("   ")
                        .append(StringUtils.abbreviate(userName, columns - 3))
                        .append(CharPool.NEW_LINE);
            } else {
                receipt.append(CharPool.SPACE)
                        .append(StringUtils.abbreviate(userName, columns - nameLabel.length() - 3))
                        .append(CharPool.NEW_LINE);
            }

            receipt.append(idLabel).append(CharPool.COLON);
            if (idLabel.length() + enrollment.length() + 2 > columns) {
                receipt.append(CharPool.NEW_LINE);
                receipt.append("   ")
                        .append(StringUtils.abbreviate(enrollment, columns - 3))
                        .append(CharPool.NEW_LINE);
            } else {
                receipt.append(CharPool.SPACE)
                        .append(StringUtils.abbreviate(enrollment, columns - idLabel.length() - 3))
                        .append(CharPool.NEW_LINE);
            }
            receipt.append(CharPool.NEW_LINE);

            List<LendingInfoDTO> currentLendings = new ArrayList<>();
            List<LendingInfoDTO> currentRenews = new ArrayList<>();
            List<LendingInfoDTO> currentReturns = new ArrayList<>();

            for (LendingInfoDTO info : lendingInfo) {
                LendingDTO lendingDto = info.getLending();
                if (lendingDto.getReturnDate() != null) {
                    currentReturns.add(info);
                } else if (lendingDto.getPreviousLendingId() != null
                        && lendingDto.getPreviousLendingId() > 0) {
                    currentRenews.add(info);
                } else {
                    currentLendings.add(info);
                }
            }

            String authorLabel = i18n.getText("circulation.lending.receipt.author");
            String titleLabel = i18n.getText("circulation.lending.receipt.title");
            String biblioLabel = i18n.getText("circulation.lending.receipt.holding_id");
            String holdingLabel = i18n.getText("circulation.lending.receipt.accession_number");
            String expectedDateLabel =
                    i18n.getText("circulation.lending.receipt.expected_return_date");
            String returnDateLabel = i18n.getText("circulation.lending.receipt.return_date");
            String lendingDateLabel = i18n.getText("circulation.lending.receipt.lending_date");

            DateFormat returnDateFormat =
                    new SimpleDateFormat(i18n.getText(Constants.TRANSLATION_FORMAT_DATE));

            if (!currentLendings.isEmpty()) {

                String header = "**" + i18n.getText("circulation.lending.receipt.lendings") + "**";
                receipt.append(StringUtils.center(header, columns)).append(CharPool.NEW_LINE);
                receipt.append(CharPool.NEW_LINE);

                for (LendingInfoDTO info : currentLendings) {
                    receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns / 2))
                            .append(CharPool.NEW_LINE);
                    receipt.append(authorLabel).append(":\n");
                    String author = info.getBiblio().getAuthor();
                    author =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(author, columns - 3));
                    receipt.append("   ").append(author).append(CharPool.NEW_LINE);
                    receipt.append(titleLabel).append(":\n");
                    String title = info.getBiblio().getTitle();
                    title =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(title, columns - 3));
                    receipt.append("   ").append(title).append(CharPool.NEW_LINE);
                    receipt.append(biblioLabel).append(":\n");
                    receipt.append("   ")
                            .append(info.getHolding().getId())
                            .append(CharPool.NEW_LINE);
                    receipt.append(holdingLabel).append(":\n");
                    String accessionNumber = info.getHolding().getAccessionNumber();
                    accessionNumber =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(accessionNumber, columns - 3));
                    receipt.append("   ").append(accessionNumber).append(CharPool.NEW_LINE);
                    receipt.append(lendingDateLabel).append(":\n");
                    Date lendingDate = info.getLending().getCreated();
                    receipt.append("   ")
                            .append(receiptDateFormat.format(lendingDate))
                            .append(CharPool.NEW_LINE);
                    receipt.append(expectedDateLabel).append(":\n");
                    Date expectedReturnDate = info.getLending().getExpectedReturnDate();

                    receipt.append("   ")
                            .append(returnDateFormat.format(expectedReturnDate))
                            .append(CharPool.NEW_LINE);
                    receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns / 2))
                            .append(CharPool.NEW_LINE);
                    receipt.append(CharPool.NEW_LINE);
                }
            }

            if (!currentRenews.isEmpty()) {

                String header = "**" + i18n.getText("circulation.lending.receipt.renews") + "**";
                receipt.append(StringUtils.center(header, columns)).append(CharPool.NEW_LINE);
                receipt.append(CharPool.NEW_LINE);

                for (LendingInfoDTO info : currentRenews) {
                    receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns / 2))
                            .append(CharPool.NEW_LINE);
                    receipt.append(authorLabel).append(":\n");
                    String author = info.getBiblio().getAuthor();
                    author =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(author, columns - 3));
                    receipt.append("   ").append(author).append(CharPool.NEW_LINE);
                    receipt.append(titleLabel).append(":\n");
                    String title = info.getBiblio().getTitle();
                    title =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(title, columns - 3));
                    receipt.append("   ").append(title).append(CharPool.NEW_LINE);
                    receipt.append(biblioLabel).append(":\n");
                    receipt.append("   ")
                            .append(info.getHolding().getId())
                            .append(CharPool.NEW_LINE);
                    receipt.append(holdingLabel).append(":\n");
                    String accessionNumber = info.getHolding().getAccessionNumber();
                    accessionNumber =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(accessionNumber, columns - 3));
                    receipt.append("   ").append(accessionNumber).append(CharPool.NEW_LINE);
                    receipt.append(lendingDateLabel).append(":\n");
                    Date lendingDate = info.getLending().getCreated();
                    receipt.append("   ")
                            .append(receiptDateFormat.format(lendingDate))
                            .append(CharPool.NEW_LINE);
                    receipt.append(expectedDateLabel).append(":\n");
                    Date expectedReturnDate = info.getLending().getExpectedReturnDate();
                    receipt.append("   ")
                            .append(returnDateFormat.format(expectedReturnDate))
                            .append(CharPool.NEW_LINE);
                    receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns / 2))
                            .append(CharPool.NEW_LINE);
                    receipt.append(CharPool.NEW_LINE);
                }
            }

            if (!currentReturns.isEmpty()) {

                String header = "**" + i18n.getText("circulation.lending.receipt.returns") + "**";
                receipt.append(StringUtils.center(header, columns)).append(CharPool.NEW_LINE);
                receipt.append(CharPool.NEW_LINE);

                for (LendingInfoDTO info : currentReturns) {
                    receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns / 2))
                            .append(CharPool.NEW_LINE);
                    receipt.append(authorLabel).append(":\n");
                    String author = info.getBiblio().getAuthor();
                    author =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(author, columns - 3));
                    receipt.append("   ").append(author).append(CharPool.NEW_LINE);
                    receipt.append(titleLabel).append(":\n");
                    String title = info.getBiblio().getTitle();
                    title =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(title, columns - 3));
                    receipt.append("   ").append(title).append(CharPool.NEW_LINE);
                    receipt.append(biblioLabel).append(":\n");
                    receipt.append("   ")
                            .append(info.getHolding().getId())
                            .append(CharPool.NEW_LINE);
                    receipt.append(holdingLabel).append(":\n");
                    String accessionNumber = info.getHolding().getAccessionNumber();
                    accessionNumber =
                            StringEscapeUtils.escapeHtml4(
                                    StringUtils.abbreviate(accessionNumber, columns - 3));
                    receipt.append("   ").append(accessionNumber).append(CharPool.NEW_LINE);
                    receipt.append(lendingDateLabel).append(":\n");
                    Date lendingDate = info.getLending().getCreated();
                    receipt.append("   ")
                            .append(receiptDateFormat.format(lendingDate))
                            .append(CharPool.NEW_LINE);
                    receipt.append(returnDateLabel).append(":\n");
                    Date returnDate = info.getLending().getReturnDate();
                    receipt.append("   ")
                            .append(receiptDateFormat.format(returnDate))
                            .append(CharPool.NEW_LINE);
                    receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns / 2))
                            .append(CharPool.NEW_LINE);
                    receipt.append(CharPool.NEW_LINE);
                }
            }
        }
        receipt.append(CharPool.NEW_LINE);
        receipt.append(StringUtils.repeat(CharPool.ASTERISK, columns)).append(CharPool.NEW_LINE);
        receipt.append("</pre>\n");

        return receipt.toString();
    }

    private String generateTableReceipt(List<Integer> lendingsIds, TranslationsMap i18n) {

        Map<String, Object> root = new HashMap<>();

        String formatDateTime = i18n.getText(Constants.TRANSLATION_FORMAT_DATETIME);

        DateFormat dateTimeFormat = new SimpleDateFormat(formatDateTime);

        root.put("dateTimeFormat", formatDateTime);

        String formatDate = i18n.getText(Constants.TRANSLATION_FORMAT_DATE);

        root.put("dateFormat", formatDate);

        Collection<LendingDTO> lendings = this.listLendings(lendingsIds);

        if (lendings == null || lendings.isEmpty()) {
            return "";
        }

        Collection<LendingInfoDTO> lendingInfo = this.populateLendingInfo(lendings);

        if (lendingInfo == null || lendingInfo.isEmpty()) {
            return "";
        }

        root.put("lendingInfo", lendingInfo);

        String libraryName = configurationBO.getString("general.title");
        String now = dateTimeFormat.format(new Date());

        root.put("libraryName", libraryName);
        root.put("now", now);

        if (lendingInfo.size() > 0) {

            LendingInfoDTO firstEntry = lendingInfo.iterator().next();

            UserDTO user = firstEntry.getUser();

            root.put("user", user);

            String nameLabel = i18n.getText("circulation.user_field.name");

            root.put("nameLabel", nameLabel);

            String userName = StringEscapeUtils.escapeHtml4(user.getName());

            root.put("userName", userName);

            String idLabel = i18n.getText("circulation.user_field.id");

            root.put("idLabel", idLabel);

            String enrollment = user.getEnrollment();

            root.put("enrollment", enrollment);

            List<LendingInfoDTO> currentLendings = new ArrayList<>();
            List<LendingInfoDTO> currentRenews = new ArrayList<>();
            List<LendingInfoDTO> currentReturns = new ArrayList<>();

            for (LendingInfoDTO info : lendingInfo) {
                LendingDTO lendingDto = info.getLending();

                if (lendingDto.getReturnDate() != null) {
                    currentReturns.add(info);
                } else if (lendingDto.getPreviousLendingId() != null
                        && lendingDto.getPreviousLendingId() > 0) {
                    currentRenews.add(info);
                } else {
                    currentLendings.add(info);
                }
            }

            String authorLabel = i18n.getText("circulation.lending.receipt.author");
            String titleLabel = i18n.getText("circulation.lending.receipt.title");
            String biblioLabel = i18n.getText("circulation.lending.receipt.holding_id");
            String holdingLabel = i18n.getText("circulation.lending.receipt.accession_number");
            String expectedDateLabel =
                    i18n.getText("circulation.lending.receipt.expected_return_date");
            String returnDateLabel = i18n.getText("circulation.lending.receipt.return_date");
            String lendingDateLabel = i18n.getText("circulation.lending.receipt.lending_date");

            root.put("currentLendings", currentLendings);
            root.put("currentRenews", currentRenews);
            root.put("currentReturns", currentReturns);

            root.put("authorLabel", authorLabel);
            root.put("titleLabel", titleLabel);
            root.put("biblioLabel", biblioLabel);
            root.put("holdingLabel", holdingLabel);
            root.put("expectedDateLabel", expectedDateLabel);
            root.put("returnDateLabel", returnDateLabel);
            root.put("lendingDateLabel", lendingDateLabel);

            if (!currentLendings.isEmpty()) {
                String header = i18n.getText("circulation.lending.receipt.lendings");

                root.put("header", header);
            }

            if (!currentRenews.isEmpty()) {
                String header = i18n.getText("circulation.lending.receipt.renews");

                root.put("renewsHeader", header);
            }

            if (!currentReturns.isEmpty()) {

                String header = i18n.getText("circulation.lending.receipt.returns");

                root.put("returnsHeader", header);
            }
        }

        StringWriter writer = new StringWriter();

        Context context = new Context(Locale.getDefault(), root);

        templateEngine.process("receipt", context, writer);

        return writer.toString();
    }

    public LendingDTO getCurrentLending(HoldingDTO holding) {
        return lendingDAO.getCurrentLending(holding);
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setHoldingBO(HoldingBO holdingBO) {
        this.holdingBO = holdingBO;
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
    public void setLendingFineBO(LendingFineBO lendingFineBO) {
        this.lendingFineBO = lendingFineBO;
    }

    @Autowired
    public void setReservationBO(ReservationBO reservationBO) {
        this.reservationBO = reservationBO;
    }

    @Autowired
    public void setLendingDAO(LendingDAO lendingDAO) {
        this.lendingDAO = lendingDAO;
    }

    @Autowired
    public void setTemplateEngin(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
