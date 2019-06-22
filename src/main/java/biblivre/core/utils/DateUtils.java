package biblivre.core.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateUtils {
	public static DateTimeFormatter getFormatter(String language) {
		return DateTimeFormatter
			  .ofLocalizedDateTime(FormatStyle.SHORT)
			  .withLocale(new Locale(language));
	}
}
