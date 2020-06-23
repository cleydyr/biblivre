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
package biblivre.circulation.lending;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDTO;
import biblivre.core.persistence.AbstractDAO;

public class LendingDAO extends AbstractDAO implements ILendingDAO {

	private static final String _GET_LATEST_SQL =
		"SELECT * FROM lendings "
		+ "WHERE holding_id = ? AND user_id = ? "
		+ "ORDER BY id DESC LIMIT 1";

	private static final String _COUNT_LENT_HOLDINGS_SQL =
		"SELECT COUNT(*) FROM lendings L "
		+ "INNER JOIN biblio_holdings H "
		+ "ON L.holding_id = H.id "
		+ "WHERE H.record_id = ? AND H.availability = 'available' "
			+ "AND L.return_date is NULL";

	private static final String _SAVE_FROM_V3_SQL =
		"INSERT INTO lendings "
		+ "(holding_id, user_id, previous_lending_id, expected_return_date, "
			+ "created_by, id, created, return_date) "
		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";

	private static final String _DO_RETURN_SQL =
		"UPDATE lendings SET return_date = now() WHERE id = ?";

	private static final String _DO_LEND_SQL =
		"INSERT INTO lendings "
		+ "(holding_id, user_id, previous_lending_id, expected_return_date, "
			+ "created_by) "
		+ "VALUES (?, ?, ?, ?, ?)";

	private static final String _GET_CURRENT_LENDINGS_COUNT_SQL =
		"SELECT COUNT(*) FROM lendings "
		+ "WHERE user_id = ? AND return_date IS null";

	private static final String _COUNT_LENDINGS_SQL = 
		"SELECT count(*) as total FROM lendings WHERE return_date IS null";

	private static final String _LIST_LENDINGS_SQL =
		"SELECT * FROM lendings "
		+ "WHERE return_date IS null "
		+ "ORDER BY return_date ASC LIMIT ? OFFSET ?";

	private static final String _LIST_BY_RECORD_ID_SQL =
		"SELECT * FROM lendings l "
		+ "INNER JOIN biblio_holdings h "
		+ "ON l.holding_id = h.id "
		+ "WHERE h.record_id = ?";

	private static final String _USER_HISTORY_COUNT_SQL =
		"SELECT COUNT(*) AS total FROM lendings "
		+ "WHERE user_id = ? AND return_date IS NOT null "
		+ "ORDER BY id DESC";

	private static final String _USER_LENDINGS_COUNT_SQL =
		"SELECT COUNT(*) AS total FROM lendings "
		+ "WHERE user_id = ? AND return_date IS null "
		+ "ORDER BY id DESC";

	private static final String _LIST_USER_HISTORY_SQL =
		"SELECT * FROM lendings "
		+ "WHERE user_id = ? AND return_date IS NOT null "
		+ "ORDER BY id ASC";

	private static final String _LIST_USER_LENDINGS_SQL =
		"SELECT * FROM lendings "
		+ "WHERE user_id = ? AND return_date IS null "
		+ "ORDER BY id ASC";

	private static final String _LIST_HISTORY_SQL =
		"SELECT * FROM lendings "
		+ "WHERE holding_id = ? AND return_date IS NOT null "
		+ "ORDER BY id DESC";

	private static final String _GET_CURRENT_LENDING_MAP_SQL_TPL =
		"SELECT * FROM lendings "
		+ "WHERE holding_id in (%s) AND return_date IS null "
		+ "ORDER BY id DESC";

	private static final String _GET_SQL =
		"SELECT * FROM lendings WHERE id = ?";

	private static final String _GET_CURRENT_LENDING_SQL =
		"SELECT * FROM lendings "
		+ "WHERE holding_id = ? AND return_date IS null "
		+ "ORDER BY id DESC";

	@Override
	public LendingDTO get(Integer id) {
		return fetchOne(this::populateDTO, _GET_SQL, id);
	}

	@Override
	public LendingDTO getCurrentLending(Integer holdingId) {
		return fetchOne(
			this::populateDTO, _GET_CURRENT_LENDING_SQL, holdingId);
	}

