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
package biblivre.circulation.accesscontrol;

import java.sql.ResultSet;
import java.util.List;

import biblivre.core.AbstractDTO;
import biblivre.core.persistence.AbstractDAO;

public class AccessControlDAO extends AbstractDAO implements IAccessControlDAO {

	private static final String _GET_BY_USER_ID_SQL =
		"SELECT * FROM access_control "
		+ "WHERE user_id = ? and departure_time is null";

	private static final String _GET_BY_CARD_ID_SQL =
		"SELECT * FROM access_control "
		+ "WHERE access_card_id = ? AND departure_time is null";

	private static final String _UPDATE_SQL =
		"UPDATE access_control "
		+ "SET departure_time = now(), modified = now(), modified_by = ? "
		+ "WHERE id = ?";

	private static final String _SAVE_FROM_V3_SQL =
		"INSERT INTO access_control "
		+ "(access_card_id, user_id, created_by, id) "
		+ "VALUES (?, ?, ?, ?)";

	private static final String _SAVE_SQL =
		"INSERT INTO access_control "
		+ "(access_card_id, user_id, created_by) "
		+ "VALUES (?, ?, ?)";

	@Override
	public boolean save(AccessControlDTO dto) {
		return executeUpdate(
			_SAVE_SQL, dto.getAccessCardId(), dto.getUserId(),
			dto.getCreatedBy());
	}

	@Override
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executeBatchUpdate(
			dtoList, AccessControlDTO.class, _SAVE_FROM_V3_SQL,
			AccessControlDTO::getAccessCardId, AccessControlDTO::getUserId,
			AccessControlDTO::getCreatedBy, AccessControlDTO::getId);
	}

	@Override
	public boolean update(AccessControlDTO dto) {
		return executeUpdate(
			_UPDATE_SQL, dto.getModifiedBy(), dto.getId());
	}

	@Override
	public AccessControlDTO getByCardId(Integer cardId) {
		return fetchOne(this::populateDto, _GET_BY_CARD_ID_SQL, cardId);
	}

	@Override
	public AccessControlDTO getByUserId(Integer userId) {
		return fetchOne(this::populateDto, _GET_BY_USER_ID_SQL, userId);
	}

	private AccessControlDTO populateDto(ResultSet rs) throws Exception {
		AccessControlDTO dto = new AccessControlDTO();
		dto.setId(rs.getInt("id"));
		dto.setAccessCardId(rs.getInt("access_card_id"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setArrivalTime(rs.getTimestamp("arrival_time"));
		dto.setDepartureTime(rs.getTimestamp("departure_time"));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

}
