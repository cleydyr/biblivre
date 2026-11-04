package biblivre.administration.usertype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.ValidationException;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * A reader who was only disabled still occupies a user type. Deleting that type would leave {@code
 * users.type} pointing at a row that no longer exists.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class UserTypeBOTest extends AbstractContainerDatabaseTest {
    private static final int TYPE_ID = 890010;
    private static final int USER_ID = 890011;

    @Autowired DataSource dataSource;
    @Autowired UserTypeBO userTypeBO;

    @BeforeEach
    void setUp() throws Exception {
        SchemaThreadLocal.setSchema("single");
        execute(
                """
                INSERT INTO users_types (id, name, description, lending_limit, reservation_limit,
                    lending_time_limit, reservation_time_limit, fine_value, created_by)
                VALUES (%d, 'Visitante', 'Leitores desativados', 1, 0, 7, 0, 0.5, 1)
                """
                        .formatted(TYPE_ID),
                """
                INSERT INTO users (id, name, type, status, created_by)
                VALUES (%d, 'Leitor inativo', %d, 'inactive', 1)
                """
                        .formatted(USER_ID, TYPE_ID));
    }

    @AfterEach
    void tearDown() throws Exception {
        execute(
                "DELETE FROM users WHERE id = %d".formatted(USER_ID),
                "DELETE FROM users_types WHERE id = %d".formatted(TYPE_ID));
        SchemaThreadLocal.remove();
    }

    @Test
    void deleteRefusesATypeThatInactiveReadersStillReference() {
        ValidationException exception =
                assertThrows(ValidationException.class, () -> userTypeBO.delete(TYPE_ID));

        assertEquals("administration.user_type.error.type_has_users", exception.getMessage());

        UserTypeDTO userType = userTypeBO.get(TYPE_ID);
        assertNotNull(userType);
        assertEquals("Visitante", userType.getName());
        assertEquals(0.5f, userType.getFineValue());
    }

    @Test
    void deleteRemovesATypeThatNoReaderReferences() throws Exception {
        execute("DELETE FROM users WHERE id = %d".formatted(USER_ID));

        assertTrue(userTypeBO.delete(TYPE_ID));

        assertNull(userTypeBO.get(TYPE_ID));
    }

    private void execute(String... statements) throws Exception {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }
}
