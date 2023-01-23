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
package biblivre.acquisition.quotation;

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
public class QuotationBO extends AbstractBO {
    private QuotationDAO quotationDAO;
    private SupplierBO supplierBO;
    private RequestBO requestBO;

    public QuotationDTO get(Integer id) {
        QuotationDTO dto = this.quotationDAO.get(id);

        this.populateDTO(dto, requestBO, supplierBO);

        return dto;
    }

    public Integer save(QuotationDTO dto) {
        return this.quotationDAO.save(dto);
    }

    public boolean update(QuotationDTO dto) {
        return this.quotationDAO.update(dto);
    }

    public boolean delete(QuotationDTO dto) {
        return this.quotationDAO.delete(dto);
    }

    public DTOCollection<QuotationDTO> list() {
        return this.search(null, Integer.MAX_VALUE, 0);
    }

    public DTOCollection<QuotationDTO> search(String value, int limit, int offset) {
        DTOCollection<QuotationDTO> list = this.quotationDAO.search(value, limit, offset);

        for (QuotationDTO quotation : list) {
            this.populateDTO(quotation, requestBO, supplierBO);
        }
        return list;
    }

    public DTOCollection<QuotationDTO> list(Integer supplierId) {
        DTOCollection<QuotationDTO> list = this.quotationDAO.list(supplierId);

        for (QuotationDTO quotation : list) {
            this.populateDTO(quotation, requestBO, supplierBO);
        }
        return list;
    }

    public List<RequestQuotationDTO> listRequestQuotation(Integer quotationId) {
        return this.quotationDAO.listRequestQuotation(quotationId);
    }

    private void populateDTO(QuotationDTO dto, RequestBO rbo, SupplierBO sbo) {
        List<RequestQuotationDTO> rqList = this.quotationDAO.listRequestQuotation(dto.getId());
        for (RequestQuotationDTO rqdto : rqList) {
            RequestDTO request = rbo.get(rqdto.getRequestId());
            rqdto.setAuthor(request.getAuthor());
            rqdto.setTitle(request.getTitle());
        }
        dto.setQuotationsList(rqList);

        SupplierDTO sdto = sbo.get(dto.getSupplierId());
        dto.setSupplierName(sdto.getTrademark());
    }

    @Autowired
    public void setQuotationDAO(QuotationDAO quotationDAO) {
        this.quotationDAO = quotationDAO;
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
