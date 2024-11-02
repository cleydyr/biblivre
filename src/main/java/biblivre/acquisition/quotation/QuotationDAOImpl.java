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

import biblivre.core.AbstractDAO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class QuotationDAOImpl extends AbstractDAO implements QuotationDAO {

    @Override
    public Integer save(QuotationDTO dto) {
        return withTransactionContext(
                con -> {
                    int quotationId = this.getNextSerial("quotations_id_seq");
                    dto.setId(quotationId);

                    String sqlQuotations =
                            "INSERT INTO quotations (id, supplier_id, "
                                    + "response_date, expiration_date, delivery_time, "
                                    + "info, created_by, created) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                    PreparedStatement pstQuotations = con.prepareStatement(sqlQuotations);
                    pstQuotations.setInt(1, dto.getId());
                    pstQuotations.setInt(2, dto.getSupplierId());
                    pstQuotations.setDate(3, new java.sql.Date(dto.getResponseDate().getTime()));
                    pstQuotations.setDate(4, new java.sql.Date(dto.getExpirationDate().getTime()));
                    pstQuotations.setInt(5, dto.getDeliveryTime());
                    pstQuotations.setString(6, dto.getInfo());
                    pstQuotations.setInt(7, dto.getCreatedBy());
                    pstQuotations.setDate(8, new java.sql.Date(dto.getCreated().getTime()));

                    pstQuotations.executeUpdate();

                    String sqlRQuotations =
                            "INSERT INTO request_quotation "
                                    + "(request_id, quotation_id, quotation_quantity, unit_value, "
                                    + "response_quantity) "
                                    + "VALUES (?, ?, ?, ?, ?);";

                    PreparedStatement pstRQuotations = con.prepareStatement(sqlRQuotations);

                    for (RequestQuotationDTO rqdto : dto.getQuotationsList()) {
                        pstRQuotations.setInt(1, rqdto.getRequestId());
                        pstRQuotations.setInt(2, quotationId);
                        pstRQuotations.setInt(3, rqdto.getQuantity());
                        pstRQuotations.setFloat(4, rqdto.getUnitValue());
                        pstRQuotations.setInt(5, rqdto.getResponseQuantity());
                        pstRQuotations.addBatch();
                    }

                    pstRQuotations.executeBatch();

                    con.commit();
                    return quotationId;
                });
    }

    @Override
    public boolean update(QuotationDTO dto) {
        return withTransactionContext(
                con -> {
                    String sqlQuotations =
                            "UPDATE quotations SET supplier_id = ?, "
                                    + "response_date = ?, expiration_date = ?, delivery_time = ?, "
                                    + "info = ?, modified_by = ?, modified = now() "
                                    + "WHERE id = ?;";

                    PreparedStatement pstQuotations = con.prepareStatement(sqlQuotations);
                    pstQuotations.setInt(1, dto.getSupplierId());
                    pstQuotations.setDate(2, new java.sql.Date(dto.getResponseDate().getTime()));
                    pstQuotations.setDate(3, new java.sql.Date(dto.getExpirationDate().getTime()));
                    pstQuotations.setInt(4, dto.getDeliveryTime());
                    pstQuotations.setString(5, dto.getInfo());
                    pstQuotations.setInt(6, dto.getModifiedBy());
                    pstQuotations.setInt(7, dto.getId());

                    pstQuotations.executeUpdate();

                    String sqlItemQuotations =
                            "DELETE FROM request_quotation " + "WHERE quotation_id = ?;";
                    PreparedStatement pstItemQuotations = con.prepareStatement(sqlItemQuotations);
                    pstItemQuotations.setInt(1, dto.getId());
                    pstItemQuotations.executeUpdate();

                    String sqlRQuotations =
                            "INSERT INTO request_quotation "
                                    + "(request_id, quotation_id, quotation_quantity, unit_value, "
                                    + "response_quantity) "
                                    + "VALUES (?, ?, ?, ?, ?);";

                    PreparedStatement pstRQuotations = con.prepareStatement(sqlRQuotations);

                    for (RequestQuotationDTO rqdto : dto.getQuotationsList()) {
                        pstRQuotations.setInt(1, rqdto.getRequestId());
                        pstRQuotations.setInt(2, dto.getId());
                        pstRQuotations.setInt(3, rqdto.getQuantity());
                        pstRQuotations.setFloat(4, rqdto.getUnitValue());
                        pstRQuotations.setInt(5, rqdto.getResponseQuantity());
                        pstRQuotations.addBatch();
                    }

                    pstRQuotations.executeBatch();

                    con.commit();

                    return true;
                });
    }

    @Override
    public QuotationDTO get(int id) {
        String sql = "SELECT * FROM quotations " + "WHERE id = ?;";
        try (Connection connection = datasource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);

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
    public List<RequestQuotationDTO> listRequestQuotation(int quotationId) {
        List<RequestQuotationDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM request_quotation " + "WHERE quotation_id = ?;";
        try (Connection connection = datasource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, quotationId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(this.populateRequestQuotationDto(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
    }

    @Override
    public boolean delete(QuotationDTO dto) {
        String sql = "DELETE FROM quotations " + "WHERE id = ?;";
        try (Connection connection = datasource.getConnection();
                PreparedStatement pstInsert = connection.prepareStatement(sql)) {

            pstInsert.setInt(1, dto.getId());

            return pstInsert.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public DTOCollection<QuotationDTO> search(String value, int limit, int offset) {
        DTOCollection<QuotationDTO> list = new DTOCollection<>();

        String sql = getSearchSQL(value);

        String sqlCount = getSearchSQLCount(value);

        try (Connection connection = datasource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql);
                PreparedStatement pstCount = connection.prepareStatement(sqlCount)) {
            int i = 1;

            if (StringUtils.isNumeric(value)) {
                pst.setInt(i++, Integer.parseInt(value));
            } else {
                pst.setString(i++, "%" + value + "%");
            }
            pst.setInt(i++, limit);
            pst.setInt(i, offset);

            if (StringUtils.isNumeric(value)) {
                pst.setInt(1, Integer.parseInt(value));
            } else {
                pst.setString(1, "%" + value + "%");
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

    private static String getSearchSQLCount(String value) {
        return
                """
            SELECT count(*) as total FROM quotations q
            %s;
            """
                .formatted(
                        StringUtils.isNumeric(value)
                                ? "WHERE id = ?"
                                : "INNER JOIN suppliers s ON q.supplier_id = s.id WHERE trademark ilike ?");
    }

    private static String getSearchSQL(String value) {
        return
                """
            SELECT * FROM quotations q
            %s;
            ORDER BY q.id ASC LIMIT ? OFFSET ?;
            """
                .formatted(
                        StringUtils.isNumeric(value)
                                ? "WHERE id = ?"
                                : "INNER JOIN suppliers s ON q.supplier_id = s.id WHERE trademark ilike ?");
    }

    @Override
    public DTOCollection<QuotationDTO> list(Integer supplierId) {
        DTOCollection<QuotationDTO> list = new DTOCollection<>();

        String sql =
                """
                    SELECT * FROM quotations
                    WHERE supplier_id = ?
                        AND expiration_date >= now()::date
                    """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            // Add 1 day to expiration_date, else it'll check for expiration_date at midnight
            // (00:00H)
            // and it won't find quotations for the same day
            // sql.append("WHERE supplier_id = ? AND expiration_date + interval '1 day' >= now();
            // ");

            pst.setInt(1, supplierId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDto(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
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

    private RequestQuotationDTO populateRequestQuotationDto(ResultSet rs) throws Exception {
        RequestQuotationDTO dto = new RequestQuotationDTO();

        dto.setRequestId(rs.getInt("request_id"));
        dto.setQuotationId(rs.getInt("quotation_id"));
        dto.setQuantity(rs.getInt("quotation_quantity"));
        dto.setUnitValue(rs.getFloat("unit_value"));
        dto.setResponseQuantity(rs.getInt("response_quantity"));

        return dto;
    }
}
