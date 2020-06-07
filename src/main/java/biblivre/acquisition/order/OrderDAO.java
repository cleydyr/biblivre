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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;

public class OrderDAO extends AbstractDAO {
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

	public static OrderDAO getInstance(String schema) {
		return (OrderDAO) AbstractDAO.getInstance(OrderDAO.class, schema);
	}

	public OrderDTO get(Integer orderId) {
		return executePreparedStatement(this::populateDto, _GET_SQL, orderId);
	}

	public Integer save(OrderDTO dto) {
		return executePreparedStatement(pst ->  {
			int orderId = this.getNextSerial("orders_id_seq");

			pst.setInt(1, dto.getQuotationId());
			pst.setDate(2, new java.sql.Date(dto.getCreated().getTime()));
			pst.setInt(3, dto.getCreatedBy());
			pst.setString(4, dto.getInfo());
			pst.setString(5, dto.getStatus());
			pst.setString(6, dto.getInvoiceNumber());

			Date receiptDate = dto.getReceiptDate();

			if (receiptDate != null) {
				pst.setDate(7, new java.sql.Date(receiptDate.getTime()));
			} else {
				pst.setNull(7, java.sql.Types.DATE);
			}

			Float totalValue = dto.getTotalValue();

			if (totalValue != null) {
				pst.setFloat(8, totalValue);
			} else {
				pst.setNull(8, java.sql.Types.FLOAT);
			}

			Integer deliveryQuantity = dto.getDeliveredQuantity();

			if (deliveryQuantity != null) {
				pst.setInt(9, deliveryQuantity);
			} else {
				pst.setNull(9, java.sql.Types.INTEGER);
			}

			pst.setString(10, dto.getTermsOfPayment());
			pst.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
			pst.setInt(12, orderId);

			return pst.executeUpdate() > 0 ? orderId : 0;
		}, _SAVE_SQL);
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executePreparedStatement(pst -> {
			for (AbstractDTO abstractDto : dtoList) {
				OrderDTO dto = (OrderDTO) abstractDto;
				pst.setInt(1, dto.getQuotationId());
				pst.setDate(2, new java.sql.Date(dto.getCreated().getTime()));
				pst.setInt(3, dto.getCreatedBy());
				pst.setString(4, dto.getInfo());
				pst.setString(5, dto.getStatus());
				pst.setString(6, dto.getInvoiceNumber());

				Date receiptDate = dto.getReceiptDate();
				if (receiptDate != null) {
					pst.setDate(7, new java.sql.Date(receiptDate.getTime()));
				} else {
					pst.setNull(7, java.sql.Types.DATE);
				}

				Float totalValue = dto.getTotalValue();
				if (totalValue != null) {
					pst.setFloat(8, totalValue);
				} else {
					pst.setNull(8, java.sql.Types.FLOAT);
				}

				Integer deliveryQuantity = dto.getDeliveredQuantity();
				if (deliveryQuantity != null) {
					pst.setInt(9, deliveryQuantity);
				} else {
					pst.setNull(9, java.sql.Types.INTEGER);
				}

				pst.setString(10, dto.getTermsOfPayment());
				pst.setDate(
					11, new java.sql.Date(dto.getDeadlineDate().getTime()));
				pst.setInt(12, dto.getId());
				pst.addBatch();
			}

			pst.executeBatch();

			return true;
		}, _SAVE_FROM_V3_SQL);
	}

	public boolean update(OrderDTO dto) {
		return executePreparedStatement(pst -> {
			pst.setInt(1, dto.getQuotationId());
			pst.setDate(2, new java.sql.Date(dto.getCreated().getTime()));
			pst.setInt(3, dto.getCreatedBy());
			pst.setString(4, dto.getInfo());
			pst.setString(5, dto.getStatus());
			pst.setString(6, dto.getInvoiceNumber());

			Date receiptDate = dto.getReceiptDate();
			if (receiptDate != null) {
				pst.setDate(7, new java.sql.Date(receiptDate.getTime()));
			} else {
				pst.setNull(7, java.sql.Types.DATE);
			}

			Float totalValue = dto.getTotalValue();
			if (totalValue != null) {
				pst.setFloat(8, totalValue);
			} else {
				pst.setNull(8, java.sql.Types.FLOAT);
			}

			Integer deliveryQuantity = dto.getDeliveredQuantity();
			if (deliveryQuantity != null) {
				pst.setInt(9, deliveryQuantity);
			} else {
				pst.setNull(9, java.sql.Types.INTEGER);
			}

			pst.setString(10, dto.getTermsOfPayment());
			pst.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
			pst.setInt(12, dto.getId());
			return pst.executeUpdate() > 0;
		}, _UPDATE_SQL);
	}

	public boolean delete(OrderDTO dto) {
		return executePreparedStatement(pst -> {
			pst.setInt(1, dto.getId());
			return pst.executeUpdate() > 0;
		}, _DELETE_SQL);
	}

	public DTOCollection<OrderDTO> search(String value, int offset, int limit) {
		String sql = _buildSearchSQL(value);

		return executePreparedStatement(pst -> {
			DTOCollection<OrderDTO> list = new DTOCollection<OrderDTO>();

			int i = 1;
			if (StringUtils.isNumeric(value)) {
				pst.setInt(i++, Integer.valueOf(value));
			} else  if (StringUtils.isNotBlank(value)) {
				pst.setString(i++, "%" + value + "%");
				pst.setString(i++, "%" + value + "%");
				pst.setString(i++, "%" + value + "%");
			}
			pst.setInt(i++, offset);
			pst.setInt(i++, limit);

			int total = 0;

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				list.add(this.populateDto(rs));
				total++;
			}

			PagingDTO paging = new PagingDTO(total, limit, offset);

			list.setPaging(paging);

			return list;
		}, sql);
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
