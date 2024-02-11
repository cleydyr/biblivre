package biblivre.administration.backup;

import biblivre.core.utils.Constants;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RestoreContextHelper {
    private final Map<String, String> deleteSchemas = new HashMap<>();
    private final Map<String, String> preRenameSchemas = new HashMap<>();
    private final Map<String, String> postRenameSchemas = new HashMap<>();
    private final Map<String, String> restoreRenamedSchemas = new HashMap<>();

    public Map<String, String> getDeleteSchemas() {
        return deleteSchemas;
    }

    public Map<String, String> getPreRenameSchemas() {
        return preRenameSchemas;
    }

    public Map<String, String> getPostRenameSchemas() {
        return postRenameSchemas;
    }

    public Map<String, String> getRestoreRenamedSchemas() {
        return restoreRenamedSchemas;
    }

    public RestoreContextHelper(RestoreOperation restore, Set<String> databaseSchemas) {

        long currentTimestamp = new Date().getTime();

        Map<String, String> restoreSchemas =
                getSchemasToRestore(restore, databaseSchemas, currentTimestamp);

        restoreSchemas
                .keySet()
                .forEach(
                        originalSchemaName -> {
                            if (!deleteSchemas.containsKey(originalSchemaName)
                                    && databaseSchemas.contains(originalSchemaName)) {

                                String tempSchemaName =
                                        "_" + originalSchemaName + "_" + currentTimestamp;

                                preRenameSchemas.put(originalSchemaName, tempSchemaName);

                                restoreRenamedSchemas.put(tempSchemaName, originalSchemaName);
                            }
                        });

        if (restore.isPurgeAll()) {
            for (String remainingSchema : databaseSchemas) {
                if (_isRelevantSchema(remainingSchema)
                        && !deleteSchemas.containsKey(remainingSchema)) {

                    deleteSchemas.put(remainingSchema, remainingSchema);
                }
            }
        }
    }

    private Map<String, String> getSchemasToRestore(
            RestoreOperation restore, Set<String> databaseSchemas, long currentTimestamp) {
        Map<String, String> restoreSchemas = restore.getRestoreSchemas();

        restoreSchemas.forEach(
                (originalSchemaName, finalSchemaName) -> {
                    if (databaseSchemas.contains(finalSchemaName)) {
                        String tempSchemaName = "_" + finalSchemaName + "_" + currentTimestamp;

                        preRenameSchemas.put(finalSchemaName, tempSchemaName);

                        deleteSchemas.put(finalSchemaName, tempSchemaName);
                    }

                    if (!originalSchemaName.equals(finalSchemaName)) {
                        postRenameSchemas.put(originalSchemaName, finalSchemaName);
                    }
                });
        return restoreSchemas;
    }

    private boolean _isRelevantSchema(String remainingSchema) {
        return !Constants.GLOBAL_SCHEMA.equals(remainingSchema)
                && !"public".equals(remainingSchema);
    }
}
