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
package biblivre.administration.setup;

import biblivre.acquisition.order.OrderBO;
import biblivre.acquisition.quotation.QuotationBO;
import biblivre.acquisition.request.RequestBO;
import biblivre.acquisition.supplier.SupplierBO;
import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.cataloging.authorities.AuthorityRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.vocabulary.VocabularyRecordBO;
import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.user.UserBO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.MemoryFile;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.login.LoginBO;
import biblivre.z3950.Z3950BO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMigrationBO extends AbstractBO {

    private DataMigrationDAOImpl dataMigrationDAO;
    private SetupDAO setupDAO;
    private final Integer limit = 50;

    private boolean migratingDatabase = false;
    private DataMigrationPhase currentPhase;
    private Integer currentCount;

    private Map<Integer, Integer> lendingMap = new HashMap<>();
    private Map<Integer, Integer> lendingHistoryMap = new HashMap<>();

    private AccessControlBO accessControlBO;
    private AccessCardBO accessCardBO;
    private LoginBO loginBO;
    private UserBO userBO;
    private QuotationBO quotationBO;
    private OrderBO orderBO;
    private SupplierBO supplierBO;
    private RequestBO requestBO;
    private BiblioRecordBO biblioRecordBO;
    private AuthorityRecordBO authoritiyRecordBO;
    private VocabularyRecordBO vocabularyRecordBO;
    private HoldingBO holdingsBO;
    private LendingBO lendingBO;
    private UserTypeBO userTypeBO;
    private LendingFineBO lendingFineBO;
    private ReservationBO reservationBO;
    private Z3950BO z3950BO;
    private DigitalMediaBO digitalMediaBO;

    public boolean isBiblivre3Available() {
        return this.dataMigrationDAO.testDatabaseConnection();
    }

    public boolean migrate(
            String schema, String datasource, List<DataMigrationPhase> selectedPhases) {
        dataMigrationDAO.setDataSourceName(datasource);
        dataMigrationDAO.setUserSchema(schema);

        synchronized (this) {
            this.migratingDatabase = true;

            if (selectedPhases == null || selectedPhases.isEmpty()) {
                selectedPhases = Arrays.asList(DataMigrationPhase.values());
            }

            State.setSteps(selectedPhases.size());

            // DIGITAL MEDIA FILES CAN BE QUITE LARGE, SO WE WILL MIGRATE THEM
            // SEPARATELY AND SLOWER (THE LIMIT VALUE IS LOWER) THAN THE REST.
            boolean migrateDigitalMedia = false;
            if (selectedPhases.contains(DataMigrationPhase.DIGITAL_MEDIA)) {
                migrateDigitalMedia = true;
                selectedPhases.remove(DataMigrationPhase.DIGITAL_MEDIA);
            }

            for (DataMigrationPhase phase : selectedPhases) {
                this.currentPhase = phase;
                this.setCurrentCount(0);
                int page = 0;
                int offset = this.limit * page;

                boolean firstPass = true;
                boolean hasMore = true;
                State.setCurrentSecondaryStep(0);
                int retries = 0;
                while (hasMore) {
                    try {
                        List<? extends AbstractDTO> dtoList =
                                this.listDTOs(phase, this.limit, offset);
                        if (dtoList == null || dtoList.size() == 0) {
                            hasMore = false;
                        } else {
                            if (firstPass) {
                                if (!phase.equals(DataMigrationPhase.ACCESS_CONTROL_HISTORY)) {
                                    this.setupDAO.deleteAll(phase);
                                }
                                firstPass = false;
                            }
                            this.setCurrentCount(this.getCurrentCount() + dtoList.size());
                            offset = this.limit * ++page;
                            this.saveDTOs(phase, dtoList);
                            State.incrementCurrentSecondaryStep(dtoList.size());
                        }
                    } catch (DAOException e) {
                        if (++retries > 3) {
                            State.writeLog(
                                    "DataMigration failed after 3 attempts at phase: "
                                            + phase.toString());
                            throw e;
                        }
                    }
                }
                this.setupDAO.fixSequence(this.currentPhase);
                State.incrementCurrentStep();
            }

            if (migrateDigitalMedia) {
                this.currentPhase = DataMigrationPhase.DIGITAL_MEDIA;
                this.setCurrentCount(0);
                int page = 0;
                int digitalMediaLimit = 1;
                int offset = digitalMediaLimit * page;

                boolean firstPass = true;
                boolean hasMore = true;
                while (hasMore) {
                    List<MemoryFile> dtoList = this.listDigitalMedia(digitalMediaLimit, offset);
                    if (dtoList == null || dtoList.size() == 0) {
                        hasMore = false;
                    } else {
                        if (firstPass) {
                            this.setupDAO.deleteAll(DataMigrationPhase.DIGITAL_MEDIA);
                            firstPass = false;
                        }

                        this.setCurrentCount(this.getCurrentCount() + dtoList.size());
                        offset = digitalMediaLimit * ++page;
                        this.saveDigitalMedia(dtoList);
                    }
                }
                this.setupDAO.fixSequence(this.currentPhase);
                State.incrementCurrentStep();
            }

            return true;
        }
    }

    private List<? extends AbstractDTO> listDTOs(DataMigrationPhase phase, int limit, int offset) {
        switch (phase) {
            case CATALOGING_BIBLIOGRAPHIC:
                return this.dataMigrationDAO.listCatalogingRecords(
                        RecordType.BIBLIO, limit, offset);

            case CATALOGING_HOLDINGS:
                return this.dataMigrationDAO.listCatalogingHoldings(limit, offset);

            case CATALOGING_AUTHORITIES:
                return this.dataMigrationDAO.listCatalogingRecords(
                        RecordType.AUTHORITIES, limit, offset);

            case CATALOGING_VOCABULARY:
                return this.dataMigrationDAO.listCatalogingRecords(
                        RecordType.VOCABULARY, limit, offset);

            case ACCESS_CARDS:
                return this.dataMigrationDAO.listAccessCards(limit, offset);

            case LOGINS:
                return this.dataMigrationDAO.listLogins(limit, offset);

            case USER_TYPES:
                return this.dataMigrationDAO.listUsersTypes(limit, offset);

            case USERS:
                return this.dataMigrationDAO.listUsers(limit, offset);

            case ACQUISITION_SUPPLIER:
                return this.dataMigrationDAO.listAquisitionSupplier(limit, offset);

            case ACQUISITION_REQUISITION:
                return this.dataMigrationDAO.listAquisitionRequisition(limit, offset);

            case ACQUISITION_QUOTATION:
                return this.dataMigrationDAO.listAquisitionQuotation(limit, offset);

            case ACQUISITION_ITEM_QUOTATION:
                return this.dataMigrationDAO.listAquisitionItemQuotation(limit, offset);

            case ACQUISITION_ORDER:
                return this.dataMigrationDAO.listAquisitionOrder(limit, offset);

            case Z3950_SERVERS:
                return this.dataMigrationDAO.listZ3950Servers(limit, offset);

            case ACCESS_CONTROL:
                return this.dataMigrationDAO.listAccessControl(limit, offset);

            case ACCESS_CONTROL_HISTORY:
                return this.dataMigrationDAO.listAccessControlHistory(limit, offset);

            case LENDINGS:
                {
                    List<LendingDTO> list = this.dataMigrationDAO.listLendings(limit, offset);

                    int i = offset + 1;
                    for (LendingDTO dto : list) {
                        if (dto.getExpectedReturnDate() == null) {
                            this.lendingHistoryMap.put(dto.getId(), i);
                        } else {
                            this.lendingMap.put(dto.getId(), i);
                        }

                        dto.setId(i);

                        i++;
                    }

                    return list;
                }

            case LENDING_FINE:
                {
                    List<LendingFineDTO> list =
                            this.dataMigrationDAO.listLendingFines(limit, offset);

                    for (LendingFineDTO dto : list) {
                        dto.setLendingId(this.lendingHistoryMap.get(dto.getLendingId()));
                    }

                    return list;
                }

            case RESERVATIONS:
                return this.dataMigrationDAO.listReservations(limit, offset);

            case DIGITAL_MEDIA:
            default:
                return null;
        }
    }

    private boolean saveDTOs(DataMigrationPhase phase, List<? extends AbstractDTO> dtoList) {
        switch (phase) {
            case CATALOGING_BIBLIOGRAPHIC:
                return biblioRecordBO.saveFromBiblivre3(dtoList);

            case CATALOGING_AUTHORITIES:
                return authoritiyRecordBO.saveFromBiblivre3(dtoList);

            case CATALOGING_VOCABULARY:
                return vocabularyRecordBO.saveFromBiblivre3(dtoList);

            case CATALOGING_HOLDINGS:
                return holdingsBO.saveFromBiblivre3(dtoList);

            case ACCESS_CARDS:
                return accessCardBO.saveFromBiblivre3(dtoList);

            case LOGINS:
                return loginBO.saveFromBiblivre3(dtoList);

            case USER_TYPES:
                return userTypeBO.saveFromBiblivre3(dtoList);

            case USERS:
                return userBO.saveFromBiblivre3(dtoList);

            case ACQUISITION_SUPPLIER:
                return supplierBO.saveFromBiblivre3(dtoList);

            case ACQUISITION_REQUISITION:
                return requestBO.saveFromBiblivre3(dtoList);

            case ACQUISITION_QUOTATION:
                return quotationBO.saveFromBiblivre3(dtoList);

            case ACQUISITION_ITEM_QUOTATION:
                return quotationBO.saveFromBiblivre3(dtoList);

            case ACQUISITION_ORDER:
                return orderBO.saveFromBiblivre3(dtoList);

            case Z3950_SERVERS:
                return z3950BO.saveFromBiblivre3(dtoList);

            case ACCESS_CONTROL:
                return accessControlBO.saveFromBiblivre3(dtoList);

            case ACCESS_CONTROL_HISTORY:
                return accessControlBO.saveFromBiblivre3(dtoList);

            case LENDINGS:
                return lendingBO.saveFromBiblivre3(dtoList);

            case LENDING_FINE:
                return lendingFineBO.saveFromBiblivre3(dtoList);

            case RESERVATIONS:
                return reservationBO.saveFromBiblivre3(dtoList);

            case DIGITAL_MEDIA:
            default:
                return true;
        }
    }

    private List<MemoryFile> listDigitalMedia(int limit, int offset) {
        return this.dataMigrationDAO.listDigitalMedia(limit, offset);
    }

    private boolean saveDigitalMedia(List<MemoryFile> dtoList) {
        for (MemoryFile file : dtoList) {
            digitalMediaBO.save(file);
        }
        return true;
    }

    public boolean isMigratingDatabase() {
        return this.migratingDatabase;
    }

    public void setMigratingDatabase(boolean migratingDatabase) {
        this.migratingDatabase = migratingDatabase;
    }

    public DataMigrationPhase getCurrentPhase() {
        return this.currentPhase;
    }

    public void setCurrentPhase(DataMigrationPhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Integer getCurrentCount() {
        if (this.currentCount == null) {
            this.currentCount = 0;
        }
        return this.currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public void setDao(DataMigrationDAOImpl dataMigrationDAO) {
        this.dataMigrationDAO = dataMigrationDAO;
    }

    public void setSetupDao(SetupDAO setupDAO) {
        this.setupDAO = setupDAO;
    }

    public void setAccessControlBO(AccessControlBO accessControlBO) {
        this.accessControlBO = accessControlBO;
    }

    public void setAccessCardBO(AccessCardBO accessCardBO) {
        this.accessCardBO = accessCardBO;
    }

    public void setLoginBO(LoginBO loginBO) {
        this.loginBO = loginBO;
    }

    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    public void setQuotationBO(QuotationBO quotationBO) {
        this.quotationBO = quotationBO;
    }

    public void setOrderBO(OrderBO orderBO) {
        this.orderBO = orderBO;
    }

    public void setSupplierBO(SupplierBO supplierBO) {
        this.supplierBO = supplierBO;
    }

    public void setRequestBO(RequestBO requestBO) {
        this.requestBO = requestBO;
    }

    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    public void setAuthoritiyRecordBO(AuthorityRecordBO authoritiyRecordBO) {
        this.authoritiyRecordBO = authoritiyRecordBO;
    }

    public void setVocabularyRecordBO(VocabularyRecordBO vocabularyRecordBO) {
        this.vocabularyRecordBO = vocabularyRecordBO;
    }

    public void setHoldingsBO(HoldingBO holdingsBO) {
        this.holdingsBO = holdingsBO;
    }

    public void setLendingBO(LendingBO lendingBO) {
        this.lendingBO = lendingBO;
    }

    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }

    public void setLendingFineBO(LendingFineBO lendingFineBO) {
        this.lendingFineBO = lendingFineBO;
    }

    public void setReservationBO(ReservationBO reservationBO) {
        this.reservationBO = reservationBO;
    }
}
