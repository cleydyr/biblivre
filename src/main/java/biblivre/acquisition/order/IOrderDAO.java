package biblivre.acquisition.order;

import java.util.List;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public interface IOrderDAO {

	OrderDTO get(Integer orderId);

	Integer save(OrderDTO dto);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	boolean update(OrderDTO dto);

	boolean delete(OrderDTO dto);

	DTOCollection<OrderDTO> search(String value, int offset, int limit);

}