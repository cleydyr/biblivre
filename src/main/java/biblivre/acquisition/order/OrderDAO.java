package biblivre.acquisition.order;

import biblivre.core.DTOCollection;
import java.util.List;

public interface OrderDAO {

    OrderDTO get(int orderId);

    Integer save(OrderDTO dto);

    List<OrderDTO> listBuyOrders(String status, int offset, int limit);

    boolean update(OrderDTO dto);

    boolean delete(int id);

    DTOCollection<OrderDTO> search(String value, int offset, int limit);
}
