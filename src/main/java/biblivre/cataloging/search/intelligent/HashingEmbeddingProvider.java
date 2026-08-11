package biblivre.cataloging.search.intelligent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Deterministic fake embeddings for tests and local pipeline smoke checks. Not suitable for judging
 * semantic search quality.
 */
@Component
@ConditionalOnProperty(
        name = "biblivre.search.intelligent.embedding.provider",
        havingValue = "hashing")
public class HashingEmbeddingProvider implements EmbeddingProvider {
    private final IntelligentSearchProperties properties;

    public HashingEmbeddingProvider(IntelligentSearchProperties properties) {
        this.properties = properties;
    }

    @Override
    public String modelId() {
        return "hashing-" + dimensions();
    }

    @Override
    public int dimensions() {
        return properties.getEmbedding().getDimensions();
    }

    @Override
    public float[] embed(String text) {
        return embedBatch(List.of(text)).getFirst();
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> result = new ArrayList<>(texts.size());
        for (String text : texts) {
            result.add(hashEmbed(text, dimensions()));
        }
        return result;
    }

    static float[] hashEmbed(String text, int dimensions) {
        float[] vector = new float[dimensions];
        if (text == null || text.isBlank()) {
            return vector;
        }

        String[] tokens = text.toLowerCase(Locale.ROOT).split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            int hash = murmurish(token);
            int index = Math.floorMod(hash, dimensions);
            vector[index] += 1.0f;
            vector[Math.floorMod(hash >>> 8, dimensions)] += 0.5f;
        }

        normalize(vector);
        return vector;
    }

    private static int murmurish(String token) {
        byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
        int h = 0x811c9dc5;
        for (byte b : bytes) {
            h ^= b & 0xff;
            h *= 0x01000193;
        }
        return h;
    }

    private static void normalize(float[] vector) {
        double sumSquares = 0;
        for (float value : vector) {
            sumSquares += value * value;
        }
        if (sumSquares == 0) {
            return;
        }
        float norm = (float) Math.sqrt(sumSquares);
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= norm;
        }
    }
}
