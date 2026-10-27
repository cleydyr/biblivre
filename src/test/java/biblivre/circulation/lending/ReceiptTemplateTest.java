package biblivre.circulation.lending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.holding.HoldingDTO;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/** Renders the real receipt template the way {@link LendingBO} does, with the SpEL compiler on. */
class ReceiptTemplateTest {

    @Test
    void rendersLendingDatesWithTheTranslatedPatterns() {
        LendingBag lendingBag = lendingBag();
        lendingBag.getLending().setExpectedReturnDate(LocalDate.of(2026, 3, 9));

        String html = render(Map.of("currentLendings", new ArrayList<>(List.of(lendingBag))));

        assertTrue(html.contains("09/03/2026"), html);
    }

    @Test
    void rendersReturnsWithTheActualReturnInstantNotTheExpectedDate() {
        LendingBag lendingBag = lendingBag();
        lendingBag.getLending().setExpectedReturnDate(LocalDate.of(2026, 3, 9));
        Instant returnedAt = LocalDate.of(2026, 3, 12).atTime(14, 5).atZone(zone()).toInstant();
        lendingBag.getLending().setReturnDate(returnedAt);

        String html = render(Map.of("currentReturns", new ArrayList<>(List.of(lendingBag))));

        assertTrue(html.contains("12/03/2026 14:05"), html);
        assertEquals(-1, html.indexOf("09/03/2026"), html);
    }

    @Test
    void usesTheDefaultLayoutWhenATranslationHoldsAnInvalidPattern() {
        // "aaaa" is what the Spanish translation ships; SimpleDateFormat tolerated it.
        ReceiptDateFormatter formats = new ReceiptDateFormatter("dd/MM/aaaa", "dd/MM/yyyy HH:mm");

        assertEquals("09/03/2026", formats.formatDate(LocalDate.of(2026, 3, 9)));
    }

    @Test
    void formatsNullAsBlank() {
        ReceiptDateFormatter formats = new ReceiptDateFormatter("dd/MM/yyyy", "dd/MM/yyyy HH:mm");

        assertEquals("", formats.formatDate(null));
        assertEquals("", formats.formatDateTime((Instant) null));
        assertEquals("", formats.formatDateTime((Date) null));
    }

    @Test
    void formatsAuditTimestampsHeldAsDate() {
        ReceiptDateFormatter formats = new ReceiptDateFormatter("dd/MM/yyyy", "dd/MM/yyyy HH:mm");
        Instant instant = Instant.parse("2026-03-09T15:30:00Z");

        assertEquals(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        .format(instant.atZone(ZoneId.systemDefault())),
                formats.formatDateTime(Date.from(instant)));
    }

    private static String render(Map<String, Object> overrides) {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/main/webapp/WEB-INF/templates/");
        resolver.setSuffix(".template");
        resolver.setTemplateMode(TemplateMode.TEXT);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.setEnableSpringELCompiler(true);

        LendingBag any = lendingBag();

        Map<String, Object> root = new HashMap<>();
        root.put("formats", new ReceiptDateFormatter("dd/MM/yyyy", "dd/MM/yyyy HH:mm"));
        root.put("lendingInfo", new ArrayList<>(List.of(any)));
        root.put("libraryName", "Library");
        root.put("now", "now");
        root.put("nameLabel", "Name");
        root.put("userName", "Reader");
        root.put("idLabel", "Id");
        root.put("enrollment", "1");
        root.put("currentLendings", new ArrayList<>());
        root.put("currentRenews", new ArrayList<>());
        root.put("currentReturns", new ArrayList<>());
        root.putAll(overrides);

        StringWriter writer = new StringWriter();
        engine.process("receipt", new Context(Locale.ROOT, root), writer);

        return writer.toString();
    }

    private static LendingBag lendingBag() {
        BiblioRecordDTO biblio = new BiblioRecordDTO();
        biblio.setAuthor("Author");
        biblio.setTitle("Title");

        HoldingDTO holding = new HoldingDTO();
        holding.setId(1);
        holding.setAccessionNumber("A-1");

        LendingBag bag = new LendingBag();
        bag.setBiblio(biblio);
        bag.setHolding(holding);
        bag.setLending(new LendingDTO());

        return bag;
    }

    private static ZoneId zone() {
        return ZoneId.systemDefault();
    }
}
