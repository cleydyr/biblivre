package biblivre.acquisition.order;

import java.util.List;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public interface OrderDAO {

	OrderDTO get(Integer orderId);

	Integer save(OrderDTO dto);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	List<OrderDTO> listBuyOrders(String status, int offset, int limit);

	boolean update(OrderDTO dto);

	boolean delete(OrderDTO dto);

	DTOCollection<OrderDTO> search(String value, int offset, int limit);

}