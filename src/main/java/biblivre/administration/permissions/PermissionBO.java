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
package biblivre.administration.permissions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biblivre.circulation.user.UserDTO;

@Service
public class PermissionBO {
	private PermissionDAO dao;

	@Autowired
	public PermissionBO(PermissionDAO dao) {
		super();
		this.dao = dao;
	}

	public boolean delete(UserDTO user) {
		return this.dao.delete(user);
	}

	public boolean save(Integer loginId, List<String> permissions) {
		UserDTO dto = new UserDTO();
		dto.setLoginId(loginId);
		if (this.delete(dto)) {
			return this.dao.save(loginId, permissions);
		}
		return false;
	}

	public List<String> getByLoginId(Integer loginid) {
		return this.dao.getByLoginId(loginid);
	}
}
