package biblivre.cataloging.search.intelligent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

/**
 * Smoke for the production-default embedding provider wiring.
 *
 * <p>{@code application-test.yml} selects {@code hashing}, so full {@code @SpringBootTest} suites
 * never load {@link OpenAiCompatibleEmbeddingProvider}. This runner uses the default {@code
 * openai_compatible} path and fails if {@link RestClient.Builder} is missing (e.g. Boot 4.1+
 * without {@code spring-boot-starter-restclient} on the main classpath).
 */
class OpenAiCompatibleEmbeddingProviderContextTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class))
                    .withBean(IntelligentSearchProperties.class)
                    .withBean(OpenAiCompatibleEmbeddingProvider.class)
                    .withPropertyValues(
                            "biblivre.search.intelligent.embedding.provider=openai_compatible",
                            "biblivre.search.intelligent.embedding.base-url=http://localhost:11434/v1",
                            "biblivre.search.intelligent.embedding.api-key=test",
                            "biblivre.search.intelligent.embedding.model=bge-m3",
                            "biblivre.search.intelligent.embedding.dimensions=1024");

    @Test
    void wiresDefaultEmbeddingProviderWhenRestClientBuilderIsAvailable() {
        contextRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(RestClient.Builder.class);
                    assertThat(context).hasSingleBean(OpenAiCompatibleEmbeddingProvider.class);
                    assertThat(context).hasSingleBean(EmbeddingProvider.class);
                });
    }

    @Test
    void failsWithoutRestClientBuilder() {
        new ApplicationContextRunner()
                .withBean(IntelligentSearchProperties.class)
                .withBean(OpenAiCompatibleEmbeddingProvider.class)
                .withPropertyValues(
                        "biblivre.search.intelligent.embedding.provider=openai_compatible")
                .run(
                        context ->
                                assertThat(context)
                                        .hasFailed()
                                        .getFailure()
                                        .hasRootCauseInstanceOf(
                                                NoSuchBeanDefinitionException.class));
    }
}
