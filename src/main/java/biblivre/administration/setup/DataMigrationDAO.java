package biblivre.administration.setup;

import biblivre.acquisition.order.OrderDTO;
import biblivre.acquisition.quotation.QuotationDTO;
import biblivre.acquisition.quotation.RequestQuotationDTO;
import biblivre.acquisition.request.RequestDTO;
import biblivre.acquisition.supplier.SupplierDTO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.accesscontrol.AccessControlDTO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.reservation.ReservationDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.file.MemoryFile;
import biblivre.login.LoginDTO;
import java.util.List;

public interface DataMigrationDAO {

    int countCatalogingRecords(RecordType recordType);

    List<RecordDTO> listCatalogingRecords(RecordType recordType, int limit, int offset);

    List<HoldingDTO> listCatalogingHoldings(int limit, int offset);

    List<MemoryFile> listDigitalMedia(int limit, int offset);

    List<AccessCardDTO> listAccessCards(int limit, int offset);

    List<LoginDTO> listLogins(int limit, int offset);

    List<AccessControlDTO> listAccessControl(int limit, int offset);

    List<AccessControlDTO> listAccessControlHistory(int limit, int offset);

    List<LendingDTO> listLendings(int limit, int offset);

    List<LendingFineDTO> listLendingFines(int limit, int offset);

    List<SupplierDTO> listAquisitionSupplier(int limit, int offset);

    List<RequestDTO> listAquisitionRequisition(int limit, int offset);

    List<QuotationDTO> listAquisitionQuotation(int limit, int offset);

    List<RequestQuotationDTO> listAquisitionItemQuotation(int limit, int offset);

    List<OrderDTO> listAquisitionOrder(int limit, int offset);

    List<ReservationDTO> listReservations(int limit, int offset);

    List<UserDTO> listUsers(int limit, int offset);

    List<UserTypeDTO> listUsersTypes(int limit, int offset);

    void setUserSchema(String userSchema);
}
