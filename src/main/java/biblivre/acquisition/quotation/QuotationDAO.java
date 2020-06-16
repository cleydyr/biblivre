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

import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.LegacyAbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PreparedStatementUtil;

public class QuotationDAO extends LegacyAbstractDAO {
	private static final String _LIST_SQL =
		"SELECT * FROM quotations " +
		"WHERE supplier_id = ? AND expiration_date >= now()::date;";

	private static final String _SEARCH_KEYWORD_SQL =
		"SELECT * FROM quotations q " +
		"INNER JOIN suppliers s " +
		"ON q.supplier_id = s.id " +
		"WHERE trademark ilike ? " +
		"ORDER BY q.id ASC LIMIT ? OFFSET ?";

	private static final String _SEARCH_NUMERIC_SQL =
		"SELECT * FROM quotations q" +
		"WHERE id = ? " +
		"ORDER BY q.id ASC " +
		"LIMIT ? OFFSET ?";

	private static final String _DELETE_SQL =
		"DELETE FROM quotations " +
		"WHERE id = ?;";

	private static final String _LIST_RQ_SQL =
		"SELECT * FROM request_quotation " +
		"WHERE quotation_id = ?;";

	private static final String _GET_SQL =
		"SELECT * FROM quotations " +
		"WHERE id = ?;";

	private static final String _INSERT_RQ_SQL =
		"INSERT INTO request_quotation " +
		"(request_id, quotation_id, quotation_quantity, unit_value, " +
			"response_quantity) " +
		"VALUES (?, ?, ?, ?, ?);";

	private static final String _DELETE_RQ_SQL =
		"DELETE FROM request_quotation " +
		"WHERE quotation_id = ?;";

	private static final String _UPDATE_SQL =
		"UPDATE quotations " +
		"SET supplier_id = ?, response_date = ?, expiration_date = ?, " +
			"delivery_time = ?, info = ?, modified_by = ?, modified = now() " +
		"WHERE id = ?;";

	private static final String _SAVE_SQL =
		"INSERT INTO quotations " +
		"(id, supplier_id, response_date, expiration_date, delivery_time, " +
			"info, created_by, created) " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String _SAVE_RQ_SQL =
		"INSERT INTO request_quotation " +
		"(request_id, quotation_id, quotation_quantity, unit_value, " +
			"response_quantity)" +
		"VALUES (?, ?, ?, ?, ?);";

	public static QuotationDAO getInstance(String schema) {
		return (QuotationDAO) LegacyAbstractDAO.getInstance(
			QuotationDAO.class, schema);
	}

	public Integer save(QuotationDTO dto) {
		int quotationId = this.getNextSerial("quotations_id_seq");

		onTransactionContext(con -> {
			executeUpdate(
				_SAVE_SQL, quotationId, dto.getSupplierId(),
				dto.getResponseDate(), dto.getExpirationDate(),
				dto.getDeliveryTime(), dto.getInfo(), dto.getCreatedBy(),
				dto.getCreated());

			executeBatchUpdate(
				dto.getQuotationsList(), RequestQuotationDTO.class,
				_SAVE_RQ_SQL, RequestQuotationDTO::getRequestId,
				__ -> quotationId,
				RequestQuotationDTO::getQuantity,
				RequestQuotationDTO::getUnitValue,
				RequestQuotationDTO::getResponseQuantity);
		});

		return quotationId;
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		if (dtoList == null || dtoList.isEmpty()) {
			return true;
		}

		AbstractDTO abstractDto = dtoList.get(0);

		if (abstractDto instanceof QuotationDTO) {
			return this.saveQuotationFromBiblivre3(dtoList);
		} else if (abstractDto instanceof RequestQuotationDTO) {
			return this.saveRequestQuotationFromBiblivre3(dtoList);
		} else {
			throw new IllegalArgumentException("List is not of QuotationDTO " +
				"or RequestQuotationDTO objects.");
		}

	}

