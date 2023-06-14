package biblivre.database.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;
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

    @Test
    void testCopyCommand() {
        String original =
                """
                COPY authorities_form_subfields (datafield, subfield, collapsed, repeatable, created, created_by, modified, modified_by, autocomplete_type, sort_order) FROM stdin;
                100 a   f   f   2014-02-08 15:26:31.667337  \\N  2014-02-08 15:26:31.667337  \\N  disabled    197
                \\.
                """;

        assertEqualsAfterNormalization(original, original);
    }

    @Test
    void testCopyCommand2() {
        String original =
                """
                COPY backups (id, created, path, schemas, type, scope, downloaded, steps, current_step) FROM stdin;
                1   2019-01-17 08:11:24.159 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   1
                2   2019-02-25 15:22:04.689 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   1
                9   2022-01-18 17:48:30.449 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} exclude_digital_media   single_schema   t   5   0
                4   2021-09-13 14:57:20.551 C:\\Biblivre\\Biblivre Backup 2021-09-13 14h57m25s Full.b5bz    {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   t   6   6
                5   2022-01-18 17:15:09.747 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   t   6   0
                42  2022-01-21 12:12:45.635 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                43  2022-01-26 09:59:53.447 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                44  2022-01-26 15:15:27.734 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                3   2021-02-09 19:35:11.421 C:\\Biblivre\\Biblivre Backup 2021-02-09 15h35m18s Full.b5bz    {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   6
                50  2022-07-11 11:23:40.955 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   t   6   0
                51  2022-09-08 11:23:28.426 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                52  2022-09-08 16:37:29.655 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                53  2022-12-16 10:11:14.672 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   1
                6   2022-01-18 17:24:05.265 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                7   2022-01-18 17:24:12.553 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                8   2022-01-18 17:48:06.098 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                10  2022-01-18 17:48:33.112 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   t   6   0
                41  2022-01-21 12:08:00.737 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                45  2022-02-10 08:24:07.058 C:\\Biblivre\\Biblivre Backup 2022-02-10 09h24m14s Full.b5bz    {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   t   6   6
                46  2022-02-22 10:19:09.643 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                47  2022-05-16 08:59:24.73  \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                48  2022-05-19 09:18:24.263 C:\\Biblivre\\Biblivre Backup 2022-05-19 09h18m32s Full.b5bz    {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   t   6   6
                49  2022-06-15 09:25:40.578 \\N  {"single":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"},"global":{"left":"Biblivre V","right":"Software Livre para Gestão de Bibliotecas"}} full    single_schema   f   6   0
                \\.
                """;

        String normalized = original;

        assertEqualsAfterNormalization(original, normalized);
    }

    @Test
    void testCopyCommand3() {
        String original =
                """
                SET search_path = global, pg_catalog;

                --
                -- Data for Name: backups; Type: TABLE DATA; Schema: global; Owner: biblivre
                --

                COPY backups (id, created, path, schemas, type, scope, downloaded, steps, current_step) FROM stdin;
                \\.


                --
                -- Name: backups_id_seq; Type: SEQUENCE SET; Schema: global; Owner: biblivre
                --

                SELECT pg_catalog.setval('backups_id_seq', 1, false);
                """;

        String normalized =
                """
                SET search_path = global, pg_catalog;COPY backups (id, created, path, schemas, type, scope, downloaded, steps, current_step) FROM stdin;
                \\.
                SELECT pg_catalog.setval('backups_id_seq', 1, false);""";

        assertEqualsAfterNormalization(original, normalized);
    }

    private void assertEqualsAfterNormalization(String original, String normalized) {
        PostgreSQLStatementIterable postgreSQLStatementIterable =
                new PostgreSQLStatementIterable(
                        new Iterator<>() {
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

        StringBuilder sb = new StringBuilder();

        for (String statement : postgreSQLStatementIterable) {
            sb.append(statement);
        }

        assertEquals(normalized, sb.toString());
    }
}
