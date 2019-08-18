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
package biblivre.core;

import java.sql.Connection;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import biblivre.update.UpdateService;

public class Updates {

	public static String getVersion() {
		return Constants.BIBLIVRE_VERSION;
	}

	public static boolean globalUpdate() {
		UpdatesDAO dao = UpdatesDAO.getInstance(Constants.GLOBAL_SCHEMA);

		Connection con = null;
		try {
			Set<String> installedVersions = dao.getInstalledVersions();

			ServiceLoader<UpdateService> serviceLoader = ServiceLoader.load(UpdateService.class);

			for (UpdateService updateService : serviceLoader) {
				if (!installedVersions.contains(updateService.getVersion())) {
					con = dao.beginUpdate();

					updateService.doUpdate(con);

					dao.commitUpdate(updateService.getVersion(), con);

					updateService.afterUpdate();
				}
			}

			return true;
		} catch (Exception e) {
			dao.rollbackUpdate(con);
			e.printStackTrace();
		}

		return false;
	}

	public static boolean schemaUpdate(String schema) {
		UpdatesDAO dao = UpdatesDAO.getInstance(schema);

		Connection con = null;
		try {
			if (!dao.checkTableExistance("versions")) {
				dao.fixVersionsTable();
			}

			Set<String> installedVersions = dao.getInstalledVersions();

			ServiceLoader<UpdateService> serviceLoader = ServiceLoader.load(UpdateService.class);

			for (UpdateService updateService : serviceLoader) {
				if (!installedVersions.contains(updateService.getVersion())) {
					con = dao.beginUpdate();

					updateService.doUpdateScopedBySchema(con);

					dao.commitUpdate(updateService.getVersion(), con);

					updateService.afterUpdate();
				}
			}

			String version = null;

			version = "4.1.0";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.1";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.2";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.3";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.4";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.5";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.6";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.7";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.fixHoldingCreationTable(con);
				dao.fixCDDBiblioBriefFormat(con);

				dao.commitUpdate(version, con);
			}

			version = "4.1.8";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.9";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.10";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.addIndexingGroup(con, RecordType.BIBLIO, "publisher", "260_b", true);
				dao.addIndexingGroup(con, RecordType.BIBLIO, "series", "490_a", true);

				dao.addBriefFormat(con, RecordType.BIBLIO, "501", "${a}", 28);
				dao.addBriefFormat(con, RecordType.BIBLIO, "530", "${a}", 31);
				dao.addBriefFormat(con, RecordType.BIBLIO, "595", "${a}", 33);

				dao.updateBriefFormat(con, RecordType.BIBLIO, "245", "${a}_{: }${b}_{ / }${c}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "100", "${a}_{ - }${d}_{ }(${q})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "110", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "111", "${a}_{. }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "130", "${a}_{. }${l}_{. }${f}");

				dao.updateBriefFormat(con, RecordType.BIBLIO, "600", "${a}_{. }${b}_{. }${c}_{. }${d}_{ - }${x}_{ - }${y}_{ - }${z}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "610", "${a}_{. }${b}_{ - }${x}_{ - }${y}_{ - }${z}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "611", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})_{ - }${x}_{ - }${y}_{ - }${z}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "630", "${a}_{. }(${d})_{ - }${x}_{ - }${y}_{ - }${z}");

				dao.updateBriefFormat(con, RecordType.BIBLIO, "700", "${a}_{. }${d}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "710", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "711", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "630", "${a}_{. }(${d})");

				dao.updateIndexingGroup(con, RecordType.BIBLIO, "title", "245_a_b,243_a_f,240_a,730_a,740_a_n_p,830_a_v,250_a,130_a");

				Fields.reset(schema, RecordType.BIBLIO);
				IndexingGroups.reset(schema, RecordType.BIBLIO);

				dao.commitUpdate(version, con);
			}

			version = "4.1.10a";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.updateBriefFormat(con, RecordType.BIBLIO, "490", "(${a}_{ ; }${v})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "830", "${a}_{. }${p}_{ ; }${v}");

				dao.invalidateIndex(con, RecordType.BIBLIO);


				Fields.reset(schema, RecordType.BIBLIO);

				dao.commitUpdate(version, con);
			}

			version = "4.1.11";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.11a";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.fixAuthoritiesBriefFormat(con);

				dao.commitUpdate(version, con);
			}

			version = "5.0.0";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				for (RecordType recordType : RecordType.values()) {
					dao.addDatafieldSortOrderColumns(con, recordType);
					dao.addSubfieldSortOrderColumns(con, recordType);
				}

				dao.commitUpdate(version, con);
			}

			 version = "5.0.1";
			 if (!installedVersions.contains(version)) {
				 con = dao.beginUpdate();
				 dao.replaceBiblivreVersion(con);

				 dao.commitUpdate(version, con);

				 Translations.reset();
				 Configurations.reset();
			 }

			version = "5.0.1b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.addBriefFormatSortOrderColumns(con, RecordType.BIBLIO);
				dao.addBriefFormatSortOrderColumns(con, RecordType.AUTHORITIES);
				dao.addBriefFormatSortOrderColumns(con, RecordType.VOCABULARY);

				dao.commitUpdate(version, con);
			}

			version = "5.0.2";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "5.0.3";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "5.0.4c";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.updateZ3950Address(con, "Library of Congress Online Catalog - EUA", "lx2.loc.gov");

				dao.commitUpdate(version, con);
			}

			// version = "4.0.X";
			// if (!installedVersions.contains(version)) {
			// con = dao.beginUpdate();
			// dao.commitUpdate(version, con);
			// }


			return true;
		} catch (Exception e) {
			dao.rollbackUpdate(con);
			e.printStackTrace();
		}

		return false;
	}

	public static String getUID() {
		String uid = Configurations.getString(Constants.GLOBAL_SCHEMA, Constants.CONFIG_UID);

		if (StringUtils.isBlank(uid)) {
			uid = UUID.randomUUID().toString();

			ConfigurationsDTO config = new ConfigurationsDTO();
			config.setKey(Constants.CONFIG_UID);
			config.setValue(uid);
			config.setType("string");
			config.setRequired(false);

			Configurations.save(Constants.GLOBAL_SCHEMA, config, 0);
		}

		return uid;
	}

	public static String checkUpdates() {
		String uid = Updates.getUID();
		String version = Updates.getVersion();

		PostMethod updatePost = new PostMethod(Constants.UPDATE_URL);
		updatePost.addParameter("uid", TextUtils.biblivreEncrypt(uid));
		updatePost.addParameter("version", TextUtils.biblivreEncrypt(version));

		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		try {
			int status = client.executeMethod(updatePost);

			if (status == HttpStatus.SC_OK) {
				return updatePost.getResponseBodyAsString();
			}
			updatePost.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void fixPostgreSQL81() {
		UpdatesDAO dao = UpdatesDAO.getInstance("public");

		try {
			if (!dao.checkFunctionExistance("array_agg")) {
				String version = dao.getPostgreSQLVersion();

				if (version.contains("8.1")) {
					dao.create81ArrayAgg();
				} else {
					dao.createArrayAgg();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
