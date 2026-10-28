package biblivre.circulation.lending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.lending.persistence.LendingFineDAO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.SchemaThreadLocal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Round-trips the lending dates through PostgreSQL. The columns are {@code timestamp without time
 * zone}, so this pins how {@link LocalDate} and {@link Instant} are written and read back.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class LendingDAOImplTest extends AbstractContainerDatabaseTest {
    private static final int RECORD_ID = 880001;
    private static final int HOLDING_ID = 880002;
    private static final int USER_ID = 880003;

    @Autowired DataSource dataSource;
    @Autowired LendingDAOImpl lendingDAO;
    @Autowired LendingFineDAO lendingFineDAO;

    @BeforeEach
    void setUp() throws Exception {
        SchemaThreadLocal.setSchema("single");

        execute(
                "INSERT INTO biblio_records (id, iso2709) VALUES (%d, '')".formatted(RECORD_ID),
                ("INSERT INTO biblio_holdings (id, record_id, iso2709, accession_number) "
                                + "VALUES (%d, %d, '', 'T-1')")
                        .formatted(HOLDING_ID, RECORD_ID),
                "INSERT INTO users (id, name, status) VALUES (%d, 'Test', 'active')"
                        .formatted(USER_ID));
    }

    @AfterEach
    void tearDown() throws Exception {
        // lendings and lending_fines are removed by ON DELETE CASCADE
        execute(
                "DELETE FROM users WHERE id = %d".formatted(USER_ID),
                "DELETE FROM biblio_holdings WHERE id = %d".formatted(HOLDING_ID),
                "DELETE FROM biblio_records WHERE id = %d".formatted(RECORD_ID));
    }

    @Test
    void doLend_storesTheExpectedReturnDateAsADate() {
        LocalDate expected = LocalDate.of(2026, 3, 9);

        lend(expected);

        LendingDTO lending = lendingDAO.getCurrentLending(holding());

        assertNotNull(lending);
        assertEquals(expected, lending.getExpectedReturnDate());
        assertNull(lending.getReturnDate());
        assertNotNull(lending.getCreated());
    }

    @Test
    void doReturn_setsAReturnInstant() {
        lend(LocalDate.of(2026, 3, 9));
        LendingDTO lending = lendingDAO.getCurrentLending(holding());

        lendingDAO.doReturn(lending.getId());

        LendingDTO returned = lendingDAO.get(lending.getId());

        assertNotNull(returned.getReturnDate());
        assertEquals(LocalDate.of(2026, 3, 9), returned.getExpectedReturnDate());
    }

    @Test
    void undoReturn_clearsTheReturnInstant() {
        lend(LocalDate.of(2026, 3, 9));
        int id = lendingDAO.getCurrentLending(holding()).getId();
        lendingDAO.doReturn(id);

        assertTrue(lendingDAO.undoReturn(id));

        assertNull(lendingDAO.get(id).getReturnDate());
    }

    @Test
    void doRenew_closesTheLendingAndOpensAnotherWithTheNewExpectedDate() {
        lend(LocalDate.of(2026, 3, 9));
        int id = lendingDAO.getCurrentLending(holding()).getId();

        assertTrue(lendingDAO.doRenew(id, LocalDate.of(2026, 3, 23), 1));

        assertNotNull(lendingDAO.get(id).getReturnDate());

        LendingDTO renewal = lendingDAO.getCurrentLending(holding());

        assertEquals(LocalDate.of(2026, 3, 23), renewal.getExpectedReturnDate());
        assertEquals(id, renewal.getPreviousLendingId());
        assertNull(renewal.getReturnDate());
    }

    @Test
    void hasLateLendings_comparesTheExpectedDateWithNow() {
        lend(LocalDate.now().minusDays(1));

        assertTrue(lendingDAO.hasLateLendings(USER_ID));

        LendingDTO lending = lendingDAO.getCurrentLending(holding());
        lendingDAO.doReturn(lending.getId());

        assertFalse(lendingDAO.hasLateLendings(USER_ID));
    }

    @Test
    void fine_unpaidHasNoPaymentAndUpdateMarksItPaid() {
        int lendingId = lendAndGetId();

        LendingFineDTO fine = fine(lendingId, null);
        lendingFineDAO.insert(fine);

        LendingFineDTO unpaid = lendingFineDAO.get(fine.getId());

        assertNull(unpaid.getPayment());
        assertFalse(unpaid.isPaid());

        unpaid.setValue(3.5f);
        assertTrue(lendingFineDAO.update(unpaid));

        LendingFineDTO paid = lendingFineDAO.getByLendingId(lendingId);

        assertNotNull(paid.getPayment());
        assertTrue(paid.isPaid());
    }

    /** A fine created already paid keeps the day precision it has always been stored with. */
    @Test
    void fine_createdAsPaidStoresThePaymentDay() {
        int lendingId = lendAndGetId();
        Instant payment = Instant.parse("2026-03-09T15:30:00Z");

        LendingFineDTO fine = fine(lendingId, payment);
        lendingFineDAO.insert(fine);

        Instant stored = lendingFineDAO.get(fine.getId()).getPayment();

        ZoneId zone = ZoneId.systemDefault();

        assertEquals(payment.atZone(zone).toLocalDate(), stored.atZone(zone).toLocalDate());
        assertEquals(java.time.LocalTime.MIDNIGHT, stored.atZone(zone).toLocalTime());
    }

    private int lendAndGetId() {
        lend(LocalDate.of(2026, 3, 9));

        return lendingDAO.getCurrentLending(holding()).getId();
    }

    private void lend(LocalDate expectedReturnDate) {
        LendingDTO lending = new LendingDTO();
        lending.setHoldingId(HOLDING_ID);
        lending.setUserId(USER_ID);
        lending.setExpectedReturnDate(expectedReturnDate);

        assertTrue(lendingDAO.doLend(lending));
    }

    private static HoldingDTO holding() {
        HoldingDTO holding = new HoldingDTO();
        holding.setId(HOLDING_ID);

        return holding;
    }

    private static LendingFineDTO fine(int lendingId, Instant payment) {
        UserDTO user = new UserDTO();
        user.setId(USER_ID);

        LendingFineDTO fine = new LendingFineDTO();
        fine.setUserId(USER_ID);
        fine.setLendingId(lendingId);
        fine.setValue(2.5f);
        fine.setPayment(payment);

        return fine;
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
