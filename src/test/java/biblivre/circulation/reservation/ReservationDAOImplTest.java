package biblivre.circulation.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Round-trips the reservation expiry date through PostgreSQL. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class ReservationDAOImplTest extends AbstractContainerDatabaseTest {
    private static final int RECORD_ID = 880011;
    private static final int USER_ID = 880013;

    @Autowired DataSource dataSource;
    @Autowired ReservationDAOImpl reservationDAO;

    @BeforeEach
    void setUp() throws Exception {
        SchemaThreadLocal.setSchema("single");

        execute(
                "INSERT INTO biblio_records (id, iso2709) VALUES (%d, '')".formatted(RECORD_ID),
                "INSERT INTO users (id, name, status) VALUES (%d, 'Test', 'active')"
                        .formatted(USER_ID));
    }

    @AfterEach
    void tearDown() throws Exception {
        // reservations are removed by ON DELETE CASCADE on the record
        execute(
                "DELETE FROM users WHERE id = %d".formatted(USER_ID),
                "DELETE FROM biblio_records WHERE id = %d".formatted(RECORD_ID));
    }

    @Test
    void insert_roundTripsTheExpiryDate() {
        LocalDate expires = LocalDate.now().plusDays(10);

        int id = reservationDAO.insert(reservation(expires));

        ReservationDTO stored = reservationDAO.get(id);

        assertEquals(expires, stored.getExpires());
        assertNotNull(stored.getCreated());
    }

    @Test
    void activeReservationsAreThoseExpiringAfterNow() {
        reservationDAO.insert(reservation(LocalDate.now().plusDays(3)));
        reservationDAO.insert(reservation(LocalDate.now().minusDays(3)));

        List<ReservationDTO> queue = reservationDAO.listByRecordId(RECORD_ID);

        assertEquals(1, queue.size());
        assertTrue(queue.get(0).getExpires().isAfter(LocalDate.now()));

        assertTrue(reservationDAO.deleteExpired());
    }

    private static ReservationDTO reservation(LocalDate expires) {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setRecordId(RECORD_ID);
        reservation.setUserId(USER_ID);
        reservation.setExpires(expires);

        return reservation;
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
