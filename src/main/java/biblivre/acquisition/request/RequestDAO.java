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

import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PreparedStatementUtil;

public class RequestDAO extends AbstractDAO {

	private static final String _UPDATE_STATUS_SQL =
		"UPDATE requests" +
		"SET status = ? " +
		"WHERE id IN (" +
			"SELECT r.id FROM requests r " +
			"INNER JOIN request_quotation rq ON rq.request_id = r.id " +
			"INNER JOIN quotation q ON q.id = rq.quotation_id " +
			"INNER JOIN order o ON o.quotation_id = q.id " +
			"WHERE o.id = ?" +
		"); ";

	private static final String _UPDATE_SQL =
		"UPDATE requests" +
		"SET author = ?, item_title = ?, item_subtitle = ?, " +
			"edition_number = ?, publisher = ?, info = ?, requester = ?, " +
			"quantity = ?, modified = now(), modified_by = ? " +
		"WHERE id = ?;";

	private static final String _SEARCH_ALL_SQL =
		"SELECT * FROM requests ORDER BY id ASC LIMIT ? OFFSET ?";

	private static final String _SEARCH_KEYWORD_SQL =
		"SELECT * FROM requests " +
		"WHERE requester ilike ? OR author ilike ? OR item_title ilike " +
		"ORDER BY id ASC LIMIT ? OFFSET ?";

	private static final String _SAVE_SQL =
		"INSERT INTO requests " +
		"(requester, author, item_title, item_subtitle, edition_number, " +
			"publisher, info, status, quantity, created_by) " +
		"VALUES (" + StringUtils.repeat("?", ", ", 10) + ");";

	private static final String _SAVE_FROM_V3_SQL =
			"INSERT INTO requests " +
			"(requester, author, item_title, item_subtitle, edition_number, " +
				"publisher, info, status, quantity, created_by) " +
			"VALUES (" + StringUtils.repeat("?", ", ", 10) + ");";

	public static RequestDAO getInstance(String schema) {
		return (RequestDAO) AbstractDAO.getInstance(RequestDAO.class, schema);
	}

	public boolean save(RequestDTO dto) {
		return executeUpdate(
			_SAVE_SQL, dto.getRequester(), dto.getAuthor(), dto.getTitle(),
			dto.getSubtitle(), dto.getEditionNumber(), dto.getPublisher(),
			dto.getInfo(), dto.getStatus().toString(), dto.getQuantity(),
			dto.getCreatedBy());
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executeBatchUpdate((pst, abstractDto) -> {
			RequestDTO dto = (RequestDTO) abstractDto;

			PreparedStatementUtil.setAllParameters(
				pst, dto.getRequester(), dto.getAuthor(), dto.getTitle(),
				dto.getSubtitle(), dto.getEditionNumber(),
				dto.getPublisher(), dto.getInfo(),
				dto.getStatus().toString(), dto.getQuantity(),
				dto.getCreatedBy(), dto.getId()
			);
		}, dtoList, _SAVE_FROM_V3_SQL);
	}

	public RequestDTO get(int id) {
		return fetchOne(
			this::populateDto, "SELECT * FROM requests WHERE id = ?;", id);
	}

	public DTOCollection<RequestDTO> search(
		String value, int limit, int offset) {

		if (StringUtils.isNotBlank(value)) {
			String likeValue = "%" + value + "%";

			return pagedListWith(
				this::populateDto, _SEARCH_KEYWORD_SQL, limit, offset, value,
				likeValue, likeValue, value);
		}
		else {
			return pagedListWith(
				this::populateDto, _SEARCH_ALL_SQL, limit, offset);
		}
	}

	public boolean update(RequestDTO dto) {
		return executeUpdate(
			_UPDATE_SQL, dto.getAuthor(), dto.getTitle(), dto.getSubtitle(),
			dto.getEditionNumber(), dto.getPublisher(), dto.getInfo(),
			dto.getRequester(), dto.getQuantity(), dto.getModifiedBy(),
			dto.getId());
	}

	public boolean updateRequestStatus(int orderId, RequestStatus status) {
		return executeUpdate(_UPDATE_STATUS_SQL, status.toString(), orderId);
	}

	public boolean delete(RequestDTO dto) {
		return executeUpdate("DELETE FROM requests WHERE id = ?", dto.getId());
	}

	private RequestDTO populateDto(ResultSet rs) throws Exception {
		RequestDTO dto = new RequestDTO();
		dto.setId(rs.getInt("id"));
		dto.setRequester(rs.getString("requester"));
		dto.setAuthor(rs.getString("author"));
		dto.setTitle(rs.getString("item_title"));
		dto.setSubtitle(rs.getString("item_subtitle"));
		dto.setEditionNumber(rs.getString("edition_number"));
		dto.setPublisher(rs.getString("publisher"));
		dto.setInfo(rs.getString("info"));
		dto.setStatus(RequestStatus.fromString(rs.getString("status")));
		dto.setQuantity(rs.getInt("quantity"));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

}
