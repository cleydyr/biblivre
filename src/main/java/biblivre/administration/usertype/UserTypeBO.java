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
package biblivre.administration.usertype;

import biblivre.circulation.user.UserDAO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserSearchDTO;
import biblivre.core.AbstractBO;
import biblivre.core.DTOCollection;
import biblivre.core.exceptions.ValidationException;
import biblivre.search.SearchException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTypeBO extends AbstractBO {
    private UserTypeDAO userTypeDAO;
    private UserDAO userDAO;

    public UserTypeDTO get(int id) {
        return this.userTypeDAO.get(id);
    }

    public List<UserTypeDTO> list() {
        return this.userTypeDAO.list();
    }

    public Map<Integer, UserTypeDTO> map() {
        List<UserTypeDTO> list = this.userTypeDAO.list();
        Map<Integer, UserTypeDTO> map = new TreeMap<>();
        for (UserTypeDTO dto : list) {
            map.put(dto.getId(), dto);
        }
        return map;
    }

    public DTOCollection<UserTypeDTO> search(String value, int limit, int offset) {
        return this.userTypeDAO.search(value, limit, offset);
    }

    public boolean save(UserTypeDTO userTypeDTO) {
        return this.userTypeDAO.save(userTypeDTO);
    }

    public boolean delete(int id) throws SearchException {
        // Check if there's any user for this user_type
        UserSearchDTO dto = new UserSearchDTO();
        dto.setType(id);

        DTOCollection<UserDTO> userList = userDAO.search(dto, 1, 0);
        boolean existingUser = !userList.isEmpty();

        if (existingUser) {
            throw new ValidationException("administration.user_type.error.type_has_users");
        }

        return this.userTypeDAO.delete(id);
    }

    @Autowired
    public void setUserTypeDAO(UserTypeDAO userTypeDAO) {
        this.userTypeDAO = userTypeDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
