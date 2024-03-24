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

import biblivre.core.utils.Constants;
import biblivre.core.utils.StringPool;
import biblivre.update.UpdateService;
import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Updates {
    private Map<String, UpdateService> updateServicesMap;

    private UpdatesDAO updatesDAO;

    public static String getVersion() {
        return Constants.BIBLIVRE_VERSION;
    }

    public void globalUpdate() {
        SchemaThreadLocal.withGlobalSchema(this::processGlobalUpdate);
    }

    private void processGlobalUpdate() {
        Connection con = null;

        try {
            Set<String> installedVersions = updatesDAO.getInstalledVersions();

            for (Entry<String, UpdateService> entry : updateServicesMap.entrySet()) {
                UpdateService updateService = entry.getValue();

                String serviceName = entry.getKey();

                String version = getVersion(serviceName, updateService);

                if (installedVersions.contains(version)) {
                    log.info("Skipping global update service {}.", version);

                    continue;
                }

                log.info("Processing global update service {}.", version);

                con = updatesDAO.beginUpdate();

                updateService.doUpdate(con);

                updatesDAO.commitUpdate(version, con);

                updateService.afterUpdate();
            }
        } catch (Exception e) {
            updatesDAO.rollbackUpdate(con);
            log.error("Error updating global schema", e);
        }
    }

    private static String getVersion(String serviceName, UpdateService updateService) {
        String prefix = serviceName.replaceFirst("biblivre.update.", StringPool.BLANK);

        return prefix.substring(
                0, prefix.lastIndexOf(updateService.getClass().getSimpleName()) - 1);
    }

    public void schemaUpdate(String schema) {
        SchemaThreadLocal.withSchema(schema, this::processSchemaUpdate);
    }

    private void processSchemaUpdate() {
        String schema = SchemaThreadLocal.get();

        Connection con = null;

        try {
            Set<String> installedVersions = updatesDAO.getInstalledVersions();

            for (Entry<String, UpdateService> entry : updateServicesMap.entrySet()) {
                UpdateService updateService = entry.getValue();

                String serviceName = entry.getKey();

                String version = getVersion(serviceName, updateService);

                if (installedVersions.contains(version)) {
                    log.info("Skipping update service {} for schema {}.", version, schema);

                    continue;
                }

                log.info("Processing update service {} for schema {}.", version, schema);

                con = updatesDAO.beginUpdate();

                updateService.doUpdateScopedBySchema(con);

                updatesDAO.commitUpdate(version, con);

                updateService.afterUpdate();
            }

        } catch (Exception e) {
            updatesDAO.rollbackUpdate(con);
            log.error("Error updating schema {}", schema, e);
        }
    }

    @Autowired
    public void setUpdateServicesMap(Map<String, UpdateService> updateServiceMap) {
        this.updateServicesMap = updateServiceMap;
    }

    @Autowired
    public void setUpdatesDAO(UpdatesDAO updatesDAO) {
        this.updatesDAO = updatesDAO;
    }
}
