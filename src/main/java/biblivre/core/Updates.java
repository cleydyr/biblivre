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
package biblivre.core;

import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.utils.Constants;
import biblivre.update.UpdateService;
import java.sql.Connection;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Updates {

    private static final Logger logger = LoggerFactory.getLogger(Updates.class);

    public static String getVersion() {
        return Constants.BIBLIVRE_VERSION;
    }

    public static boolean globalUpdate() {
        return SchemaThreadLocal.withSchema(
                Constants.GLOBAL_SCHEMA,
                () -> {
                    UpdatesDAO dao = UpdatesDAO.getInstance();

                    Connection con = null;
                    try {
                        Set<String> installedVersions = dao.getInstalledVersions();

                        ServiceLoader<UpdateService> serviceLoader =
                                ServiceLoader.load(UpdateService.class);

                        for (UpdateService updateService : serviceLoader) {
                            if (!installedVersions.contains(updateService.getVersion())) {
                                logger.info(
                                        "Processing global update service {}.",
                                        updateService.getVersion());

                                con = dao.beginUpdate();

                                updateService.doUpdate(con);

                                dao.commitUpdate(updateService.getVersion(), con);

                                updateService.afterUpdate();
                            } else {
                                logger.info(
                                        "Skipping global update service {}.",
                                        updateService.getVersion());
                            }
                        }

                        return true;
                    } catch (Exception e) {
                        dao.rollbackUpdate(con);
                        e.printStackTrace();
                    }

                    return false;
                });
    }

    public static boolean schemaUpdate(String schema) {
        return SchemaThreadLocal.withSchema(
                schema,
                () -> {
                    UpdatesDAO dao = UpdatesDAO.getInstance();

                    Connection con = null;
                    try {
                        if (!dao.checkTableExistance("versions")) {
                            dao.fixVersionsTable();
                        }

                        Set<String> installedVersions = dao.getInstalledVersions();

                        ServiceLoader<UpdateService> serviceLoader =
                                ServiceLoader.load(UpdateService.class);

                        for (UpdateService updateService : serviceLoader) {
                            if (!installedVersions.contains(updateService.getVersion())) {
                                logger.info(
                                        "Processing update service {} for schema {}.",
                                        updateService.getVersion(),
                                        schema);

                                con = dao.beginUpdate();

                                updateService.doUpdateScopedBySchema(con);

                                dao.commitUpdate(updateService.getVersion(), con);

                                updateService.afterUpdate();
                            } else {
                                logger.info(
                                        "Skipping update service {} for schema {}.",
                                        updateService.getVersion(),
                                        schema);
                            }
                        }

                        return true;
                    } catch (Exception e) {
                        dao.rollbackUpdate(con);
                        e.printStackTrace();
                    }

                    return false;
                });
    }

    public static String getUID() {
        return SchemaThreadLocal.withSchema(
                Constants.GLOBAL_SCHEMA,
                () -> {
                    String uid = Configurations.getString(Constants.CONFIG_UID);

                    if (StringUtils.isBlank(uid)) {
                        uid = UUID.randomUUID().toString();

                        ConfigurationsDTO config = new ConfigurationsDTO();
                        config.setKey(Constants.CONFIG_UID);
                        config.setValue(uid);
                        config.setType("string");
                        config.setRequired(false);

                        Configurations.save(config, 0);
                    }

                    return uid;
                });
    }

    public static void fixPostgreSQL81() {
        SchemaThreadLocal.withSchema(
                "public",
                () -> {
                    UpdatesDAO dao = UpdatesDAO.getInstance();

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
                        e.printStackTrace();
                    }

                    return null;
                });
    }
}
