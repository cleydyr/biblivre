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
package biblivre.administration.backup;

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.utils.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private BackupBO backupBO;
    private SchemaBO schemaBO;

    public void prepare(ExtendedRequest request, ExtendedResponse response) {
        String schemas = request.getString("schemas");
        String type = request.getString("type");

        BackupType backupType = BackupType.fromString(type);
        if (backupType == null) {
            this.setMessage(
                    ActionResult.ERROR,
                    "administration.maintenance.backup.error.invalid_backup_type");
            return;
        }

        BackupScope backupScope = backupBO.getBackupScope(SchemaThreadLocal.get());

        String schema = SchemaThreadLocal.get();

        ArrayList<String> list = new ArrayList<>();
        list.add(Constants.GLOBAL_SCHEMA);

        if (Constants.GLOBAL_SCHEMA.equals(schema)) {
            list.addAll(Arrays.asList(StringUtils.split(schemas, ",")));

            if (list.size() == 2) {
                // Only one schema is being backuped. We can say that the backupScope is:
                backupScope = BackupScope.SINGLE_SCHEMA_FROM_MULTI_SCHEMA;
            }
        } else {
            list.add(schema);
        }

        Map<String, Pair<String, String>> map = new HashMap<>();

        for (String s : list) {
            if (schemaBO.isNotLoaded(s)) {
                this.setMessage(
                        ActionResult.ERROR,
                        "administration.maintenance.backup.error.invalid_schema");
                return;
            }

            String title = configurationBO.getString(Constants.CONFIG_TITLE);
            String subtitle = configurationBO.getString(Constants.CONFIG_SUBTITLE);
            map.put(s, Pair.of(title, subtitle));
        }

        BackupDTO dto = backupBO.prepare(map, backupType, backupScope);

        put("id", dto.getId());
    }

    // http://localhost:8080/Biblivre5/?controller=json&module=administration.backup&action=backup
    public void backup(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        BackupDTO dto = backupBO.get(id);

        if (dto == null) {
            this.setMessage(ActionResult.ERROR, "error.invalid_parameters");
            return;
        }

        backupBO.backup(dto);
        request.setScopedSessionAttribute("system_warning_backup", false);
    }

    // http://localhost:8080/Biblivre5/?controller=download&module=administration.backup&action=download&id=1
    public void download(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        final BackupDTO dto = backupBO.get(id);

        if (dto == null) {
            // TODO: Error
            return;
        }

        DiskFile diskFile = new DiskFile(dto.getBackup(), "application/zip");

        this.setFile(diskFile);

        this.setCallback(() -> finishDownload(backupBO, dto));
    }

    public void progress(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        final BackupDTO dto = backupBO.get(id);

        if (dto == null) {
            this.setMessage(ActionResult.ERROR, "error.invalid_parameters");
            return;
        }

        put("current", dto.getCurrentStep());
        put("total", dto.getSteps());
        put("complete", dto.getCurrentStep().equals(dto.getSteps()));
    }

    // http://localhost:8080/Biblivre5/?controller=json&module=administration.backup&action=list
    public void list(ExtendedRequest request, ExtendedResponse response) {
        for (BackupDTO dto : backupBO.list()) {
            append("backups", dto.toJSONObject());
        }
    }

    private void finishDownload(final BackupBO bo, final BackupDTO dto) {
        dto.setDownloaded(true);
        bo.save(dto);
    }

    @Autowired
    public void setBackupBO(BackupBO backupBO) {
        this.backupBO = backupBO;
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }
}
