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
package biblivre.acquisition.order;

import biblivre.acquisition.quotation.QuotationBO;
import biblivre.acquisition.quotation.QuotationDTO;
import biblivre.acquisition.quotation.RequestQuotationDTO;
import biblivre.acquisition.request.RequestBO;
import biblivre.acquisition.request.RequestDTO;
import biblivre.acquisition.supplier.SupplierBO;
import biblivre.acquisition.supplier.SupplierDTO;
import biblivre.core.AbstractBO;
import biblivre.core.DTOCollection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderBO extends AbstractBO {
    private OrderDAO orderDAO;
    private QuotationBO quotationBO;
    private SupplierBO supplierBO;
    private RequestBO requestBO;

    public OrderDTO get(Integer id) {
        OrderDTO dto = this.orderDAO.get(id);

        this.populateDTO(dto);

        return dto;
    }

    public Integer save(OrderDTO dto) {
        return this.orderDAO.save(dto);
    }

    public boolean update(OrderDTO dto) {
        return this.orderDAO.update(dto);
    }

    public boolean delete(OrderDTO dto) {
        return this.orderDAO.delete(dto.getId());
    }

    public DTOCollection<OrderDTO> list() {
        return this.search(null, 0, Integer.MAX_VALUE);
    }

    public DTOCollection<OrderDTO> search(String value, int offset, int limit) {
        DTOCollection<OrderDTO> list = this.orderDAO.search(value, offset, limit);

        for (OrderDTO dto : list) {
            this.populateDTO(dto);
        }

        return list;
    }

    private void populateDTO(OrderDTO dto) {
        QuotationDTO qdto = quotationBO.get(dto.getQuotationId());
        SupplierDTO sdto = supplierBO.get(qdto.getSupplierId());

        List<RequestQuotationDTO> rqList = quotationBO.listRequestQuotation(qdto.getId());
        for (RequestQuotationDTO rqdto : rqList) {
            RequestDTO request = requestBO.get(rqdto.getRequestId());
            rqdto.setAuthor(request.getAuthor());
            rqdto.setTitle(request.getTitle());
        }

        dto.setQuotationsList(rqList);
        dto.setSupplierId(qdto.getSupplierId());
        dto.setSupplierName(sdto.getTrademark());
        dto.setDeliveryTime(qdto.getDeliveryTime());
    }

    @Autowired
    public void setOrderDAO(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Autowired
    public void setQuotationBO(QuotationBO quotationBO) {
        this.quotationBO = quotationBO;
    }

    @Autowired
    public void setSupplierBO(SupplierBO supplierBO) {
        this.supplierBO = supplierBO;
    }

    @Autowired
    public void setRequestBO(RequestBO requestBO) {
        this.requestBO = requestBO;
    }
}
