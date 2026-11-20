package biblivre.digitalmedia;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Drops direct uploads that were signed but never confirmed, in every library: the object in the
 * store and the pending row. A PUT without a complete never becomes media.
 */
@Component
@ConditionalOnProperty(
        value = "DIGITAL_MEDIA_DAO_IMPL",
        havingValue = "biblivre.digitalmedia.s3.S3DigitalMediaDAO")
@Slf4j
public class PendingUploadSweeper {
    private SchemaBO schemaBO;

    private DigitalMediaBO digitalMediaBO;

    @Scheduled(
            initialDelayString = "${digital-media.pending-sweep-initial-delay-ms:600000}",
            fixedDelayString = "${digital-media.pending-sweep-delay-ms:3600000}")
    public void sweep() {
        for (SchemaDTO schema : schemaBO.getSchemas()) {
            try {
                int swept =
                        SchemaThreadLocal.withSchema(
                                schema.getSchema(), digitalMediaBO::sweepExpiredUploads);

                if (swept > 0) {
                    log.info("Dropped {} expired media uploads from {}", swept, schema.getSchema());
                }
            } catch (RuntimeException e) {
                log.error("Could not sweep expired media uploads of {}", schema.getSchema(), e);
            }
        }
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }

    @Autowired
    public void setDigitalMediaBO(DigitalMediaBO digitalMediaBO) {
        this.digitalMediaBO = digitalMediaBO;
    }
}
