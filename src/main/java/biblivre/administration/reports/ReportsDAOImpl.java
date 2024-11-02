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
package biblivre.administration.reports;

import biblivre.administration.reports.dto.*;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.circulation.user.UserStatus;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MarcConstants;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

@Service
public class ReportsDAOImpl extends AbstractDAO implements ReportsDAO {

    @Override
    public SummaryReportDto getSummaryReportData(RecordDatabase database) {
        SummaryReportDto dto = new SummaryReportDto();
        try (Connection con = datasource.getConnection()) {
            String sql = "SELECT iso2709, id FROM biblio_records WHERE database = ?;";
            String countSql = "SELECT count(id) FROM biblio_holdings WHERE record_id = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setFetchSize(100);

            final PreparedStatement count = con.prepareStatement(countSql);

            pst.setString(1, database.toString());

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes("iso2709"));
                MarcDataReader dataReader = new MarcDataReader(record);
                String[] data = new String[8];
                String title = dataReader.getTitle(false);
                data[0] = StringUtils.isNotBlank(title) ? title : "";
                String author = dataReader.getAuthor(true);
                data[1] = StringUtils.isNotBlank(author) ? author : "";
                String isbn = dataReader.getIsbn();
                data[2] = StringUtils.isNotBlank(isbn) ? isbn : "";
                String editor = dataReader.getEditor();
                data[3] = StringUtils.isNotBlank(editor) ? editor : ""; // editora(50)
                String year = dataReader.getPublicationYear();
                data[4] = StringUtils.isNotBlank(year) ? year : ""; // ano(20)
                String edition = dataReader.getEdition();
                data[5] = StringUtils.isNotBlank(edition) ? edition : "";
                String dewey = dataReader.getDDCN();
                data[6] = StringUtils.isNotBlank(dewey) ? dewey : "";

                count.setInt(1, rs.getInt("id"));
                ResultSet countRs = count.executeQuery();
                countRs.next();
                data[7] = countRs.getString(1);
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public DeweyReportDto getDeweyReportData(RecordDatabase db, String datafield, int digits) {
        DeweyReportDto dto = new DeweyReportDto();
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT b.iso2709, count(h.id) as holdings FROM biblio_records b "
                            + "LEFT OUTER JOIN biblio_holdings h "
                            + "ON b.id = h.record_id "
                            + "WHERE b.database = ? "
                            + "GROUP BY b.iso2709; ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, db.toString());

            final ResultSet rs = pst.executeQuery();
            Map<String, Integer[]> acc = new HashMap<>();

            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getString("iso2709"));

                String dewey = "";

                MarcDataReader dataReader = new MarcDataReader(record);
                if (datafield.equals(MarcConstants.DDCN)) {
                    dewey = dataReader.getDDCN();
                } else if (datafield.equals(MarcConstants.SHELF_LOCATION)) {
                    dewey = dataReader.getLocation();
                }

                String formattedDewey = ReportUtils.formatDeweyString(dewey, digits);

                Integer numberOfHoldings = rs.getInt("holdings");
                Integer[] totals = acc.get(formattedDewey);

                if (totals == null) {
                    acc.put(formattedDewey, new Integer[] {1, numberOfHoldings});
                } else {
                    Integer[] newTotals =
                            new Integer[] {totals[0] + 1, totals[1] + numberOfHoldings};
                    acc.put(formattedDewey, newTotals);
                }
            }

