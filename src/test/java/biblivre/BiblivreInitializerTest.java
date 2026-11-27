package biblivre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import biblivre.core.controllers.SchemaFilter;
import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.test.util.ReflectionTestUtils;

class BiblivreInitializerTest {

    @Test
    void schemaFilterRunsOnRequestForwardAndError() throws Exception {
        BiblivreInitializer initializer = new BiblivreInitializer();
        ReflectionTestUtils.setField(
                initializer, "autowireCapableBeanFactory", mock(AutowireCapableBeanFactory.class));

        FilterRegistrationBean<SchemaFilter> registration = initializer.schemaFilterRegistration();

        assertEquals(
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR),
                registration.determineDispatcherTypes());
    }
}
