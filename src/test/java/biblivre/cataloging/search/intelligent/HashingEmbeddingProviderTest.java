package biblivre.cataloging.search.intelligent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HashingEmbeddingProviderTest {

    @Test
    void embedsAreDeterministicAndNormalized() {
        float[] first = HashingEmbeddingProvider.hashEmbed("Machado de Assis", 32);
        float[] second = HashingEmbeddingProvider.hashEmbed("Machado de Assis", 32);

        assertEquals(32, first.length);
        double norm = 0;
        for (int i = 0; i < first.length; i++) {
            assertEquals(first[i], second[i], 1e-6);
            norm += first[i] * first[i];
        }
        assertEquals(1.0, Math.sqrt(norm), 1e-5);
    }

    @Test
    void differentTextsProduceDifferentVectors() {
        float[] a = HashingEmbeddingProvider.hashEmbed("poesia brasileira", 64);
        float[] b = HashingEmbeddingProvider.hashEmbed("matematica discreta", 64);

        double dot = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
        }
        assertTrue(dot < 0.99);
    }

    @Test
    void vectorLiteralFormatsAsPgVector() {
        assertEquals("[1.0,0.5,-0.25]", VectorLiteral.toLiteral(new float[] {1f, 0.5f, -0.25f}));
    }
}
