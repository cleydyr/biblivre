package biblivre.cataloging.search.intelligent;

/**
 * Skeleton for an on-process local embedding model. Not wired by default; select with {@code
 * biblivre.search.intelligent.embedding.provider=local} once implemented.
 */
public class LocalEmbeddingProvider implements EmbeddingProvider {
    @Override
    public String modelId() {
        throw new UnsupportedOperationException(
                "Local embedding provider is not implemented yet; use openai_compatible or hashing");
    }

    @Override
    public int dimensions() {
        throw new UnsupportedOperationException(
                "Local embedding provider is not implemented yet; use openai_compatible or hashing");
    }

    @Override
    public float[] embed(String text) {
        throw new UnsupportedOperationException(
                "Local embedding provider is not implemented yet; use openai_compatible or hashing");
    }

    @Override
    public java.util.List<float[]> embedBatch(java.util.List<String> texts) {
        throw new UnsupportedOperationException(
                "Local embedding provider is not implemented yet; use openai_compatible or hashing");
    }
}
