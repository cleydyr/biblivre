package biblivre.cataloging.search.intelligent;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(
        name = "biblivre.search.intelligent.embedding.provider",
        havingValue = "openai_compatible",
        matchIfMissing = true)
public class OpenAiCompatibleEmbeddingProvider implements EmbeddingProvider {
    private final IntelligentSearchProperties properties;
    private final RestClient restClient;

    public OpenAiCompatibleEmbeddingProvider(
            IntelligentSearchProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        var embedding = properties.getEmbedding();
        var httpClient =
                HttpClient.newBuilder().connectTimeout(embedding.getConnectTimeout()).build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(embedding.getReadTimeout());
        var builder =
                restClientBuilder.baseUrl(embedding.getBaseUrl()).requestFactory(requestFactory);
        String apiKey = embedding.getApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + apiKey);
        }
        this.restClient = builder.build();
    }

    @Override
    public String modelId() {
        return properties.getEmbedding().getModel();
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
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }

        EmbeddingResponse response =
                restClient
                        .post()
                        .uri("/embeddings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new EmbeddingRequest(modelId(), texts))
                        .retrieve()
                        .body(EmbeddingResponse.class);

        if (response == null || response.data() == null) {
            throw new IllegalStateException("Empty embedding response from provider");
        }

        return toOrderedVectors(response.data(), texts.size(), dimensions());
    }

    /**
     * Rebuilds the batch in input order using each item's {@code index}, and validates that every
     * input has exactly one vector of the expected dimension.
     */
    static List<float[]> toOrderedVectors(
            List<EmbeddingData> data, int expectedCount, int expectedDimensions) {
        if (data == null) {
            throw new IllegalStateException("Empty embedding response from provider");
        }

        float[][] ordered = new float[expectedCount][];
        boolean[] seen = new boolean[expectedCount];

        for (EmbeddingData item : data) {
            int index = item.index();
            if (index < 0 || index >= expectedCount) {
                throw new IllegalStateException(
                        "Embedding index out of range: "
                                + index
                                + " (batch size "
                                + expectedCount
                                + ")");
            }
            if (seen[index]) {
                throw new IllegalStateException("Duplicate embedding index: " + index);
            }

            float[] vector = toFloatArray(item.embedding());
            if (vector.length != expectedDimensions) {
                throw new IllegalStateException(
                        "Unexpected embedding dimensions at index "
                                + index
                                + ": got "
                                + vector.length
                                + ", expected "
                                + expectedDimensions);
            }

            ordered[index] = vector;
            seen[index] = true;
        }

        List<float[]> vectors = new ArrayList<>(expectedCount);
        for (int i = 0; i < expectedCount; i++) {
            if (!seen[i]) {
                throw new IllegalStateException("Missing embedding for input index: " + i);
            }
            vectors.add(ordered[i]);
        }
        return vectors;
    }

    private static float[] toFloatArray(List<Double> embedding) {
        Objects.requireNonNull(embedding, "embedding");
        float[] values = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            values[i] = embedding.get(i).floatValue();
        }
        return values;
    }

    private record EmbeddingRequest(String model, List<String> input) {}

    private record EmbeddingResponse(List<EmbeddingData> data) {}

    record EmbeddingData(List<Double> embedding, int index) {}
}
