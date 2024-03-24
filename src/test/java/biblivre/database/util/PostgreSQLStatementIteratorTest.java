package biblivre.database.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.administration.backup.BufferedReaderIterator;
import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class PostgreSQLStatementIteratorTest {
    @Test
    void testRemoveWhitespace() {
        String original =
                """
                ALTER TABLE ONLY lendings
                    ADD CONSTRAINT fk_lendings_biblio_holdings FOREIGN KEY (holding_id) REFERENCES biblio_holdings(id) ON DELETE CASCADE;
                """;

        String normalized =
                "ALTER TABLE ONLY lendings ADD CONSTRAINT fk_lendings_biblio_holdings FOREIGN KEY (holding_id) REFERENCES biblio_holdings(id) ON DELETE CASCADE;";

        assertEqualsAfterNormalization(original, normalized);
    }

    @Test
    void testNoComments() {
        String original =
                """
                --
                -- Name: FK_lending_fines_lendings; Type: FK CONSTRAINT; Schema: single; Owner: biblivre
                --

                ALTER TABLE ONLY lending_fines
                    ADD CONSTRAINT "FK_lending_fines_lendings" FOREIGN KEY (lending_id) REFERENCES lendings(id) ON DELETE CASCADE;
                """;

        String normalized =
                "ALTER TABLE ONLY lending_fines ADD CONSTRAINT \"FK_lending_fines_lendings\" FOREIGN KEY (lending_id) REFERENCES lendings(id) ON DELETE CASCADE;";

        assertEqualsAfterNormalization(original, normalized);
    }

    @Test
    void testDollarQuotesString() {
        String original =
                """
                --
                -- Name: unlink(); Type: FUNCTION; Schema: global; Owner: biblivre
                --

                CREATE FUNCTION unlink() RETURNS trigger
                    LANGUAGE plpgsql
                    AS $$
                BEGIN
                    PERFORM pg_catalog.lo_unlink(OLD.blob);
                    RETURN OLD;
                EXCEPTION WHEN OTHERS THEN
                    RETURN OLD;
                END;
                $$;
                """;

        String normalized =
                """
                CREATE FUNCTION unlink() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN
                    PERFORM pg_catalog.lo_unlink(OLD.blob);
                    RETURN OLD;
                EXCEPTION WHEN OTHERS THEN
                    RETURN OLD;
                END;
                $$;""";

        assertEqualsAfterNormalization(original, normalized);
    }

    @Test
    void testDollarQuotesStringWithNonEmptyTag() {
        String tagName = "Mdeqe6H91y";

        String original =
                        """
                --
                -- Name: unlink(); Type: FUNCTION; Schema: global; Owner: biblivre
                --

                CREATE FUNCTION unlink() RETURNS trigger
                    LANGUAGE plpgsql
                    AS $%s$
                BEGIN
                    PERFORM pg_catalog.lo_unlink(OLD.blob);
                    RETURN OLD;
                EXCEPTION WHEN OTHERS THEN
                    RETURN OLD;
                END;
                $%s$;
                """
                        .formatted(tagName, tagName);

        String normalized =
                        """
                CREATE FUNCTION unlink() RETURNS trigger LANGUAGE plpgsql AS $%s$
                BEGIN
                    PERFORM pg_catalog.lo_unlink(OLD.blob);
                    RETURN OLD;
                EXCEPTION WHEN OTHERS THEN
                    RETURN OLD;
                END;
                $%s$;"""
                        .formatted(tagName, tagName);

        assertEqualsAfterNormalization(original, normalized);
    }

    @Test
    void testDollarQuotesStringWithNonEmptyTag2() {
        String tagName = "BjoUqrPZzj";

        String original =
                        """
                SELECT $%s$foobar$%s$;
                """
                        .formatted(tagName, tagName);

        String normalized = "SELECT $%s$foobar$%s$;".formatted(tagName, tagName);

        assertEqualsAfterNormalization(original, normalized);
    }

    @Test
    void testDollarQuotesStringWithNonEmptyTag3() {
        String tagName = "rTjqaWKJrA";

        String original =
                        """
                SELECT $%s$a$b$%s$;
                """
                        .formatted(tagName, tagName);

        String normalized = "SELECT $%s$a$b$%s$;".formatted(tagName, tagName);

        assertEqualsAfterNormalization(original, normalized);
    }

    private void assertEqualsAfterNormalization(String original, String normalized) {
        try {
            PostgreSQLStatementIterable postgreSQLStatementIterable =
                    new PostgreSQLStatementIterable(
                            new BufferedReaderIterator(
                                    new BufferedReader(new java.io.StringReader(original))));
            StringBuilder sb = new StringBuilder();

            for (String statement : postgreSQLStatementIterable) {
                sb.append(statement);
            }

            assertEquals(normalized, sb.toString());
        } catch (IOException e) {
            fail(e);
        }
    }
}
