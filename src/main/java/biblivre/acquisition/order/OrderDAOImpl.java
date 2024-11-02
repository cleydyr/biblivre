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

import biblivre.core.AbstractDAO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class OrderDAOImpl extends AbstractDAO implements OrderDAO {

    @Override
    public OrderDTO get(int orderId) {
        try (Connection con = datasource.getConnection();
                PreparedStatement pst =
                        con.prepareStatement(" SELECT * FROM orders WHERE id = ?; ")) {

            pst.setInt(1, orderId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return this.populateDto(rs);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return null;
    }

    @Override
    public Integer save(OrderDTO dto) {
        String sqlInsert =
                """
                INSERT INTO orders (quotation_id, created
                        created_by, info, status, invoice_number,
                        receipt_date, total_value, delivered_quantity,
                        terms_of_payment, deadline_date, id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sqlInsert)) {

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
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<OrderDTO> listBuyOrders(String status, int offset, int limit) {
        ArrayList<OrderDTO> requestList = new ArrayList<>();

        boolean setStatus =
                StringUtils.isNotBlank(status) && (status.equals("0") || status.equals("1"));

        return setStatus
                ? this.doListBuyOrders(status, offset, limit, requestList)
                : this.doListBuyOrders(offset, limit, requestList);
    }

    private List<OrderDTO> doListBuyOrders(
            String status, int offset, int limit, ArrayList<OrderDTO> requestList) {
        String sql =
                """
                SELECT * FROM orders
                WHERE status = ?
                ORDER BY created ASC offset ? limit ?;
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            PreparedStatementUtil.setAllParameters(pst, status, offset, limit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                OrderDTO dto = this.populateDto(rs);
                requestList.add(dto);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return requestList;
    }

    private List<OrderDTO> doListBuyOrders(int offset, int limit, ArrayList<OrderDTO> requestList) {
        String sql =
                """
                SELECT * FROM orders
                ORDER BY created ASC offset ? limit ?;
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            PreparedStatementUtil.setAllParameters(pst, offset, limit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                OrderDTO dto = this.populateDto(rs);
                requestList.add(dto);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return requestList;
    }

    @Override
    public boolean update(OrderDTO dto) {
        String sqlInsert =
                """
                UPDATE orders
                SET quotation_id = ?, created = ?
                    created_by = ?, info = ?, status = ?
                    invoice_number = ?, receipt_date = ?, total_value = ?
                    delivered_quantity = ?, terms_of_payment = ?, deadline_date= ?
                WHERE id = ?
                        """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sqlInsert)) {

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
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public DTOCollection<OrderDTO> search(String value, int offset, int limit) {
        DTOCollection<OrderDTO> list = new DTOCollection<>();
        String sql = getSearchSQL(value);

        String sqlCount = getSearchCounterSQL(value);
        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            int i = 1;

            String likeValue = "%" + value + "%";

            if (StringUtils.isNumeric(value)) {
                pst.setInt(i++, Integer.parseInt(value));
            } else if (StringUtils.isNotBlank(value)) {
                pst.setString(i++, likeValue);
                pst.setString(i++, likeValue);
                pst.setString(i++, likeValue);
            }
            pst.setInt(i++, offset);
            pst.setInt(i, limit);

            PreparedStatement pstCount = con.prepareStatement(sqlCount);

            if (StringUtils.isNumeric(value)) {
                pst.setInt(1, Integer.parseInt(value));
            } else if (StringUtils.isNotBlank(value)) {
                pst.setString(1, likeValue);
                pst.setString(2, likeValue);
                pst.setString(3, likeValue);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDto(rs));
            }

            ResultSet rsCount = pstCount.executeQuery();
            if (rsCount.next()) {
                int total = rsCount.getInt("total");

                PagingDTO paging = new PagingDTO(total, limit, offset);
                list.setPaging(paging);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    private static String getSearchCounterSQL(String value) {
        StringBuilder sqlCount = new StringBuilder("SELECT count(*) as total FROM orders O ");

        if (StringUtils.isNumeric(value)) {
            sqlCount.append("WHERE O.id = ? ");
        } else if (StringUtils.isNotBlank(value)) {
            sqlCount.append(
                    """
                            , quotations Q, suppliers S, request_quotation RQ, requests R
                            WHERE O.quotation_id = Q.id AND Q.supplier_id = S.id AND Q.id = RQ.quotation_id
                            AND ((S.trademark ilike ?) OR (R.author ilike ?) OR (R.item_title ilike ?))
                            """);
        }
        return sqlCount.toString();
    }

    private static String getSearchSQL(String value) {
        StringBuilder sql =
                new StringBuilder(
                        """
                        SELECT O.id, O.info, O.status, O.invoice_number, O.receipt_date, O.total_value, O.delivered_quantity,
                        O.terms_of_payment, O.deadline_date, O.created, O.created_by, O.modified, O.modified_by, O.quotation_id
                        FROM orders O
                                """);

        if (StringUtils.isNumeric(value)) {
            sql.append("WHERE O.id = ? ");
        } else if (StringUtils.isNotBlank(value)) {
            sql.append(
                    """
                            , quotations Q, suppliers S, request_quotation RQ, requests R
                            WHERE O.quotation_id = Q.id AND Q.supplier_id = S.id AND Q.id = RQ.quotation_id
                            AND RQ.request_id = R.id AND ((S.trademark ilike ?) OR (R.author ilike ?) OR (R.item_title ilike ?))
                            """);
        }

        sql.append("ORDER BY O.created ASC LIMIT ? OFFSET ?;");
        return sql.toString();
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
