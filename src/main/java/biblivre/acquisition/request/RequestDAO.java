package biblivre.acquisition.request;

import biblivre.core.DTOCollection;

public interface RequestDAO {

    boolean save(RequestDTO dto);

    RequestDTO get(int id);

    DTOCollection<RequestDTO> search(String value, int limit, int offset);

    boolean update(RequestDTO dto);

    boolean updateRequestStatus(int orderId, RequestStatus status);

    boolean delete(RequestDTO dto);
}
