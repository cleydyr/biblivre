package biblivre.cataloging.labels;

import biblivre.core.LabelPrintDTO;
import biblivre.core.file.DiskFile;
import java.util.List;

public interface LabelGenerator {
    DiskFile generate(List<LabelDTO> labels, LabelPrintDTO printDTO, int horizontalAlignment);
}