            List<String[]> data = new ArrayList<>();
            dto.setData(data);
            for (Entry<String, Integer[]> entry : acc.entrySet()) {
                String[] arrayData = new String[3];
                arrayData[0] = entry.getKey();
                Integer[] totals = entry.getValue();
                arrayData[1] = String.valueOf(totals[0]);
                arrayData[2] = String.valueOf(totals[1]);
                dto.getData().add(arrayData);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public AssetHoldingDto getAssetHoldingReportData() {
        AssetHoldingDto dto = new AssetHoldingDto();
        try (Connection con = datasource.getConnection()) {
            String sql =
                    " SELECT H.accession_number, R.iso2709 FROM biblio_holdings H INNER JOIN biblio_records R "
                            + " ON R.id = H.record_id WHERE H.database = 'main' "
                            + " ORDER BY H.accession_number ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setFetchSize(100);

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes("iso2709"));
                MarcDataReader dataReader = new MarcDataReader(record);
                String assetHolding = rs.getString("accession_number");
                String[] data = new String[5];
                data[0] = assetHolding;
                data[1] = dataReader.getAuthorName(false);
                data[2] = dataReader.getTitle(false);
                data[3] = dataReader.getEdition();
                data[4] = dataReader.getPublicationYear();
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public AssetHoldingDto getAssetHoldingFullReportData() {
        AssetHoldingDto dto = new AssetHoldingDto();
        try (Connection con = datasource.getConnection()) {
            String sql =
                    " SELECT H.id, H.accession_number, R.iso2709 FROM biblio_holdings H INNER JOIN biblio_records R "
                            + " ON R.id = H.record_id WHERE H.database = 'main' "
                            + " ORDER BY H.accession_number ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setFetchSize(100);

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes("iso2709"));
                MarcDataReader dataReader = new MarcDataReader(record);
                String assetHolding = rs.getString("accession_number");
                String serial = rs.getString("id");
                String[] data = new String[7];
                data[0] = serial;
                data[1] = assetHolding;
                data[2] = dataReader.getTitle(false);
                data[3] = dataReader.getAuthorName(false);
                String location = dataReader.getShelfLocation();
                data[4] = StringUtils.isNotBlank(location) ? location : "";
                data[5] = dataReader.getEdition();
                data[6] = dataReader.getPublicationYear();
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public AssetHoldingByDateDto getAssetHoldingByDateReportData(
            String initialDate, String finalDate) {
        AssetHoldingByDateDto dto = new AssetHoldingByDateDto();
        try (Connection con = datasource.getConnection()) {
            String sql =
                    " SELECT H.accession_number, to_char(H.created, 'DD/MM/YYYY'), R.iso2709, H.iso2709 "
                            + " FROM biblio_holdings H INNER JOIN biblio_records R "
                            + " ON R.id = H.record_id WHERE H.database = 'main' "
                            + " AND H.created >= to_date(?, 'DD-MM-YYYY') "
                            + " AND H.created <= to_date(?, 'DD-MM-YYYY') "
                            + " ORDER BY H.created, H.accession_number ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, initialDate);
            pst.setString(2, finalDate);
            pst.setFetchSize(100);

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes(3));

                String assetHolding = rs.getString("accession_number");
                String creationDate = rs.getString(2);
                String[] data = new String[6];
                MarcDataReader dataReader = new MarcDataReader(record);
                data[0] = creationDate;
                data[1] = assetHolding;
                data[2] = dataReader.getTitle(false);
                data[3] = dataReader.getAuthorName(false);
                data[4] = dataReader.getPublicationYear();

                Record holding = MarcUtils.iso2709ToRecord(rs.getBytes(4));
                MarcDataReader holdingReader = new MarcDataReader(holding);
                data[5] = holdingReader.getSourceAcquisitionDate();
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public HoldingCreationByDateReportDto getHoldingCreationByDateReportData(
            String initialDate, String finalDate) {
        HoldingCreationByDateReportDto dto = new HoldingCreationByDateReportDto();
        dto.setInitialDate(initialDate);
        dto.setFinalDate(finalDate);
        String totalBiblioMain = "0";
        String totalBiblioWork = "0";
        String totalHoldingMain = "0";
        String totalHoldingWork = "0";
        try (Connection con = datasource.getConnection()) {
            String sqlTotal =
                    """
                            SELECT to_char(created, 'DD/MM/YYYY'), user_name, count(created_by)
                            FROM holding_creation_counter
                            WHERE created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                            GROUP BY user_name, to_char(created, 'DD/MM/YYYY')
                            ORDER BY to_char(created, 'DD/MM/YYYY'), user_name
                            """;
            PreparedStatement st = con.prepareStatement(sqlTotal);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            ResultSet rs = st.executeQuery();
            List<String[]> data = new ArrayList<>();
            while (rs.next()) {
                String[] arrayData = new String[4];
                arrayData[0] = rs.getString(1); // data
                arrayData[1] = rs.getString(2); // nome
                arrayData[2] = rs.getString(3); // total
                data.add(arrayData);
            }
            dto.setData(data);

            String sqlBiblioMain =
                    """
                            SELECT COUNT(id) FROM biblio_records
                            WHERE database = 'main' AND created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                            """;

            st = con.prepareStatement(sqlBiblioMain);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalBiblioMain = rs.getString(1);
            }

            String sqlBiblioWork =
                    """
                            SELECT COUNT(id) FROM biblio_records
                            WHERE database = 'work' AND created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                            """;

            st = con.prepareStatement(sqlBiblioWork);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalBiblioWork = rs.getString(1);
            }

            String sqlHoldingMain =
                    """
                            SELECT COUNT(id) FROM biblio_holdings
                            WHERE database = 'main' AND created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                            """;
            st = con.prepareStatement(sqlHoldingMain);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalHoldingMain = rs.getString(1);
            }

            String sqlHoldingWork =
                    """
                            SELECT COUNT(id) FROM biblio_holdings
                            WHERE database = 'work' AND created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                            """;

            st = con.prepareStatement(sqlHoldingWork);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalHoldingWork = rs.getString(1);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        dto.setTotalBiblioMain(totalBiblioMain);
        dto.setTotalBiblioWork(totalBiblioWork);
        dto.setTotalHoldingMain(totalHoldingMain);
        dto.setTotalHoldingWork(totalHoldingWork);
        return dto;
    }

    @Override
    public LendingsByDateReportDto getLendingsByDateReportData(
            String initialDate, String finalDate) {
        PreparedStatement st;
        LendingsByDateReportDto dto = new LendingsByDateReportDto();
        dto.setInitialDate(initialDate);
        dto.setFinalDate(finalDate);
        int lended = 0, late = 0, total = 0;
        try (Connection con = datasource.getConnection()) {
            String sqlLent =
                    """
                            SELECT count(*)
                            FROM lendings
                            WHERE created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                                AND return_date IS NULL
                            """;
            st = con.prepareStatement(sqlLent);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                lended = rs.getInt(1);
            }
            rs.close();

            String sqlHistory =
                    """
                            SELECT count(*)
                            FROM lendings
                            WHERE created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                                AND return_date IS NOT NULL
                            """;

            st = con.prepareStatement(sqlHistory);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1) + lended;
            }
            rs.close();

            String sqlLate =
                    """
                            SELECT count(*)
                            FROM lendings
                            WHERE created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                                AND expected_return_date < to_date(?, 'DD-MM-YYYY')
                                AND return_date IS NULL
                            """;

            st = con.prepareStatement(sqlLate);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            st.setString(3, dd_MM_yyyy.format(new Date()));
            rs = st.executeQuery();
            if (rs.next()) {
                late = rs.getInt(1);
            }
            rs.close();

            String[] totals = {String.valueOf(total), String.valueOf(lended), String.valueOf(late)};
            dto.setTotals(totals);

            String sqlTop20 =
                    """
                            SELECT b.iso2709, count(b.id) AS rec_count
                            FROM lendings l, biblio_records b, biblio_holdings h
                            WHERE l.holding_id = h.id
                                AND l.created >= to_date(?, 'DD-MM-YYYY')
                                AND l.created <= to_date(?, 'DD-MM-YYYY')
                                AND h.record_id = b.id
                            GROUP BY b.id
                            ORDER BY rec_count DESC
                            LIMIT 20;
                            """;

            st = con.prepareStatement(sqlTop20);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            List<String[]> data = new ArrayList<>();

            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes(1));
                Integer count = rs.getInt(2);

