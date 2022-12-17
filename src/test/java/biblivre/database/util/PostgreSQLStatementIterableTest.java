package biblivre.database.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class PostgreSQLStatementIterableTest {
    @Test
    void testRemoveWhitespace() {
        String original =
                """
                ALTER TABLE ONLY lendings
                    ADD CONSTRAINT fk_lendings_biblio_holdings FOREIGN KEY (holding_id) REFERENCES biblio_holdings(id) ON DELETE CASCADE;
                """;

        String normalized =
                "ALTER TABLE ONLY lendings ADD CONSTRAINT fk_lendings_biblio_holdings FOREIGN KEY (holding_id) REFERENCES biblio_holdings(id) ON DELETE CASCADE";

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
                "ALTER TABLE ONLY lending_fines ADD CONSTRAINT \"FK_lending_fines_lendings\" FOREIGN KEY (lending_id) REFERENCES lendings(id) ON DELETE CASCADE";

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
                "CREATE FUNCTION unlink() RETURNS trigger LANGUAGE plpgsql AS $$\n"
                        + "BEGIN\n"
                        + "    PERFORM pg_catalog.lo_unlink(OLD.blob);\n"
                        + "    RETURN OLD;\n"
                        + "EXCEPTION WHEN OTHERS THEN\n"
                        + "    RETURN OLD;\n"
                        + "END;\n"
                        + "$$";

        assertEqualsAfterNormalization(original, normalized);
    }

    private void assertEqualsAfterNormalization(String original, String normalized) {
        PostgreSQLStatementIterable postgreSQLStatementIterable =
                new PostgreSQLStatementIterable(
                        new Iterator<Character>() {
                            int i = 0;

                            @Override
                            public Character next() {
                                return original.charAt(i++);
                            }

                            @Override
                            public boolean hasNext() {
                                return i != original.length();
                            }
                        });

        assertEquals(normalized, postgreSQLStatementIterable.next());
    }
}
