package biblivre.administration.reports;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JasperReportUtilsTest {

    @Test
    void formatDeweyString() {
        assertEquals("", ReportUtils.formatDeweyString("", 3));
        assertEquals("000", ReportUtils.formatDeweyString("000", 3));
        assertEquals("123", ReportUtils.formatDeweyString("1234", 3));
        assertEquals("5123", ReportUtils.formatDeweyString("512312", 4));
        assertEquals("512312", ReportUtils.formatDeweyString("512312", -1));
        assertEquals("540", ReportUtils.formatDeweyString("543", 2));
        assertEquals("500", ReportUtils.formatDeweyString("543", 1));
    }
}
