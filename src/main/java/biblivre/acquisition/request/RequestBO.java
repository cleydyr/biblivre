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
package biblivre.acquisition.request;

import biblivre.core.AbstractBO;
import biblivre.core.DTOCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestBO extends AbstractBO {
    private RequestDAO requestDAO;

    public RequestDTO get(Integer id) {
        return this.requestDAO.get(id);
    }

    public boolean save(RequestDTO dto) {
        return this.requestDAO.save(dto);
    }

    public boolean update(RequestDTO dto) {
        return this.requestDAO.update(dto);
    }

    public boolean delete(RequestDTO dto) {
        return this.requestDAO.delete(dto);
    }

    public DTOCollection<RequestDTO> list() {
        return this.search(null, Integer.MAX_VALUE, 0);
    }

    public DTOCollection<RequestDTO> search(String value, int limit, int offset) {
        return this.requestDAO.search(value, limit, offset);
    }

    @Autowired
    public void setRequestDAO(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }
}
