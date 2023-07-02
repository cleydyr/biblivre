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
package biblivre.acquisition.supplier;

import biblivre.core.AbstractBO;
import biblivre.core.DTOCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierBO extends AbstractBO {
    private SupplierDAO supplierDAO;

    public SupplierDTO get(Integer id) {
        return this.supplierDAO.get(id);
    }

    public int save(SupplierDTO dto) {
        return this.supplierDAO.save(dto);
    }

    public void update(SupplierDTO dto) {
        this.supplierDAO.update(dto);
    }

    public boolean delete(SupplierDTO dto) {
        return this.supplierDAO.delete(dto.getId());
    }

    public DTOCollection<SupplierDTO> list() {
        return this.search(null, Integer.MAX_VALUE, 0);
    }

    public DTOCollection<SupplierDTO> search(String value, int limit, int offset) {
        return this.supplierDAO.search(value, limit, offset);
    }

    @Autowired
    public void setSupplierDAO(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }
}
