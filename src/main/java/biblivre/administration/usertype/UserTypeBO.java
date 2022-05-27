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

import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserSearchDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.exceptions.ValidationException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTypeBO extends AbstractBO {
    private UserTypeDAO userTypeDAO;
    private UserBO userBO;

    public UserTypeDTO get(int id) {
        return this.userTypeDAO.get(id);
    }

    public List<UserTypeDTO> list() {
        return this.userTypeDAO.list();
    }

    public DTOCollection<UserTypeDTO> search(String value, int limit, int offset) {
        return this.userTypeDAO.search(value, limit, offset);
    }

    public boolean save(UserTypeDTO userTypeDTO) {
        return this.userTypeDAO.save(userTypeDTO);
    }

    public boolean delete(int id) {
        // Check if there's any user for this user_type
        UserSearchDTO dto = new UserSearchDTO();
        dto.setType(id);

        DTOCollection<UserDTO> userList = userBO.search(dto, 1, 0);
        boolean existingUser = userList.size() > 0;

        if (existingUser) {
            throw new ValidationException("administration.user_type.error.type_has_users");
        }

        return this.userTypeDAO.delete(id);
    }

    public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
        return this.userTypeDAO.saveFromBiblivre3(dtoList);
    }

    @Autowired
    public void setUserTypeDAO(UserTypeDAO userTypeDAO) {
        this.userTypeDAO = userTypeDAO;
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    //	public boolean updateUserType(UserTypeDTO userTypeDTO) {
    //		return dao.update(userTypeDTO);
    //	}

}
