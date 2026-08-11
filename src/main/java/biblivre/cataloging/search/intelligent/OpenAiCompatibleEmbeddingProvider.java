package biblivre.cataloging.search.intelligent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.http.MediaType;
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
        var settings =
                HttpClientSettings.defaults()
                        .withConnectTimeout(embedding.getConnectTimeout())
                        .withReadTimeout(embedding.getReadTimeout());
        var requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings);
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

        List<float[]> vectors = new ArrayList<>(texts.size());
        for (EmbeddingData item : response.data()) {
            vectors.add(toFloatArray(item.embedding()));
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

    private record EmbeddingData(List<Double> embedding, int index) {}
}