	@Override
	public Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> ids) {
		String questionMarks = StringUtils.repeat("?", ", ", ids.size());

		String sql = String.format(
			_GET_CURRENT_LENDING_MAP_SQL_TPL, questionMarks);

		Stream<LendingDTO> stream =
			listWith(this::populateDTO, sql, ids.toArray()).stream();

		return stream
			.collect(Collectors.toMap(LendingDTO::getHoldingId,
				Function.identity()));
	}


	@Override
	public List<LendingDTO> listHoldingHistory(int holdingId) {
		return listWith(this::populateDTO, _LIST_HISTORY_SQL, holdingId);
	}

	@Override
	public List<LendingDTO> listUserLendings(int userId) {
		return listWith(
			this::populateDTO, _LIST_USER_LENDINGS_SQL, userId);
	}

	@Override
	public List<LendingDTO> listUserHistory(int userId) {
		return listWith(
			this::populateDTO, _LIST_USER_HISTORY_SQL, userId);
	}

	@Override
	public Integer userLendingsCount(int userId) {
		return fetchOne(
			rs -> rs.getInt("total"), _USER_LENDINGS_COUNT_SQL, userId);
	}

	@Override
	public Integer userHistoryCount(int userId) {
		return fetchOne(
			rs -> rs.getInt("total"), _USER_HISTORY_COUNT_SQL, userId);
	}

	@Override
	public List<LendingDTO> listByRecordId(int recordId) {
		return listWith(this::populateDTO, _LIST_BY_RECORD_ID_SQL, recordId);
	}

	@Override
	public List<LendingDTO> listLendings(int offset, int limit) {
		return listWith(this::populateDTO, _LIST_LENDINGS_SQL, limit, offset);
	}

	@Override
	public int countLendings() {
		return fetchOne(rs -> rs.getInt("total"), _COUNT_LENDINGS_SQL);
	}

	@Override
	public int getCurrentLendingsCount(int userId) {
		return fetchOne(
			rs -> rs.getInt(1), _GET_CURRENT_LENDINGS_COUNT_SQL, userId);
	}

	@Override
	public boolean doLend(LendingDTO lending) {
		return executeUpdate(
			_DO_LEND_SQL, lending.getHoldingId(), lending.getUserId(),
			integerOrNullable(lending.getPreviousLendingId()),
			new Timestamp(lending.getExpectedReturnDate().getTime()),
			lending.getCreatedBy());
	}

	@Override
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executeBatchUpdate(
			dtoList, LendingDTO.class, _SAVE_FROM_V3_SQL,
			LendingDTO::getHoldingId, LendingDTO::getUserId,
			integerOrNullable(LendingDTO::getPreviousLendingId),
			dateOrNullable(LendingDTO::getExpectedReturnDate),
			LendingDTO::getCreatedBy, LendingDTO::getId,
			timestampOrNullable(LendingDTO::getCreated),
			dateOrNullable(LendingDTO::getReturnDate));
	}

	@Override
	public boolean doReturn(int lendingId) {
		return executeUpdate(_DO_RETURN_SQL, lendingId);
	}

	@Override
	public boolean doRenew(
		int lendingId, Date expectedReturnDate, int createdBy) {

		return onTransactionContext(con -> {
			executeUpdate(con, _DO_RETURN_SQL, lendingId);

			LendingDTO oldLending = get(lendingId);

			oldLending.setPreviousLendingId(lendingId);
			oldLending.setExpectedReturnDate(expectedReturnDate);
			oldLending.setCreatedBy(createdBy);

			executeUpdate(
				con, _DO_LEND_SQL, oldLending.getHoldingId(),
				oldLending.getUserId(),
				integerOrNullable(oldLending.getPreviousLendingId()),
				new Timestamp(oldLending.getExpectedReturnDate().getTime()),
				oldLending.getCreatedBy());

			return true;
		});
	}

	@Override
	public Integer countLentHoldings(int recordId) {
		return fetchOne(rs -> rs.getInt(1), _COUNT_LENT_HOLDINGS_SQL);
	}

	@Override
	public LendingDTO getLatest(int holdingSerial, int userId) {
		return fetchOne(
			this::populateDTO, _GET_LATEST_SQL, holdingSerial, userId);
	}

	private LendingDTO populateDTO(ResultSet rs) throws SQLException {
		LendingDTO dto = new LendingDTO();

		dto.setId(rs.getInt("id"));
		dto.setHoldingId(rs.getInt("holding_id"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setPreviousLendingId(rs.getInt("previous_lending_id"));
		dto.setExpectedReturnDate(rs.getTimestamp("expected_return_date"));
		dto.setReturnDate(rs.getTimestamp("return_date"));

		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));

		return dto;
	}
}
