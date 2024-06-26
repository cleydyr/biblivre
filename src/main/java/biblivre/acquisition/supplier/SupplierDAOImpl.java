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
package biblivre.acquisition.supplier;

import biblivre.core.AbstractDAO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SupplierDAOImpl extends AbstractDAO implements SupplierDAO {

    @Override
    public int save(SupplierDTO dto) {

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "INSERT INTO suppliers ( "
                            + "trademark, supplier_name, supplier_number, "
                            + "vat_registration_number, address, address_number, "
                            + "address_complement, area, city, state, country, "
                            + "zip_code, telephone_1, telephone_2, telephone_3, "
                            + "telephone_4, contact_1, contact_2, contact_3, "
                            + "contact_4, info, url, email, created_by) "
                            + "VALUES ("
                            + StringUtils.repeat("?", ", ", 24)
                            + ")";

            PreparedStatement pstInsert =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstInsert.setString(1, dto.getTrademark());
            pstInsert.setString(2, dto.getName());
            pstInsert.setString(3, dto.getSupplierNumber());
            pstInsert.setString(4, dto.getVatRegistrationNumber());
            pstInsert.setString(5, dto.getAddress());
            pstInsert.setString(6, dto.getAddressNumber());
            pstInsert.setString(7, dto.getComplement());
            pstInsert.setString(8, dto.getArea());
            pstInsert.setString(9, dto.getCity());
            pstInsert.setString(10, dto.getState());
            pstInsert.setString(11, dto.getCountry());
            pstInsert.setString(12, dto.getZipCode());
            pstInsert.setString(13, dto.getTelephone1());
            pstInsert.setString(14, dto.getTelephone2());
            pstInsert.setString(15, dto.getTelephone3());
            pstInsert.setString(16, dto.getTelephone4());
            pstInsert.setString(17, dto.getContact1());
            pstInsert.setString(18, dto.getContact2());
            pstInsert.setString(19, dto.getContact3());
            pstInsert.setString(20, dto.getContact4());
            pstInsert.setString(21, dto.getInfo());
            pstInsert.setString(22, dto.getUrl());
            pstInsert.setString(23, dto.getEmail());
            pstInsert.setInt(24, dto.getCreatedBy());

            pstInsert.executeUpdate();

            try (ResultSet generatedKeys = pstInsert.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new DAOException(new Exception("Unexpected empty generated key result set"));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void update(SupplierDTO dto) {
        String sql =
                """
                        UPDATE suppliers
                        SET trademark = ?, supplier_name = ?, supplier_number = ?, vat_registration_number = ?,
                            address = ?, address_number = ?, address_complement = ?, area = ?, city = ?,
                            state = ?, country = ?, zip_code = ?, telephone_1 = ?, telephone_2 = ?,
                            telephone_3 = ?, telephone_4 = ?, contact_1 = ?, contact_2 = ?,
                            contact_3 = ?, contact_4 = ?, info = ?, url = ?, email = ?,
                            modified = now(), modified_by = ?
                        WHERE id = ? RETURNING id
                        """;
        try (Connection con = datasource.getConnection();
                PreparedStatement pstInsert = con.prepareStatement(sql)) {

            PreparedStatementUtil.setAllParameters(
                    pstInsert,
                    dto.getTrademark(),
                    dto.getName(),
                    dto.getSupplierNumber(),
                    dto.getVatRegistrationNumber(),
                    dto.getAddress(),
                    dto.getAddressNumber(),
                    dto.getComplement(),
                    dto.getArea(),
                    dto.getCity(),
                    dto.getState(),
                    dto.getCountry(),
                    dto.getZipCode(),
                    dto.getTelephone1(),
                    dto.getTelephone2(),
                    dto.getTelephone3(),
                    dto.getTelephone4(),
                    dto.getContact1(),
                    dto.getContact2(),
                    dto.getContact3(),
                    dto.getContact4(),
                    dto.getInfo(),
                    dto.getUrl(),
                    dto.getEmail(),
                    dto.getModifiedBy(),
                    dto.getId());

            try (ResultSet resultSet = pstInsert.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }

                throw new Exception("Unexpected: No row updated");
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = datasource.getConnection()) {

            String sql = "DELETE FROM suppliers " + "WHERE id = ?;";

            PreparedStatement pstInsert = con.prepareStatement(sql);
            pstInsert.setInt(1, id);

            return pstInsert.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public SupplierDTO get(int id) {
        try (Connection con = datasource.getConnection()) {
            String sql = "SELECT * FROM suppliers " + "WHERE id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
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
    public DTOCollection<SupplierDTO> search(String value, int limit, int offset) {
        DTOCollection<SupplierDTO> list = new DTOCollection<>();

        try (Connection con = datasource.getConnection()) {

            StringBuilder sql = new StringBuilder("SELECT * FROM suppliers ");
            if (StringUtils.isNotBlank(value)) {
                sql.append("WHERE trademark ilike ? ");
                sql.append("OR supplier_name ilike ? ");
                sql.append("OR supplier_number = ? ");
            }
            sql.append("ORDER BY id ASC LIMIT ? OFFSET ? ");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (StringUtils.isNotBlank(value)) {
                pst.setString(i++, "%" + value + "%");
                pst.setString(i++, "%" + value + "%");
                pst.setString(i++, value);
            }
            pst.setInt(i++, limit);
            pst.setInt(i, offset);

            StringBuilder sqlCount = new StringBuilder("SELECT count(*) as total FROM suppliers ");
            if (StringUtils.isNotBlank(value)) {
                sqlCount.append("WHERE trademark ilike ? ");
                sqlCount.append("OR supplier_name ilike ? ");
                sqlCount.append("OR supplier_number = ? ");
            }

            PreparedStatement pstCount = con.prepareStatement(sqlCount.toString());
            if (StringUtils.isNotBlank(value)) {
                pstCount.setString(1, "%" + value + "%");
                pstCount.setString(2, "%" + value + "%");
                pstCount.setString(3, value);
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

    private SupplierDTO populateDto(ResultSet rs) throws Exception {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(rs.getInt("id"));
        dto.setTrademark(rs.getString("trademark"));
        dto.setName(rs.getString("supplier_name"));
        dto.setSupplierNumber(rs.getString("supplier_number"));
        dto.setVatRegistrationNumber(rs.getString("vat_registration_number"));
        dto.setAddress(rs.getString("address"));
        dto.setAddressNumber(rs.getString("address_number"));
        dto.setComplement(rs.getString("address_complement"));
        dto.setArea(rs.getString("area"));
        dto.setCity(rs.getString("city"));
        dto.setState(rs.getString("state"));
        dto.setCountry(rs.getString("country"));
        dto.setZipCode(rs.getString("zip_code"));
        dto.setTelephone1(rs.getString("telephone_1"));
        dto.setTelephone2(rs.getString("telephone_2"));
        dto.setTelephone3(rs.getString("telephone_3"));
        dto.setTelephone4(rs.getString("telephone_4"));
        dto.setContact1(rs.getString("contact_1"));
        dto.setContact2(rs.getString("contact_2"));
        dto.setContact3(rs.getString("contact_3"));
        dto.setContact4(rs.getString("contact_4"));
        dto.setInfo(rs.getString("info"));
        dto.setUrl(rs.getString("url"));
        dto.setEmail(rs.getString("email"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));
        dto.setModified(rs.getTimestamp("modified"));
        dto.setModifiedBy(rs.getInt("modified_by"));
        return dto;
    }
}
