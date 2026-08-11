package biblivre.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SqlPartsTest {
    @Test
    void append_keepsFragmentsAndParametersInOrder() {
        SqlParts sqlParts = new SqlParts();
        sqlParts.append("SELECT * FROM records WHERE search_id = ? ", 42);
        sqlParts.append("AND indexing_group_id = ? ", 3);
        sqlParts.append("OFFSET ? LIMIT ?", 10L, 20L);

        assertEquals(
                "SELECT * FROM records WHERE search_id = ? AND indexing_group_id = ? OFFSET ?"
                        + " LIMIT ?",
                sqlParts.sql());
        assertArrayEquals(new Object[] {42, 3, 10L, 20L}, sqlParts.parameters());
    }

    @Test
    void append_allowsFragmentsWithoutParameters() {
        SqlParts sqlParts = new SqlParts();
        sqlParts.append("SELECT * FROM ");
        sqlParts.append("biblio_records ");
        sqlParts.append("WHERE id = ?", 7);

        assertEquals("SELECT * FROM biblio_records WHERE id = ?", sqlParts.sql());
        assertArrayEquals(new Object[] {7}, sqlParts.parameters());
    }

    @Test
    void assertPlaceholderCount_rejectsMismatchedParameters() {
        SqlParts sqlParts = new SqlParts();
        sqlParts.append("WHERE search_id = ? AND indexing_group_id = ?", 1);

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, sqlParts::assertPlaceholderCount);
        assertEquals("SQL has 2 placeholders but 1 parameters", exception.getMessage());
    }

    @Test
    void assertPlaceholderCount_acceptsMatchingParameters() {
        SqlParts sqlParts = new SqlParts();
        sqlParts.append("WHERE search_id = ?", 1);
        sqlParts.append(" LIMIT ?", 5L);

        sqlParts.assertPlaceholderCount();
    }
}
