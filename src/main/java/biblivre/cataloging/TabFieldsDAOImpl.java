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
package biblivre.cataloging;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TabFieldsDAOImpl extends AbstractDAO implements TabFieldsDAO {

    @Override
    public List<BriefTabFieldFormatDTO> listBriefFormats(RecordType recordType) {
        List<BriefTabFieldFormatDTO> list = new ArrayList<>();

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT * FROM "
                            + recordType
                            + "_brief_formats ORDER BY sort_order, datafield;";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                try {
                    list.add(this.populateFormatsDTO(rs));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
    }

    @Override
    public boolean insertBriefFormat(
            BriefTabFieldFormatDTO dto, RecordType recordType, int loggedUser) {

        try (Connection con = datasource.getConnection()) {

            String sql =
                    " INSERT INTO "
                            + recordType
                            + "_brief_formats "
                            + " (datafield, format, sort_order, created_by) "
                            + " VALUES (?, ?, ?, ?); ";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, dto.getDatafieldTag());
            pst.setString(2, dto.getFormat());
            pst.setInt(3, dto.getSortOrder());
            pst.setInt(4, dto.getCreatedBy());
            pst.executeUpdate();

        } catch (Exception e) {
            throw new DAOException(e);
        }

        return true;
    }

    @Override
    public boolean updateBriefFormats(
            List<BriefTabFieldFormatDTO> briefFormats, RecordType recordType, int loggedUser) {

        try (Connection con = datasource.getConnection()) {

            String sql =
                    " UPDATE "
                            + recordType
                            + "_brief_formats "
                            + " SET sort_order = ?, "
                            + " format = ?, "
                            + " modified = now(), "
                            + " modified_by = ? "
                            + " WHERE datafield = ?; ";

            PreparedStatement pst = con.prepareStatement(sql);

            for (BriefTabFieldFormatDTO dto : briefFormats) {
                pst.setInt(1, dto.getSortOrder());
                pst.setString(2, dto.getFormat());
                pst.setInt(3, loggedUser);
                pst.setString(4, dto.getDatafieldTag());

                pst.addBatch();
            }

            pst.executeBatch();

        } catch (Exception e) {
            throw new DAOException(e);
        }

        return true;
    }

    @Override
    public boolean deleteBriefFormat(String datafield, RecordType recordType) {

        try (Connection con = datasource.getConnection()) {

            String sql =
                    " DELETE FROM " + recordType + "_brief_formats " + " WHERE datafield = ?; ";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, datafield);

            pst.executeUpdate();

        } catch (Exception e) {
            throw new DAOException(e);
        }

        return true;
    }

    @Override
    public boolean deleteFormTabDatafield(String datafield, RecordType recordType) {
        return withTransactionContext(
                con -> {
                    String subfieldSql =
                            " DELETE FROM "
                                    + recordType
                                    + "_form_subfields "
                                    + " WHERE datafield = ?; ";

                    PreparedStatement subfieldPst = con.prepareStatement(subfieldSql);
                    subfieldPst.setString(1, datafield);

                    subfieldPst.executeUpdate();

                    String datafieldSql =
                            " DELETE FROM "
                                    + recordType
                                    + "_form_datafields "
                                    + " WHERE datafield = ?; ";

                    PreparedStatement datafieldPst = con.prepareStatement(datafieldSql);
                    datafieldPst.setString(1, datafield);

                    datafieldPst.executeUpdate();

                    return true;
                });
    }

    @Override
    public List<FormTabDatafieldDTO> listFields(RecordType recordType) {
        List<FormTabDatafieldDTO> list = new ArrayList<>();
        Map<String, FormTabDatafieldDTO> hash = new HashMap<>();

        try (Connection con = datasource.getConnection()) {
            String sqlDatafields =
                    "SELECT * FROM "
                            + recordType
                            + "_form_datafields ORDER BY sort_order, datafield;;";

            Statement stDatafields = con.createStatement();
            ResultSet rsDatafields = stDatafields.executeQuery(sqlDatafields);
            while (rsDatafields.next()) {
                try {
                    FormTabDatafieldDTO datafield = this.populateDatafieldDTO(rsDatafields);

                    hash.put(datafield.getDatafield(), datafield);
                    list.add(datafield);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            String sqlSubfields =
                    "SELECT * FROM "
                            + recordType
                            + "_form_subfields ORDER BY sort_order, datafield, subfield;";
            Statement stSubfields = con.createStatement();
            ResultSet rsSubfields = stSubfields.executeQuery(sqlSubfields);

            while (rsSubfields.next()) {
                try {
                    FormTabSubfieldDTO subfield = this.populateSubfieldDTO(rsSubfields);

                    FormTabDatafieldDTO datafield = hash.get(subfield.getDatafield());

                    if (datafield != null) {
                        datafield.addSubfield(subfield);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
    }

    private BriefTabFieldFormatDTO populateFormatsDTO(ResultSet rs) throws SQLException {
        BriefTabFieldFormatDTO dto = new BriefTabFieldFormatDTO();

        dto.setDatafieldTag(rs.getString("datafield"));
        dto.setFormat(rs.getString("format"));
        dto.setSortOrder(rs.getInt("sort_order"));

        return dto;
    }

    private FormTabDatafieldDTO populateDatafieldDTO(ResultSet rs) throws SQLException {
        FormTabDatafieldDTO dto = new FormTabDatafieldDTO();

        dto.setDatafield(rs.getString("datafield"));
        dto.setCollapsed(rs.getBoolean("collapsed"));
        dto.setRepeatable(rs.getBoolean("repeatable"));
        dto.setIndicator1(rs.getString("indicator_1"));
        dto.setIndicator2(rs.getString("indicator_2"));
        dto.setMaterialType(rs.getString("material_type"));
        dto.setSortOrder(rs.getInt("sort_order"));

        return dto;
    }

    private FormTabSubfieldDTO populateSubfieldDTO(ResultSet rs) throws SQLException {
        FormTabSubfieldDTO dto = new FormTabSubfieldDTO();

        dto.setDatafield(rs.getString("datafield"));
        dto.setSubfield(rs.getString("subfield"));
        dto.setCollapsed(rs.getBoolean("collapsed"));
        dto.setRepeatable(rs.getBoolean("repeatable"));
        dto.setAutocompleteType(rs.getString("autocomplete_type"));
        dto.setSortOrder(rs.getInt("sort_order"));

        return dto;
    }

    @Override
    public boolean updateFormTabDatafield(
            Map<String, FormTabDatafieldDTO> formDatafields,
            RecordType recordType,
            int loggedUser) {
        return withTransactionContext(
                con -> {
                    boolean insert = false;
                    boolean delete = false;

                    PreparedStatement deleteSubfieldsPST =
                            con.prepareStatement(
                                    "DELETE FROM "
                                            + recordType
                                            + "_form_subfields WHERE datafield = ?");

                    PreparedStatement insertSubfieldsPST =
                            con.prepareStatement(
                                    "INSERT INTO "
                                            + recordType
                                            + "_form_subfields "
                                            + "(datafield, subfield, collapsed, repeatable, created, created_by,"
                                            + "	modified, modified_by, autocomplete_type, sort_order) "
                                            + "VALUES (?, ?, ?, ?, now(), ?, now(), ?, ?, ?); ");

                    PreparedStatement updateDataFieldsPST =
                            con.prepareStatement(
                                    "UPDATE "
                                            + recordType
                                            + "_form_datafields "
                                            + "SET sort_order = ?, datafield = ?, collapsed = ?, repeatable = ?, "
                                            + "indicator_1 = ?, indicator_2 = ?, material_type = ?, "
                                            + "modified = now(), modified_by = ? "
                                            + "WHERE datafield = ?");
                    PreparedStatement insertDataFieldsPST =
                            con.prepareStatement(
                                    "INSERT INTO "
                                            + recordType
                                            + "_form_datafields"
                                            + "(sort_order, datafield, collapsed, repeatable, indicator_1, "
                                            + "indicator_2, material_type, created_by, modified_by) "
                                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING");

                    for (Entry<String, FormTabDatafieldDTO> entry : formDatafields.entrySet()) {
                        String tag = entry.getKey();

                        FormTabDatafieldDTO datafield = entry.getValue();

                        delete |=
                                datafield.getSubfields() != null
                                        && !datafield.getSubfields().isEmpty();

                        if (delete) {
                            deleteSubfieldsPST.setString(1, tag);

                            deleteSubfieldsPST.addBatch();

                            for (FormTabSubfieldDTO sub : datafield.getSubfields()) {
                                insert = true;

                                insertSubfieldsPST.setString(1, sub.getDatafield());
                                insertSubfieldsPST.setString(2, sub.getSubfield());
                                insertSubfieldsPST.setBoolean(3, sub.isCollapsed());
                                insertSubfieldsPST.setBoolean(4, sub.isRepeatable());
                                insertSubfieldsPST.setInt(5, loggedUser);
                                insertSubfieldsPST.setInt(6, loggedUser);
                                insertSubfieldsPST.setString(
                                        7, sub.getAutocompleteType().toString());
                                insertSubfieldsPST.setInt(8, sub.getSortOrder());

                                insertSubfieldsPST.addBatch();
                            }
                        }

                        updateDataFieldsPST.setInt(1, datafield.getSortOrder());
                        updateDataFieldsPST.setString(2, datafield.getDatafield());
                        updateDataFieldsPST.setBoolean(3, datafield.isCollapsed());
                        updateDataFieldsPST.setBoolean(4, datafield.isRepeatable());
                        updateDataFieldsPST.setString(5, datafield.getIndicator1());
                        updateDataFieldsPST.setString(6, datafield.getIndicator2());
                        updateDataFieldsPST.setString(7, datafield.getMaterialType());
                        updateDataFieldsPST.setInt(8, loggedUser);
                        updateDataFieldsPST.setString(9, datafield.getDatafield());

                        updateDataFieldsPST.addBatch();

                        insertDataFieldsPST.setInt(1, datafield.getSortOrder());
                        insertDataFieldsPST.setString(2, datafield.getDatafield());
                        insertDataFieldsPST.setBoolean(3, datafield.isCollapsed());
                        insertDataFieldsPST.setBoolean(4, datafield.isRepeatable());
                        insertDataFieldsPST.setString(5, datafield.getIndicator1());
                        insertDataFieldsPST.setString(6, datafield.getIndicator2());
                        insertDataFieldsPST.setString(7, datafield.getMaterialType());
                        insertDataFieldsPST.setInt(8, loggedUser);
                        insertDataFieldsPST.setInt(9, loggedUser);

                        insertDataFieldsPST.execute();
                    }

                    if (delete) {
                        deleteSubfieldsPST.executeBatch();
                    }

                    updateDataFieldsPST.executeBatch();

                    if (insert) {
                        insertSubfieldsPST.executeBatch();
                    }

                    return true;
                });
    }
}
