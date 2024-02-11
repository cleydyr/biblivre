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

import static biblivre.core.utils.Constants.DEFAULT_DATE_FORMAT;
import static biblivre.core.utils.Constants.DEFAULT_DATE_FORMAT_TIMEZONE;
import static biblivre.core.utils.Constants.DEFAULT_DATE_PRINTER_TIMEZONE;

import biblivre.core.AbstractDTO;
import biblivre.core.utils.SchemaUtils;
import java.io.File;
import java.io.Serial;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

public final class RestoreOperation extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    private Map<String, Pair<String, String>> schemas;
    private BackupType type;
    private BackupScope backupScope;
    private File backup;
    private boolean valid;
    private boolean purgeAll;

    private transient BackupScope restoreScope;
    private transient Map<String, String> restoreSchemas;

    public RestoreOperation() {}

    public RestoreOperation(BackupDTO dto) {
        this.setSchemas(dto.getSchemas());
        this.setType(dto.getType());
        this.setBackupScope(dto.getBackupScope());
        this.setCreated(dto.getCreated());
        this.setBackup(dto.getBackup());
    }

    public RestoreOperation(JSONObject backupMetadataJSON) {
        this.setSchemas(backupMetadataJSON.get("schemas").toString());
        this.setType(BackupType.fromString(backupMetadataJSON.getString("type")));
        this.setBackupScope(BackupScope.fromString(backupMetadataJSON.getString("backup_scope")));
        this.setValid(true);

        if (!backupMetadataJSON.has("created")) {
            return;
        }

        String created = backupMetadataJSON.getString("created");

        Date parsedDate = new Date(0);

        try {
            if (created != null) {
                parsedDate = DEFAULT_DATE_FORMAT_TIMEZONE.parse(created);
            }
        } catch (ParseException e) {
            try {
                this.setCreated(DEFAULT_DATE_FORMAT.parse(created));
            } catch (ParseException e2) {
                this.setValid(false);
            }
        }
        this.setCreated(parsedDate);
    }

    public Map<String, Pair<String, String>> getSchemas() {
        return this.schemas;
    }

    public void setSchemas(Map<String, Pair<String, String>> schemas) {
        this.schemas = schemas;
    }

    public void setSchemas(String schemas) {
        this.schemas = SchemaUtils.buildSchemasMap(schemas);
    }

    public BackupType getType() {
        return this.type;
    }

    public void setType(BackupType type) {
        this.type = type;
    }

    public BackupScope getBackupScope() {
        return this.backupScope;
    }

    public void setBackupScope(BackupScope backupScope) {
        this.backupScope = backupScope;
    }

    public BackupScope getRestoreScope() {
        return this.restoreScope;
    }

    public void setRestoreScope(BackupScope restoreScope) {
        this.restoreScope = restoreScope;
    }

    public File getBackup() {
        return this.backup;
    }

    public void setBackup(File backup) {
        this.backup = backup;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isPurgeAll() {
        return this.purgeAll;
    }

    public void setPurgeAll(boolean purgeAll) {
        this.purgeAll = purgeAll;
    }

    public Map<String, String> getRestoreSchemas() {
        return this.restoreSchemas;
    }

    public void setRestoreSchemas(Map<String, String> restoreSchemas) {
        this.restoreSchemas = restoreSchemas;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.putOpt("schemas", this.getSchemas());
        json.putOpt("type", this.getType());
        json.putOpt("backup_scope", this.getBackupScope());

        if (this.getCreated() != null) {
            String formattedDate = DEFAULT_DATE_PRINTER_TIMEZONE.format(this.getCreated());

            json.putOpt("created", formattedDate);
        }

        if (this.getBackup() != null) {
            json.putOpt("file", this.getBackup().getName());
        }

        json.putOpt("valid", this.isValid());

        return json;
    }
}
