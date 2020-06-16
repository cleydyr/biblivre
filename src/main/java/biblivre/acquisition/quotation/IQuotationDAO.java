package biblivre.acquisition.quotation;

import java.util.List;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public interface IQuotationDAO {

	Integer save(QuotationDTO dto);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	boolean update(QuotationDTO dto);

	QuotationDTO get(int id);

	List<RequestQuotationDTO> listRequestQuotation(int quotationId);

	boolean delete(QuotationDTO dto);

	DTOCollection<QuotationDTO> search(String value, int limit, int offset);

	DTOCollection<QuotationDTO> list(Integer supplierId);

}