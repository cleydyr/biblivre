package biblivre.core;

import static biblivre.core.utils.Constants.FROM_CM;

import com.github.cleydyr.pimaco.PimacoTagSheet;
import com.github.cleydyr.pimaco.SheetSize;
import com.lowagie.text.RectangleReadOnly;
import lombok.Getter;

@Getter
public class ITextPimacoTagSheetAdapter {

    public ITextPimacoTagSheetAdapter(String model) {
        PimacoTagSheet sheet = PimacoTagSheet.fromPimacoCode(model).orElseThrow();
        float verticalMargin = FROM_CM.apply(sheet.getVerticalMargin());
        float horizontalPadding = FROM_CM.apply(sheet.getHoriziontalSpacing().divide(2));
        float horizontalMargin = FROM_CM.apply(sheet.getHorizontalMargin());
        float verticalPadding = FROM_CM.apply(sheet.getVerticalSpacing().divide(2));
        float tagHeight = FROM_CM.apply(sheet.getTagHeight());
        float cellHeight = 2 * verticalPadding + tagHeight;
        SheetSize sheetSize = sheet.getSheetSize();
        float width = FROM_CM.apply(sheetSize.getSheetWidth());
        float height = FROM_CM.apply(sheetSize.getSheetHeight());
        final float realHorizontalMargin = horizontalMargin - horizontalPadding;

        this.verticalMargin = verticalMargin - verticalPadding;
        this.horizontalMargin = realHorizontalMargin;
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
        this.columns = sheet.getColumns();
        this.rows = sheet.getRows();
        this.pageSize = new RectangleReadOnly(width, height);
        this.cellHeight = cellHeight;
    }

    private final float verticalMargin;
    private final float horizontalMargin;
    private final float verticalPadding;
    private final float horizontalPadding;
    private final RectangleReadOnly pageSize;
    private final int rows;
    private final int columns;
    private final float cellHeight;
}
