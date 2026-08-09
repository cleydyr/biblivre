package biblivre.circulation.lending;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class LendingFineDTOTest {

    @Test
    void toJSONObject_includesPaymentWhenFineIsPaid() {
        LendingFineDTO fine = new LendingFineDTO();
        fine.setId(1);
        fine.setLendingId(10);
        fine.setUserId(20);
        fine.setValue(5.0f);
        fine.setPayment(new Date());

        JSONObject json = fine.toJSONObject();

        assertTrue(
                json.has("payment"),
                "paid Fine must serialize payment so the SPA can hide pay/waive/adjust");
        assertFalse(json.isNull("payment"));
    }
}
