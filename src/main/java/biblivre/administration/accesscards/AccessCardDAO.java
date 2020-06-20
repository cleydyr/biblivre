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
package biblivre.administration.accesscards;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.persistence.AbstractDAO;
import biblivre.core.persistence.sql.PreparedStatementUtil;

public class AccessCardDAO extends AbstractDAO implements IAccessCardDAO {

	private static final String _DELETE_SQL =
		"DELETE FROM access_cards WHERE id = ?";

	private static final String _UPDATE_SQL =
		"UPDATE access_cards " +
		"SET status = ?, modified = now(), modified_by = ? " +
		"WHERE id = ?";

	private static final String _SAVE_FROM_V3_SQL =
		"INSERT INTO access_cards" +
			"(code, status, created_by, id) " +
		"VALUES (?, ?, ?, ?)";

	private static final String _SAVE_SQL =
		"INSERT INTO access_cards" +
			"(id, code, status, created_by) " +
		"VALUES (?, ?, ?, ?)";

	private static final String _GET_SQL =
		"SELECT * FROM access_cards WHERE id = ?";

	@Override
	public Collection<AccessCardDTO> get(
		List<String> codes) {

		int codesSize = codes != null ? codes.size() : 0;

		String sql = _getSQL(codesSize);

		if (codesSize > 0) {
			return listWith(this::populateDTO, sql, codes.toArray());
		}
		else {
			return listWith(this::populateDTO, sql);
		}
	}

	@Override
	public AccessCardDTO get(int id) {
		return fetchOne(
			this::populateDTO, _GET_SQL, id);
	}

	@Override
	public DTOCollection<AccessCardDTO> search(
		String code, AccessCardStatus status, int limit, int offset) {

		boolean isNotBlankCode = StringUtils.isNotBlank(code);

		Object[] parameters = new Object[isNotBlankCode ? 2 : 1];

		parameters[0] =
			status == null ? AccessCardStatus.CANCELLED.toString() :
				status.toString();

		if (isNotBlankCode) {
			parameters[1] = "%" + code + "%";
		}

		String searchSQL = _getSearchSQL(code, status);

		return pagedListWith(
			this::populateDTO, searchSQL, limit, offset, parameters);
	}

	@Override
	public AccessCardDTO create() {
		AccessCardDTO accessCard = new AccessCardDTO();

		onTransactionContext(con -> {
			accessCard.setId(getNextSerial(con, "access_cards_id_seq"));
		});

		return accessCard;
	}

	@Override
	public boolean save(LinkedList<AccessCardDTO> cardList) {
		return executeBatchUpdate((pst, card) -> {
			PreparedStatementUtil.setAllParameters(
				pst, card.getId(), card.getCode(), card.getStatus().toString(),
				card.getCreatedBy());
		}, cardList, _SAVE_SQL);
	}

	@Override
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executeBatchUpdate((pst, item) -> {
			AccessCardDTO accessCard = (AccessCardDTO) item;
			PreparedStatementUtil.setAllParameters(
				pst, accessCard.getCode(), accessCard.getStatus().toString(),
				accessCard.getCreatedBy(), accessCard.getId());
		}, dtoList, _SAVE_FROM_V3_SQL);
	}

	@Override
	public boolean update(AccessCardDTO dto) {
		return executeUpdate(
			_UPDATE_SQL, dto.getStatus().toString(), dto.getModifiedBy(),
			dto.getId());
	}

	@Override
	public boolean delete(int id) {
		return executeUpdate(_DELETE_SQL, id);
	}

	private AccessCardDTO populateDTO(ResultSet rs) throws SQLException {
		AccessCardDTO dto = new AccessCardDTO();
		dto.setId(rs.getInt("id"));
		dto.setCode(rs.getString("code"));
		dto.setStatus(AccessCardStatus.fromString(rs.getString("status")));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

	private String _getSQL(int codesSize) {
		StringBuilder sb = new StringBuilder(codesSize > 0 ? 4 : 1);

		sb.append("SELECT * FROM access_cards");

		if (codesSize > 0) {
			sb.append("WHERE code in (");
			sb.append(StringUtils.repeat("?", ", ", codesSize));
			sb.append(");");
		}

		return sb.toString();
	}

	private String _getSearchSQL(String code, AccessCardStatus status) {
		StringBuilder sql =
			new StringBuilder(StringUtils.isNotBlank(code) ? 4 : 3);

		sql.append("SELECT * FROM access_cards WHERE ");

		if (status == null) {
			sql.append("status <> ? ");
		} else {
			sql.append("status = ? ");
		}

		if (StringUtils.isNotBlank(code)) {
			sql.append("AND code ILIKE ? ");
		}

		sql.append("ORDER BY id ASC LIMIT ? OFFSET ?;");

		return sql.toString();
	}
}
