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
package biblivre.administration.setup;

import biblivre.administration.backup.BackupBO;
import biblivre.administration.backup.BackupScope;
import biblivre.administration.backup.BackupType;
import biblivre.administration.backup.RestoreBO;
import biblivre.administration.backup.RestoreDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.MemoryFile;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.Constants;
import biblivre.core.utils.StringPool;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private RestoreBO restoreBO;
    private BackupBO backupBO;
    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;

    public void cleanInstall(ExtendedRequest request, ExtendedResponse response) {

        boolean isNewLibrary = configurationBO.getBoolean(Constants.CONFIG_NEW_LIBRARY);
        boolean success = true;

        if (!isNewLibrary) {
            SchemaDTO dto = new SchemaDTO();
            dto.setName(Constants.BIBLIVRE);
            dto.setSchema(SchemaThreadLocal.get());
            dto.setCreatedBy(request.getLoggedUserId());

            State.start();
            State.writeLog(request.getLocalizedText("multi_schema.manage.log_header"));

            success = schemaBO.createSchema(dto, false, StringPool.BLANK);

            if (success) {
                State.finish();
            } else {
                State.cancel();
            }
        }

        if (success) {
            ConfigurationsDTO dto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
            configurationBO.save(dto, 0);
        }

        put("success", success);
    }

    // http://localhost:8080/Biblivre5/?controller=json&module=administration.backup&action=list_restores
    public void listRestores(ExtendedRequest request, ExtendedResponse response) {
        List<RestoreDTO> list = restoreBO.list();

        put("success", true);

        for (RestoreDTO dto : list) {
            append("restores", dto.toJSONObject());
        }
    }

    public void uploadBiblivre4(ExtendedRequest request, ExtendedResponse response) {
        boolean mediaUpload = request.getBoolean("media_upload", false);

        MemoryFile file = request.getFile(mediaUpload ? "biblivre4backupmedia" : "biblivre4backup");

        String extension = file.getName().endsWith("b4bz") ? "b4bz" : "b5bz";

        File path = backupBO.getBackupDestination();
        String uuid = UUID.randomUUID() + "." + extension;
        File backup = new File(path, uuid);

        boolean success = true;
        RestoreDTO dto = null;

        try (OutputStream os = Files.newOutputStream(backup.toPath())) {
            file.copy(os);

            dto = restoreBO.getRestoreDTO(backup.getName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            success = false;
        }
        put("success", success);
        put("file", uuid);

        if (success && dto != null) {
            put("metadata", dto.toJSONObject());
        }
    }

    private Map<String, String> breakString(String string) {
        Map<String, String> ret = new HashMap<>();

        if (StringUtils.isBlank(string)) {
            return ret;
        }

        for (String s : string.split(",")) {
            if (StringUtils.isBlank(s)) {
                continue;
            }

            String[] split = s.split(":");

            if (split.length != 2) {
                continue;
            }

            ret.put(split[0], split[1]);
        }

        return ret;
    }

    private boolean checkForPartialBackup(
            RestoreDTO dto, ExtendedRequest request, ExtendedResponse response) {

        String mediaFileBackup = request.getString("mediaFileBackup");
        boolean skip = request.getBoolean("skip", false);

        try {
            switch (dto.getType()) {
                case FULL -> {
                    // Full backup User may restore it
                    return true;
                }
                case DIGITAL_MEDIA_ONLY -> {
                    State.writeLog(
                            request.getLocalizedText(
                                    "administration.setup.biblivre4restore.error.digital_media_only_selected"));
                    put("success", false);
                    return false;
                }
                case EXCLUDE_DIGITAL_MEDIA -> {
                    if (skip) {
                        return true;
                    }

                    if (StringUtils.isNotBlank(mediaFileBackup)) {
                        RestoreDTO partialDto = restoreBO.getRestoreDTO(mediaFileBackup);

                        if (partialDto.getType() == BackupType.DIGITAL_MEDIA_ONLY) {
                            return true;
                        }

                        State.writeLog(
                                request.getLocalizedText(
                                        "administration.setup.biblivre4restore.error.digital_media_only_should_be_selected"));
                        put("success", false);
                        return false;
                    }

                    put("success", true);
                    put("ask_for_media_backup", true);
                    return false;
                }
            }
        } catch (Exception e) {
            State.writeLog(ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    // http://localhost:8080/Biblivre5/?controller=json&module=administration.backup&action=restore&filename=Biblivre Backup 2012-09-15 22h56m22s Full.b5bz
    public void restore(ExtendedRequest request, ExtendedResponse response) {
        String schema = SchemaThreadLocal.get();

        String filename = request.getString("filename");
        String mediaFileBackup = request.getString("mediaFileBackup");
        String selectedBackupSchema = request.getString("selected_schema");
        String schemasMap = request.getString("schemas_map");
        String type = request.getString("type", "partial");

        boolean success = false;
        try {
            State.start();
            State.writeLog(
                    request.getLocalizedText("administration.setup.biblivre4restore.log_header"));

            BackupScope restoreScope = backupBO.getBackupScope();

            RestoreDTO dto = restoreBO.getRestoreDTO(filename);

            if (!this.checkForPartialBackup(dto, request, response)) {
                State.cancel();
                return;
            }

            BackupScope backupScope = dto.getBackupScope();

            State.writeLog(backupScope.toString() + " => " + restoreScope.toString());

            Map<String, String> restoreSchemas = new HashMap<>();

            if (restoreScope == BackupScope.SINGLE_SCHEMA) {
                // Se o backup possui schema global e multi bibliotecas não está habilitado,
                // restaura o global também

                if (dto.getSchemas().containsKey(Constants.GLOBAL_SCHEMA)) {
                    restoreSchemas.put(Constants.GLOBAL_SCHEMA, Constants.GLOBAL_SCHEMA);
                }
            }

            if (restoreScope == BackupScope.SINGLE_SCHEMA
                    || restoreScope == BackupScope.SINGLE_SCHEMA_FROM_MULTI_SCHEMA) {
                // Schema de destino é o schema atual e só um schema de origem pode ter sido
                // selecionado
                if (StringUtils.isNotBlank(selectedBackupSchema)) {
                    // Se o usuário selecionou, usa o selecionado
                    restoreSchemas.put(selectedBackupSchema, schema);
                } else {
                    // Se o usuário não selecionou, use o que encontrar. Geralmente só vai ter um,
                    // já que origem MULTI SCHEMA requer seleção acima

                    for (String s : dto.getSchemas().keySet()) {
                        if (!s.equals(Constants.GLOBAL_SCHEMA)) {
                            restoreSchemas.put(s, schema);
                            break;
                        }
                    }
                }
            } else {
                // O destino é multi bibliotecas. Usar o que o usuário selecionou na tela.
                if (type.equals("complete")) {
                    // Marcar para excluir todos os schemas
                    dto.setPurgeAll(true);

                    for (String s : dto.getSchemas().keySet()) {
                        restoreSchemas.put(s, s);
                    }
                } else {
                    restoreSchemas.putAll(this.breakString(schemasMap));
                }
            }

            // Validando se schemas de origem existem no backup e se esquemas de destino são válidos
            Set<String> uniqueCheck = new HashSet<>();
            if (restoreSchemas.size() == 1 && restoreSchemas.containsKey(Constants.GLOBAL_SCHEMA)) {
                throw new ValidationException(
                        "administration.maintenance.backup.error.no_schema_selected");
            }

            for (Entry<String, String> entry : restoreSchemas.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (StringUtils.isBlank(key) || !dto.getSchemas().containsKey(key)) {
                    throw new ValidationException(
                            "administration.maintenance.backup.error.invalid_origin_schema");
                }

                if (StringUtils.isBlank(value) || SchemaBO.isInvalidName(value)) {
                    throw new ValidationException(
                            "administration.maintenance.backup.error.invalid_destination_schema");
                }

                if (key.equals(Constants.GLOBAL_SCHEMA) && !key.equals(value)) {
                    throw new ValidationException(
                            "administration.maintenance.backup.error.invalid_destination_schema");
                }

                uniqueCheck.add(entry.getValue());
            }

            if (uniqueCheck.size() != restoreSchemas.size()) {
                throw new ValidationException(
                        "administration.maintenance.backup.error.duplicated_destination_schema");
            }

            dto.setRestoreScope(restoreScope);
            dto.setRestoreSchemas(restoreSchemas);

            RestoreDTO partialDTO = null;

            if (StringUtils.isNotBlank(mediaFileBackup)) {
                partialDTO = restoreBO.getRestoreDTO(mediaFileBackup);
            }

            success = restoreBO.restore(dto, partialDTO);

            if (success) {
                ConfigurationsDTO cdto =
                        new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
                configurationBO.save(cdto, 0);

                State.finish();
            } else {
                State.cancel();
            }
        } catch (ValidationException e) {
            this.setMessage(e);
            State.writeLog(request.getLocalizedText(e.getMessage()));

            if (e.getCause() != null) {
                State.writeLog(ExceptionUtils.getStackTrace(e.getCause()));
            }

            State.cancel();
        } catch (Throwable e) {
            this.setMessage(e);
            State.writeLog(ExceptionUtils.getStackTrace(e));
            State.cancel();
        }

        put("success", success);
    }

    public void progress(ExtendedRequest request, ExtendedResponse response) {
        put("success", true);
        put("current", State.getCurrentStep());
        put("total", State.getSteps());
        put("secondary_current", State.getCurrentSecondaryStep());
        put("complete", !State.LOCKED.get());
    }

    @Autowired
    public void setRestoreBO(RestoreBO restoreBO) {
        this.restoreBO = restoreBO;
    }

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    @Autowired
    public void setBackupBO(BackupBO backupBO) {
        this.backupBO = backupBO;
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
