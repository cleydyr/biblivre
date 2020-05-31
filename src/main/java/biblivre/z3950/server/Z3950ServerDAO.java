package biblivre.z3950.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;

public class Z3950ServerDAO extends AbstractDAO {
	public static Z3950ServerDAO getInstance(String schema) {
		return (Z3950ServerDAO) AbstractDAO.getInstance(Z3950ServerDAO.class, schema);
	}

	public Collection<String> search(String value, Integer indexGroupId, int offset, int limit) {
		String[] terms = TextUtils.prepareWords(TextUtils.preparePhrase(value));
		StringBuilder sql = _buildSql(terms);

		try (Connection con = getConnection();
				PreparedStatement st = con.prepareStatement(sql.toString())) {

			int index = 1;

			st.setInt(index++, indexGroupId.intValue());

			for (int i = 0; i < terms.length; i++) {
				if (StringUtils.isNotBlank(terms[i])) {
					String term = terms[i];

					if (StringUtils.isNotBlank(term)) {
						term = term.toLowerCase();
					}

					st.setString(index++, terms[i]);

					st.setString(index++, TextUtils.incrementLastChar(terms[i]));
				}
			}

			st.setInt(index++, offset);

			st.setInt(index++, limit);

			Collection<String> result = new ArrayList<>();

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					result.add(new String(rs.getBytes("iso2709"), Constants.DEFAULT_CHARSET));
				}
			}

			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	private StringBuilder _buildSql(String[] terms) {
		StringBuilder sql = new StringBuilder(5 + terms.length);

		sql.append("SELECT DISTINCT B.id, B.iso2709 FROM biblio_records B ");
		sql.append("INNER JOIN biblio_idx_fields I ON I.record_id = B.id ");
		sql.append("WHERE B.database = 'main' ");
		sql.append("AND I.indexing_group_id  = ? ");

		for (int i = 0; i < terms.length; i++) {
			if (StringUtils.isNotBlank(terms[i])) {
				sql.append("AND B.id in (SELECT record_id FROM biblio_idx_fields WHERE word >= ? and word < ?) ");
			}
		}

		sql.append("OFFSET ? LIMIT ?; ");

		return sql;
	}
}
