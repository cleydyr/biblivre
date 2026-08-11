package biblivre.cataloging.search.intelligent;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class OpenAiCompatibleEmbeddingProviderTest {

    @Test
    void ordersEmbeddingsByIndex() {
        List<float[]> vectors =
                OpenAiCompatibleEmbeddingProvider.toOrderedVectors(
                        List.of(
                                new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                        List.of(0.3, 0.4), 1),
                                new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                        List.of(0.1, 0.2), 0)),
                        2,
                        2);

        assertEquals(2, vectors.size());
        assertArrayEquals(new float[] {0.1f, 0.2f}, vectors.get(0), 1e-5f);
        assertArrayEquals(new float[] {0.3f, 0.4f}, vectors.get(1), 1e-5f);
    }

    @Test
    void rejectsMissingIndex() {
        assertThrows(
                IllegalStateException.class,
                () ->
                        OpenAiCompatibleEmbeddingProvider.toOrderedVectors(
                                List.of(
                                        new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                                List.of(0.1, 0.2), 0)),
                                2,
                                2));
    }

    @Test
    void rejectsDuplicateIndex() {
        assertThrows(
                IllegalStateException.class,
                () ->
                        OpenAiCompatibleEmbeddingProvider.toOrderedVectors(
                                List.of(
                                        new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                                List.of(0.1, 0.2), 0),
                                        new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                                List.of(0.3, 0.4), 0)),
                                2,
                                2));
    }

    @Test
    void rejectsWrongDimensions() {
        assertThrows(
                IllegalStateException.class,
                () ->
                        OpenAiCompatibleEmbeddingProvider.toOrderedVectors(
                                List.of(
                                        new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                                List.of(0.1, 0.2, 0.3), 0)),
                                1,
                                2));
    }

    @Test
    void rejectsOutOfRangeIndex() {
        assertThrows(
                IllegalStateException.class,
                () ->
                        OpenAiCompatibleEmbeddingProvider.toOrderedVectors(
                                List.of(
                                        new OpenAiCompatibleEmbeddingProvider.EmbeddingData(
                                                List.of(0.1, 0.2), 5)),
                                1,
                                2));
    }
}
