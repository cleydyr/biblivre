package biblivre.circulation.lending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import biblivre.circulation.reservation.ReservationDTO;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * The SPA and the legacy JSPs parse these fields with {@code new Date(value)}, so the wire format
 * must stay what {@link Date} fields were always serialized as: local time, no offset.
 */
class LendingDTOJsonTest {

    @Test
    void expectedReturnDate_isSerializedAsLocalMidnight() {
        LendingDTO lending = new LendingDTO();
        lending.setExpectedReturnDate(LocalDate.of(2026, 3, 9));

        JSONObject json = lending.toJSONObject();

        assertEquals("2026-03-09T00:00:00", json.getString("expectedReturnDate"));
    }

    @Test
    void returnDate_isSerializedExactlyLikeTheLegacyDateField() {
        Instant returnedAt = Instant.parse("2026-03-09T15:30:45.123Z");

        LendingDTO lending = new LendingDTO();
        lending.setReturnDate(returnedAt);

        String legacy =
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(Date.from(returnedAt));

        assertEquals(legacy, lending.toJSONObject().getString("returnDate"));
        assertEquals(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(returnedAt.atZone(ZoneId.systemDefault())),
                legacy);
    }

    @Test
    void openLending_omitsTheReturnDate() {
        LendingDTO lending = new LendingDTO();
        lending.setExpectedReturnDate(LocalDate.of(2026, 3, 9));

        assertFalse(lending.toJSONObject().has("returnDate"));
    }

    @Test
    void finePayment_isSerializedLikeTheLegacyDateField() {
        Instant paidAt = Instant.parse("2026-03-09T15:30:45Z");

        LendingFineDTO fine = new LendingFineDTO();
        fine.setPayment(paidAt);

        assertEquals(
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(Date.from(paidAt)),
                fine.toJSONObject().getString("payment"));
    }

    @Test
    void reservationExpiry_isSerializedAsLocalMidnight() {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setExpires(LocalDate.of(2026, 3, 9));

        assertEquals("2026-03-09T00:00:00", reservation.toJSONObject().getString("expires"));
    }
}
