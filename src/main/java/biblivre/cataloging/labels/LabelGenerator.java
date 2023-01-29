package biblivre.cataloging.labels;

import biblivre.core.file.DiskFile;
import biblivre.labels.print.LabelPrintDTO;
import java.util.List;

public interface LabelGenerator {
    DiskFile generate(List<LabelDTO> labels, LabelPrintDTO printDTO, int horizontalAlignment);
}