	private boolean saveQuotationFromBiblivre3(
		List<? extends AbstractDTO> dtoList) {

		return executeBatchUpdate((pst, item) -> {
			QuotationDTO quotation = (QuotationDTO) item;

			PreparedStatementUtil.setAllParameters(
				pst, quotation.getId(), quotation.getSupplierId(),
				quotation.getResponseDate(),
				quotation.getExpirationDate(),
				quotation.getDeliveryTime(), quotation.getInfo(),
				quotation.getCreatedBy(), quotation.getCreated());
		}, dtoList, _SAVE_SQL);
	}

	private boolean saveRequestQuotationFromBiblivre3(
			List<? extends AbstractDTO> dtoList) {

		return executeBatchUpdate((pst, item) -> {
			RequestQuotationDTO requestQuotation =
					(RequestQuotationDTO) item;

			PreparedStatementUtil.setAllParameters(
				pst, requestQuotation.getRequestId(),
				requestQuotation.getQuotationId(),
				requestQuotation.getQuantity(),
				requestQuotation.getUnitValue(),
				requestQuotation.getResponseQuantity());
		}, dtoList, _SAVE_RQ_SQL);
	}


	public boolean update(QuotationDTO dto) {
		onTransactionContext(con -> {
			executeUpdate(
				_UPDATE_SQL, dto.getSupplierId(),
				dto.getResponseDate(), dto.getExpirationDate(),
				dto.getDeliveryTime(), dto.getInfo(), dto.getModifiedBy(),
				dto.getId());

			executeUpdate(_DELETE_RQ_SQL, dto.getId());

			executeBatchUpdate((pst, item) -> {
				PreparedStatementUtil.setAllParameters(
					pst, item.getRequestId(), dto.getId(),
					item.getQuantity(), item.getUnitValue(),
					item.getResponseQuantity());
			}, dto.getQuotationsList(), _INSERT_RQ_SQL);
		});

		return true;
	}

	public QuotationDTO get(int id) {
		return fetchOne(this::populateDto, _GET_SQL, id);
	}

	public List<RequestQuotationDTO> listRequestQuotation(int quotationId) {
		return listWith(
			this::populateRequestQuotationDto, _LIST_RQ_SQL, quotationId);
	}

	public boolean delete(QuotationDTO dto) {
		return executeUpdate(_DELETE_SQL, dto.getId());
	}

	public DTOCollection<QuotationDTO> search(
		String value, int limit, int offset) {

		boolean isNumeric = StringUtils.isNumeric(value);

		String sql = isNumeric ? _SEARCH_NUMERIC_SQL : _SEARCH_KEYWORD_SQL;

		Object keyword = isNumeric ? Integer.valueOf(value) : "%" + value + "%";

		return pagedListWith(
			this::populateDto, sql, limit, offset, keyword);
	}

	public DTOCollection<QuotationDTO> list(Integer supplierId) {
		List<QuotationDTO> list =
			listWith(this::populateDto, _LIST_SQL, supplierId);

		DTOCollection<QuotationDTO> collection =
			new DTOCollection<QuotationDTO>();

		for (QuotationDTO quotation : list) {
			collection.add(quotation);
		}

		return collection;
	}

	private QuotationDTO populateDto(ResultSet rs) throws Exception {
		QuotationDTO dto = new QuotationDTO();

		dto.setId(rs.getInt("id"));
		dto.setSupplierId(rs.getInt("supplier_id"));
		dto.setResponseDate(rs.getDate("response_date"));
		dto.setExpirationDate(rs.getDate("expiration_date"));
		dto.setDeliveryTime(rs.getInt("delivery_time"));
		dto.setInfo(rs.getString("info"));

		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));

		return dto;
	}

	private RequestQuotationDTO populateRequestQuotationDto(ResultSet rs)
		throws Exception {
		RequestQuotationDTO dto = new RequestQuotationDTO();

		dto.setRequestId(rs.getInt("request_id"));
		dto.setQuotationId(rs.getInt("quotation_id"));
		dto.setQuantity(rs.getInt("quotation_quantity"));
		dto.setUnitValue(rs.getFloat("unit_value"));
		dto.setResponseQuantity(rs.getInt("response_quantity"));

		return dto;
	}
}
