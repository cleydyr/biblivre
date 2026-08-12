package biblivre.cataloging.search.intelligent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Registers the annotated bean only when intelligent search is enabled.
 *
 * <p>Schema updates that require pgvector use this so classic-only installs (enabled=false) do not
 * attempt {@code CREATE EXTENSION vector} or related DDL. Skipped updates are not recorded as
 * installed, so enabling the feature later applies them on the next boot.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "biblivre.search.intelligent.enabled", havingValue = "true")
public @interface ConditionalOnIntelligentSearchEnabled {}
