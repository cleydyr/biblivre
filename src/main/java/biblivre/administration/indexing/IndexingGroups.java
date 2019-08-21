/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser útil,
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
package biblivre.administration.indexing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.StaticBO;
import biblivre.core.translations.Translations;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Pair;

public class IndexingGroups extends StaticBO {
	private static Logger logger = Logger.getLogger(IndexingGroups.class);

	private static HashMap<Pair<String, RecordType>, List<IndexingGroupDTO>> groups;

	private IndexingGroups() {
	}

	static {
		IndexingGroups.reset();
	}

	public static void reset() {
		IndexingGroups.groups = new HashMap<Pair<String, RecordType>, List<IndexingGroupDTO>>();
	}

	public static void reset(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);

		IndexingGroups.groups.remove(pair);
	}

	public static List<IndexingGroupDTO> getGroups(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);

		List<IndexingGroupDTO> list = IndexingGroups.groups.get(pair);

		if (list == null) {
			list = IndexingGroups.loadGroups(schema, recordType);
		}

		return list;
	}

	public static String getSearchableGroupsText(String schema, RecordType recordType, String language) {
		List<String> list = new LinkedList<String>();

		List<IndexingGroupDTO> groups = IndexingGroups.getGroups(schema, recordType);

		TranslationsMap translations = Translations.get(schema, language);

		for (IndexingGroupDTO group : groups) {
			if (group.getId() == 0) {
				continue;
			}

			list.add(translations.getText("cataloging." + (recordType == RecordType.BIBLIO ? "bibliographic" : recordType) + ".indexing_groups." + group.getTranslationKey()));
		}

		return StringUtils.join(list, ", ");
	}

	public static Integer getDefaultSortableGroupId(String schema, RecordType recordType) {
		List<IndexingGroupDTO> groups = IndexingGroups.getGroups(schema, recordType);

		IndexingGroupDTO sort = null;

		for (IndexingGroupDTO group : groups) {
			if (group.isSortable()) {
				if (group.isDefaultSort()) {
					sort = group;
					break;
				}

				if (sort == null) {
					sort = group;
				}				
			}
		}

		return (sort != null) ? sort.getId() : 1;
	}

	public static void addIndexingGroup(
			Connection con, RecordType recordType, String name, String datafields, boolean sortable)
		throws SQLException {

		_deleteFromIndexingGroupsByTranslationKey(recordType, name, con);

		_insertIntoIndexingGroups(recordType, name, datafields, sortable, con);
	}

	public static void updateIndexingGroup(
			Connection con, RecordType recordType, String name, String datafields)
		throws SQLException {

		StringBuilder sql =
				new StringBuilder(3)
					.append("UPDATE ")
					.append(recordType)
					.append("_indexing_groups SET datafields = ? WHERE translation_key = ?;");

		try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
			PreparedStatementUtil.setAllParameters(pst, datafields, name);

			pst.execute();
		}
	}

	private static void _insertIntoIndexingGroups(
			RecordType recordType, String name, String datafields, boolean sortable,
			Connection con)
		throws SQLException {

		StringBuilder sql =
				new StringBuilder(3)
					.append("INSERT INTO ")
					.append(recordType)
					.append("_indexing_groups (translation_key, datafields, sortable) "
						+ "VALUES (?, ?, ?);");

		try (PreparedStatement insertIntoIndexingGroups = con.prepareStatement(sql.toString())) {

			PreparedStatementUtil.setAllParameters(
					insertIntoIndexingGroups, name, datafields, sortable);

			insertIntoIndexingGroups.execute();
		}
	}

	private static void _deleteFromIndexingGroupsByTranslationKey(
			RecordType recordType, String translationKey, Connection con)
		throws SQLException {

		StringBuilder deleteFromIndexingGroupsByTranslationKeySQLTemplate =
				new StringBuilder(3)
					.append("DELETE FROM ")
					.append(recordType)
					.append("_indexing_groups WHERE translation_key = ?;");

		try (PreparedStatement deleteFromIndexingGroupsByTranslationKey = con.prepareStatement(
					deleteFromIndexingGroupsByTranslationKeySQLTemplate.toString())) {

			PreparedStatementUtil.setAllParameters(
					deleteFromIndexingGroupsByTranslationKey, translationKey);

			deleteFromIndexingGroupsByTranslationKey.execute();
		}
	}

	private static synchronized List<IndexingGroupDTO> loadGroups(
			String schema, RecordType recordType) {

		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);

		List<IndexingGroupDTO> list = IndexingGroups.groups.get(pair);

		// Checking again for thread safety.
		if (list != null) {
			return list;
		}

		if (IndexingGroups.logger.isDebugEnabled()) {
			IndexingGroups.logger.debug("Loading indexing groups from " + schema + "." + recordType);
		}

		IndexingGroupsDAO dao = IndexingGroupsDAO.getInstance(schema);

		list = dao.list(recordType);

		IndexingGroups.groups.put(pair, list);

		return list;
	}
}
