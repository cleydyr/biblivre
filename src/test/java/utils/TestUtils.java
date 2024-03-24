package utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.random.RandomGenerator;
import org.jetbrains.annotations.NotNull;

public class TestUtils {
    private static final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    @NotNull
    public static Date getRandomDate() {
        return Date.from(
                LocalDate.now()
                        .plusDays(randomGenerator.nextInt(1000 * 365))
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant());
    }
}
