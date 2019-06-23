package biblivre.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateUtils {
	public static DateTimeFormatter getFormatter(String language) {
		return DateTimeFormatter
			  .ofLocalizedDateTime(FormatStyle.SHORT)
			  .withLocale(new Locale(language));
	}

	public static String now(String language) {
		return format(LocalDateTime.now(), language);
	}

	public static String format(LocalDateTime date, String language) {
		DateTimeFormatter fmt = getFormatter(language);

		return fmt.format(date);
	}
}
