package biblivre.cataloging.search.intelligent;

import java.util.List;

public interface EmbeddingProvider {
    String modelId();

    int dimensions();

    float[] embed(String text);

    List<float[]> embedBatch(List<String> texts);
}
