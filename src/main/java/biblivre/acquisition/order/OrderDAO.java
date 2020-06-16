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
package biblivre.acquisition.order;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.NullableSQLObject;
import biblivre.core.PreparedStatementUtil;

public class OrderDAO extends AbstractDAO implements IOrderDAO {
	private static final String _GET_SQL =
		"SELECT * FROM orders WHERE id = ?; ";

	private static final String _SEARCH_SQL =
		"SELECT O.id, O.info, O.status, O.invoice_number, " +
			"O.receipt_date, O.total_value, O.delivered_quantity, " +
			"O.terms_of_payment, O.deadline_date, O.created, O.created_by, " +
			"O.modified, O.modified_by, O.quotation_id  FROM orders O " +
		"ORDER BY O.created ASC LIMIT ? OFFSET ?;";

	private static final String _SEARCH_SQL_WITH_NO_BLANK_VALUE =
		"SELECT O.id, O.info, O.status, O.invoice_number, " +
			"O.receipt_date, O.total_value, O.delivered_quantity, " +
			"O.terms_of_payment, O.deadline_date, O.created, O.created_by, " +
			"O.modified, O.modified_by, O.quotation_id  FROM orders O, " +
			"quotations Q, suppliers S, request_quotation RQ, requests R " +
		"WHERE O.quotation_id = Q.id AND Q.supplier_id = S.id " +
			"AND Q.id = RQ.quotation_id AND RQ.request_id = R.id " +
			"AND ((S.trademark ilike ?) OR (R.author ilike ?) " +
			"OR (R.item_title ilike ?)) " +
		"ORDER BY O.created ASC LIMIT ? OFFSET ?;";

	private static final String _SEARCH_SQL_WITH_NUMERIC_VALUE =
		"SELECT O.id, O.info, O.status, O.invoice_number, " +
			"O.receipt_date, O.total_value, O.delivered_quantity, " +
			"O.terms_of_payment, O.deadline_date, O.created, O.created_by, " +
			"O.modified, O.modified_by, O.quotation_id  FROM orders O " +
		"WHERE O.id = ? " +
		"ORDER BY O.created ASC LIMIT ? OFFSET ?;";

	private static final String _DELETE_SQL =
		"DELETE FROM orders WHERE id = ?;";

	private static final String _SAVE_SQL =
		"INSERT INTO orders " +
		"(quotation_id, created, created_by, info, status, invoice_number, " +
			"receipt_date, total_value, delivered_quantity, " +
			"terms_of_payment, deadline_date, id) " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";

	private static final String _SAVE_FROM_V3_SQL =
		"INSERT INTO orders (quotation_id, created, created_by, info, " +
		"status, invoice_number, receipt_date, total_value, " +
		"delivered_quantity, terms_of_payment, deadline_date, id) " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";

	private static final String _UPDATE_SQL =
		"UPDATE orders " +
		"SET quotation_id = ?, created = ?, created_by = ?, info = ?, " +
		"status = ?, invoice_number = ?, receipt_date = ?, total_value = ?, " +
		"delivered_quantity = ?, terms_of_payment = ?, deadline_date= ? " +
		"WHERE id = ?;";

	@Override
	public OrderDTO get(Integer orderId) {
		return fetchOne(this::populateDto, _GET_SQL, orderId);
	}

	@Override
	public Integer save(OrderDTO dto) {
		return onTransactionContext(con -> {
			int orderId = getNextSerial(con, "orders_id_seq");

			boolean update = executeUpdate(
				_SAVE_SQL, dto.getQuotationId(), dto.getCreated(),
				dto.getCreatedBy(), dto.getInfo(), dto.getStatus(),
				dto.getInvoiceNumber(), _getReceitptDate(dto),
				_getTotalValue(dto), _getDeliveredQuantity(dto),
				dto.getTermsOfPayment(), dto.getDeadlineDate(), orderId);

			return update ? orderId : 0;
		});

	}

	@Override
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executeBatchUpdate((pst, item) -> {
				OrderDTO dto = (OrderDTO) item;

				PreparedStatementUtil.setAllParameters(
					pst, dto.getQuotationId(), dto.getCreated(),
					dto.getCreatedBy(), dto.getInfo(), dto.getStatus(),
					dto.getInvoiceNumber(), _getReceitptDate(dto),
					_getTotalValue(dto), _getDeliveredQuantity(dto),
					dto.getTermsOfPayment(), dto.getDeadlineDate(),
					dto.getId());
			}, dtoList, _SAVE_FROM_V3_SQL);
	}

	@Override
	public boolean update(OrderDTO dto) {
		return executeUpdate(
			_UPDATE_SQL, dto.getQuotationId(), dto.getCreated(),
			dto.getCreatedBy(), dto.getInfo(), dto.getStatus(),
			dto.getInvoiceNumber(), _getReceitptDate(dto),
			_getTotalValue(dto), _getDeliveredQuantity(dto),
			dto.getTermsOfPayment(), dto.getDeadlineDate(), dto.getId());
	}

	@Override
	public boolean delete(OrderDTO dto) {
		return executeUpdate(_DELETE_SQL, dto.getId());
	}

	@Override
	public DTOCollection<OrderDTO> search(String value, int offset, int limit) {
		String sql = _buildSearchSQL(value);

		boolean isNumericValue = StringUtils.isNumeric(value);

		List<Object> parameters = new ArrayList<>();

		if (isNumericValue) {
			parameters.add(Integer.valueOf(value));
		} else  if (StringUtils.isNotBlank(value)) {
			String likeValue = "%" + value + "%";

			parameters.addAll(Arrays.asList(likeValue, likeValue, likeValue));
		}

		return pagedListWith(
			this::populateDto, sql, limit, offset, parameters.toArray());
	}

	private String _buildSearchSQL(String value) {
		if (StringUtils.isNumeric(value)) {
			return _SEARCH_SQL_WITH_NUMERIC_VALUE;
		}
		else if (StringUtils.isNotBlank(value)) {
			return _SEARCH_SQL_WITH_NO_BLANK_VALUE;
		}
		else {
			return _SEARCH_SQL;
		}
	}

	private Object _getDeliveredQuantity(OrderDTO dto) {
		return dto.getDeliveredQuantity() != null ? dto.getDeliveredQuantity() :
		 NullableSQLObject.INTEGER;
	}

	private Object _getTotalValue(OrderDTO dto) {
		return dto.getTotalValue()	!= null ? dto.getTotalValue() :
			NullableSQLObject.FLOAT;
	}

	private Object _getReceitptDate(OrderDTO dto) {
		return dto.getReceiptDate() != null ? dto.getReceiptDate() :
			NullableSQLObject.DATE;
	}

	private OrderDTO populateDto(ResultSet rs) throws Exception {
		OrderDTO dto = new OrderDTO();
		dto.setId(rs.getInt("id"));
		dto.setQuotationId(rs.getInt("quotation_id"));
		dto.setInfo(rs.getString("info"));
		dto.setStatus(rs.getString("status"));
		dto.setInvoiceNumber(rs.getString("invoice_number"));
		dto.setReceiptDate(rs.getDate("receipt_date"));
		dto.setTotalValue(rs.getFloat("total_value"));
		dto.setDeliveredQuantity(rs.getInt("delivered_quantity"));
		dto.setTermsOfPayment(rs.getString("terms_of_payment"));
		dto.setDeadlineDate(rs.getDate("deadline_date"));

		dto.setCreated(rs.getDate("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getDate("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}
}