                MarcDataReader dataReader = new MarcDataReader(record);
                String[] arrayData = new String[3];
                arrayData[0] = String.valueOf(count); // count
                arrayData[1] = dataReader.getTitle(false); // title
                arrayData[2] = dataReader.getAuthorName(false); // author
                data.add(arrayData);
            }
            dto.setData(data);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public LateLendingsDto getLateReturnLendingsReportData() {
        LateLendingsDto dto = new LateLendingsDto();
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT u.id as userid, u.name as username, l.expected_return_date, b.iso2709 "
                            + "FROM lendings l, users u, biblio_records b, biblio_holdings h "
                            + "WHERE l.expected_return_date < to_date(?, 'DD-MM-YYYY') "
                            + "AND l.user_id = u.id "
                            + "AND l.holding_id = h.id "
                            + "AND h.record_id = b.id "
                            + "AND l.return_date is null; ";
            final PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, dd_MM_yyyy.format(new Date()));

            final ResultSet rs = st.executeQuery();
            List<String[]> data = new ArrayList<>();

            while (rs.next()) {
                String[] lending = new String[4];
                lending[0] = String.valueOf(rs.getInt("userid")); // matricula
                lending[1] = rs.getString("username"); // nome do usuario
                Record record =
                        MarcUtils.iso2709ToRecord(
                                new String(rs.getBytes("iso2709"), Constants.DEFAULT_CHARSET));
                MarcDataReader dataReader = new MarcDataReader(record);
                lending[2] = dataReader.getTitle(false); // titulo
                lending[3] = dd_MM_yyyy.format(rs.getDate("expected_return_date"));
                data.add(lending);
            }
            dto.setData(data);
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return dto;
    }

    @Override
    public SearchesByDateReportDto getSearchesByDateReportData(
            String initialDate, String finalDate) {
        SearchesByDateReportDto dto = new SearchesByDateReportDto();
        try (Connection con = datasource.getConnection()) {
            final String sql =
                    """
                            SELECT count(created), to_char(created, 'YYYY-MM-DD')
                            FROM biblio_searches
                            WHERE created >= to_date(?, 'DD-MM-YYYY')
                                AND created <= to_date(?, 'DD-MM-YYYY')
                            GROUP BY to_char(created, 'YYYY-MM-DD')
                            ORDER BY to_char(created, 'YYYY-MM-DD') ASC
                            """;

            final PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            final ResultSet rs = st.executeQuery();
            dto.setInitialDate(initialDate);
            dto.setFinalDate(finalDate);
            List<String[]> data = new ArrayList<>();
            dto.setData(data);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                String[] arrayData = new String[2];
                arrayData[0] = rs.getString(1);
                Date date = format.parse(rs.getString(2));
                arrayData[1] = dd_MM_yyyy.format(date);
                dto.getData().add(arrayData);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public AllUsersReportDto getAllUsersReportData() {
        AllUsersReportDto dto = new AllUsersReportDto();
        dto.setTypesMap(new HashMap<>());
        dto.setData(new HashMap<>());

        try (Connection con = datasource.getConnection()) {

            String firstSql =
                            """
                    SELECT count(u.type) as total, t.description, t.id
                    FROM users u, users_types t
                    WHERE u.type = t.id
                        AND u.status <> '%s'
                    GROUP BY u.type, t.description, t.id
                    ORDER BY t.description;
                    """
                            .formatted(UserStatus.INACTIVE);

            ResultSet rs = con.createStatement().executeQuery(firstSql);

            while (rs.next()) {
                final String description = rs.getString("description");
                final Integer count = rs.getInt("total");
                dto.getTypesMap().put(description, count);

                String secondSql =
                                """
                        SELECT name, id, created, modified
                        FROM users
                        WHERE type = '%s'
                        ORDER BY name
                        """
                                .formatted(rs.getInt("id"));

                ResultSet rs2 = con.createStatement().executeQuery(secondSql);
                List<String> dataList = new ArrayList<>();
                while (rs2.next()) {
                    var str =
                                    """
                            %s\t%d\t%s\t%s
                            """
                                    .formatted(
                                            rs2.getString("name"),
                                            rs2.getInt("id"),
                                            dd_MM_yyyy.format(rs2.getDate("created")),
                                            dd_MM_yyyy.format(rs2.getDate("modified")));
                    dataList.add(str);
                }
                dto.getData().put(description, dataList);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public RequestsByDateReportDto getRequestsByDateReportData(
            String initialDate, String finalDate) {
        RequestsByDateReportDto dto = new RequestsByDateReportDto();
        dto.setInitialDate(initialDate);
        dto.setFinalDate(finalDate);
        try (Connection con = datasource.getConnection()) {
            String sql =
                    """
                    SELECT DISTINCT o.id, r.requester, r.item_title, r.quantity, i.unit_value, o.total_value
                    FROM orders o, requests r, request_quotation i
                    WHERE o.quotation_id = i.quotation_id
                        AND r.id = i.request_id
                        AND r.created >= to_date(?, 'DD-MM-YYYY')
                        AND r.created <= to_date(?, 'DD-MM-YYYY')
                    ORDER BY o.id
                    """;

            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            final ResultSet rs = st.executeQuery();
            List<String[]> dataList = new ArrayList<>();
            dto.setData(dataList);
            while (rs.next()) {
                String[] data = new String[6];
                data[0] = rs.getString("id");
                data[1] = rs.getString("requester");
                data[2] = rs.getString("item_title");
                data[3] = rs.getString("quantity");
                data[4] = rs.getString("unit_value");
                data[5] = rs.getString("total_value");
                dataList.add(data);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public TreeMap<String, Set<Integer>> searchAuthors(String authorName, RecordDatabase database) {
        TreeMap<String, Set<Integer>> results = new TreeMap<>();

        String[] terms = authorName.split(" ");

        try (Connection con = datasource.getConnection()) {
            String sql =
                            """
                    SELECT DISTINCT B.id, B.iso2709 FROM biblio_records B
                    INNER JOIN biblio_idx_fields I ON I.record_id = B.id
                    WHERE B.database = ?
                        AND I.indexing_group_id = 1
                        %s
                    """
                            .formatted(
                                    "AND B.id in (SELECT record_id FROM biblio_idx_fields WHERE word >= ? and word < ?) "
                                            .repeat(
                                                    (int)
                                                            Arrays.stream(terms)
                                                                    .filter(StringUtils::isNotBlank)
                                                                    .count()));

            PreparedStatement st = con.prepareStatement(sql);
            int index = 1;
            st.setString(index++, database.toString());

            for (String term : terms) {
                if (StringUtils.isBlank(term)) {
                    continue;
                }

                st.setString(index++, term);
                st.setString(index++, TextUtils.incrementLastChar(term));
            }

            ResultSet rs = st.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    String iso2709 = new String(rs.getBytes("iso2709"), Constants.DEFAULT_CHARSET);
                    Record record = MarcUtils.iso2709ToRecord(iso2709);
                    String name = new MarcDataReader(record).getAuthor(false);
                    if (results.containsKey(name)) {
                        Set<Integer> ids = results.get(name);
                        ids.add(id);
                    } else {
                        Set<Integer> ids = new HashSet<>();
                        ids.add(id);
                        results.put(name, ids);
                    }
                }
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return results;
    }

    @Override
    public BibliographyReportDto getBibliographyReportData(
            String authorName, Integer[] recordIdArray) {
        BibliographyReportDto dto = new BibliographyReportDto();
        dto.setAuthorName(authorName);

        try (Connection con = datasource.getConnection()) {
            String sql =
                            """
                    SELECT iso2709 FROM biblio_records
                    WHERE id IN (%s)
                    ORDER BY id ASC;
                    """
                            .formatted(StringUtils.repeat("?", ",", recordIdArray.length));

            PreparedStatement st = con.prepareStatement(sql);
            for (int i = 0; i < recordIdArray.length; i++) {
                st.setInt(i + 1, recordIdArray[i]);
            }

            ResultSet rs = st.executeQuery();
            List<String[]> data = new ArrayList<>();
            while (rs.next()) {
                String iso2709 = new String(rs.getBytes("iso2709"), Constants.DEFAULT_CHARSET);
                Record record = MarcUtils.iso2709ToRecord(iso2709);
                MarcDataReader dataReader = new MarcDataReader(record);
                String[] lending = new String[5];
                lending[0] = dataReader.getTitle(false);
                lending[1] = dataReader.getEdition();
                lending[2] = dataReader.getEditor();
                lending[3] = dataReader.getPublicationYear();
                lending[4] = dataReader.getShelfLocation();
                data.add(lending);
            }
            dto.setData(data);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public ReservationReportDto getReservationReportData() {
        ReservationReportDto dto = new ReservationReportDto();
        try (Connection con = datasource.getConnection()) {
            Statement st = con.createStatement();

            String sql =
                    """
                    SELECT u.name, u.id, b.iso2709, to_char(r.created, 'DD/MM/YYYY') AS created
                    FROM reservations r, users u, biblio_records b
                    WHERE r.user_id = u.id
                        AND r.record_id = b.id
                        AND r.record_id is not null
                    ORDER BY u.name ASC;
                    """;

            ResultSet rs = st.executeQuery(sql);

            List<String[]> biblioReservations = new ArrayList<>();
            while (rs.next()) {
                String[] reservation = new String[5];
                reservation[0] = rs.getString("name");
                reservation[1] = String.valueOf(rs.getInt("id"));
                String iso2709 = new String(rs.getBytes("iso2709"), Constants.DEFAULT_CHARSET);
                Record record = MarcUtils.iso2709ToRecord(iso2709);
                MarcDataReader dataReader = new MarcDataReader(record);
                reservation[2] = dataReader.getTitle(false);
                reservation[3] = dataReader.getAuthorName(false);
                reservation[4] = rs.getString("created");
                biblioReservations.add(reservation);
            }
            dto.setBiblioReservations(biblioReservations);

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }
}
