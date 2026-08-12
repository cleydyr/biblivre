package biblivre.cataloging.search.intelligent;

import static org.assertj.core.api.Assertions.assertThat;

import biblivre.update.UpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

class ConditionalOnIntelligentSearchEnabledUpdateTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withUserConfiguration(IntelligentSearchUpdates.class);

    @Test
    void registersIntelligentSearchUpdatesWhenEnabled() {
        contextRunner
                .withPropertyValues("biblivre.search.intelligent.enabled=true")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context.getBeansOfType(UpdateService.class))
                                    .hasSize(4)
                                    .containsKeys(
                                            "biblivre.update.v6_0_0$9_0_0$alpha.Update",
                                            "biblivre.update.v6_0_0$9_0_1$alpha.Update",
                                            "biblivre.update.v6_0_0$9_0_2$alpha.Update",
                                            "biblivre.update.v6_0_0$9_0_4$alpha.Update");
                        });
    }

    @Test
    void skipsIntelligentSearchUpdatesWhenDisabled() {
        contextRunner
                .withPropertyValues("biblivre.search.intelligent.enabled=false")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context.getBeansOfType(UpdateService.class)).isEmpty();
                        });
    }

    @Test
    void skipsIntelligentSearchUpdatesWhenPropertyMissing() {
        contextRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBeansOfType(UpdateService.class)).isEmpty();
                });
    }

    @Configuration
    @ComponentScan(
            basePackages = {
                "biblivre.update.v6_0_0$9_0_0$alpha",
                "biblivre.update.v6_0_0$9_0_1$alpha",
                "biblivre.update.v6_0_0$9_0_2$alpha",
                "biblivre.update.v6_0_0$9_0_4$alpha"
            },
            useDefaultFilters = false,
            includeFilters =
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = UpdateService.class),
            nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
    static class IntelligentSearchUpdates {}
}
