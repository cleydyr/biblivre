/**
 *  Este arquivo é parte do Biblivre 5.
 *
 *  Biblivre 5 é um software livre; você pode redistribuí-lo e/ou
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *  Licença, ou (caso queira) qualquer versão posterior.
 *
 *  Este programa é distribuído na esperança de que possa ser  útil,
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 *
 */

package biblivre.z3950.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.jdbc.JDBCSearchable;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.k_int.sql.data_dictionary.OID;

import biblivre.core.configurations.Configurations;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.schemas.Schemas;
import biblivre.core.utils.Constants;

public class BiblivreJDBCSearchable extends JDBCSearchable {
	private static Logger logger = LoggerFactory.getLogger(JDBCSearchable.class);

	private static final String DATASOURCE_NAME = "z3950DataSource";

	private ApplicationContext ctx = null;

	private boolean setup_completed = false;

	private static final Map<String, Integer> accessPointMap = new HashMap<>();

	static {
		accessPointMap.put("bib-1.1.1", 1);
		accessPointMap.put("bib-1.1.1003", 1);
		accessPointMap.put("bib-1.1.4", 3);
		accessPointMap.put("bib-1.1.21", 4);
		accessPointMap.put("bib-1.1.7", 5);
		accessPointMap.put("bib-1.1.8", 6);
		accessPointMap.put("bib-1.1.1016", 0);
		accessPointMap.put("bib-1.1.1036", 0);
	}

	public void close() {
		super.close();
	}

	public IRResultSet evaluate(IRQuery q) {
		return evaluate(q, null);
	}

	public IRResultSet evaluate(IRQuery q, Object user_info) {
		return evaluate(q, user_info, null);
	}

	public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
		checkSetup();

		logger.info("create JDBC Result Set");

		JDBCResultSet result = new JDBCResultSet(this);

		try {
			result.init();

			AttrPlusTermNode aptn = (AttrPlusTermNode) ((InternalModelNamespaceNode) q.query
					.toInternalQueryModel(this.ctx).getChild()).getChild();

			String accessPoint = aptn.getAccessPoint().toString();

			String term = (String) aptn.getTerm();

			Integer indexingGroupId = (Integer) accessPointMap.get(accessPoint);

			String collection = (String) q.getCollections().get(0);

			if ((StringUtils.isBlank(collection)) || collection.equalsIgnoreCase("default")) {
				collection = Z3950ServerBO.getSingleSchema();
			}

			SchemaDTO schemaDto = Schemas.getSchema(collection);

			if ((schemaDto == null) || (schemaDto.isDisabled())) {
				result.setStatus(8);

				return result;
			}

			boolean isZ3950Active = Configurations.getBoolean(collection, Constants.CONFIG_Z3950_SERVER_ACTIVE);

			if (!isZ3950Active) {
				result.setStatus(8);

				return result;
			}

			Z3950ServerDAO dao = Z3950ServerDAO.getInstance(collection);

			Collection<String> results = dao.search(term, indexingGroupId, 0, 200);

			if (results.isEmpty()) {
				result.setStatus(8);

				return result;
			}

			results.stream()
				.map(r -> new OID(DATASOURCE_NAME, "Records", new BiblivreEntityKey(r)))
				.forEach(key -> result.add(key));

			result.setStatus(4);
		}
		catch (Exception e) {
			logger.warn(e.getMessage());

			result.setStatus(8);
		}
		finally {
			logger.info("evaluate complete");
		}

		return result;
	}

	private synchronized void checkSetup() {
		if (!this.setup_completed) {
			this.setup_completed = true;
		}
	}

	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}
}
