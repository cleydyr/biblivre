package biblivre.cataloging.labels;

import biblivre.core.ITextPimacoTagSheetAdapter;
import biblivre.core.file.DiskFile;
import biblivre.labels.print.LabelPrintDTO;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ITextLabelGenerator implements LabelGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ITextLabelGenerator.class);

    @Override
    public DiskFile generate(
            List<LabelDTO> labels, LabelPrintDTO printDTO, int horizontalAlignment) {
        try {
            ITextPimacoTagSheetAdapter adapter =
                    new ITextPimacoTagSheetAdapter(printDTO.getModel());

            Document document =
                    new Document(
                            adapter.getPageSize(),
                            adapter.getHorizontalMargin(),
                            0,
                            adapter.getVerticalMargin(),
                            0);

            File tempLabelsFile = File.createTempFile("biblivre_label_", ".pdf");

            try (OutputStream outputStream = Files.newOutputStream(tempLabelsFile.toPath())) {
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);

                PdfPTable table = new PdfPTable(adapter.getColumns());

                document.open();

                table.setWidthPercentage(100f);

                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                float fixedHeight = adapter.getCellHeight();

                _skipOffset(printDTO, horizontalAlignment, table, fixedHeight);

                for (LabelDTO ldto : labels) {
                    _printOddLabels(
                            printDTO, fixedHeight, writer, table, horizontalAlignment, ldto);

                    _printEvenLabels(fixedHeight, table, ldto, horizontalAlignment);
                }

                table.completeRow();

                document.add(table);

                writer.flush();

                document.close();

                return new DiskFile(tempLabelsFile, "application/pdf");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private void _skipOffset(
            LabelPrintDTO printDTO, int horizontalAlignment, PdfPTable table, float fixedHeight) {

        for (int i = 0; i < printDTO.getOffset(); i++) {
            PdfPCell cell = _getNewCell(fixedHeight, horizontalAlignment);

            table.addCell(cell);
        }
    }

    private PdfPCell _getNewCell(float fixedHeight, int horizontalAlignment) {
        return _getNewCell(fixedHeight, horizontalAlignment, null);
    }

    private void _printOddLabels(
            LabelPrintDTO printDTO,
            float fixedHeight,
            PdfWriter writer,
            PdfPTable table,
            int horizontalAlignment,
            LabelDTO ldto) {
        PdfPCell cell;
        PdfContentByte cb = writer.getDirectContent();

        StringBuilder holdingSerial = new StringBuilder(String.valueOf(ldto.getId()));
        while (holdingSerial.length() < 10) {
            holdingSerial.insert(0, "0");
        }
        Barcode39 code39 = new Barcode39();
        code39.setExtended(true);
        code39.setCode(holdingSerial.toString());
        code39.setStartStopText(false);

        Image image39 = code39.createImageWithBarcode(cb, null, null);
        if (printDTO.getHeight() > 30.0f) {
            image39.scalePercent(110f);
        } else {
            image39.scalePercent(90f);
        }

        Paragraph para = new Paragraph();
        Phrase p1 = new Phrase(StringUtils.left(ldto.getAuthor(), 28) + "\n");
        Phrase p2 = new Phrase(StringUtils.left(ldto.getTitle(), 28) + "\n\n");
        Phrase p3 = new Phrase(new Chunk(image39, 0, 0));
        para.add(p1);
        para.add(p2);
        para.add(p3);

        cell = _getNewCell(fixedHeight, horizontalAlignment, para);
        table.addCell(cell);
    }

    private PdfPCell _getNewCell(float fixedHeight, int horizontalAlignment, Paragraph para) {
        PdfPCell cell;
        cell = new PdfPCell(para);
        cell.setNoWrap(true);
        cell.setFixedHeight(fixedHeight);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void _printEvenLabels(
            float fixedHeight, PdfPTable table, LabelDTO ldto, int horizontalAlignment) {
        PdfPCell cell;
        Paragraph para = new Paragraph();
        Phrase p5 = new Phrase(ldto.getLocationA() + "\n");
        Phrase p6 = new Phrase(ldto.getLocationB() + "\n");
        Phrase p7 = new Phrase(ldto.getLocationC() + "\n");
        Phrase p8 = new Phrase(ldto.getLocationD() + "\n");
        Phrase p4 = new Phrase(ldto.getAccessionNumber() + "\n");
        para.add(p5);
        para.add(p6);
        para.add(p7);
        para.add(p8);
        para.add(p4);

        cell = _getNewCell(fixedHeight, horizontalAlignment, para);
        table.addCell(cell);
    }
}
