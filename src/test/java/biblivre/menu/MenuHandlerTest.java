package biblivre.menu;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.controllers.Controller;
import biblivre.login.MenuProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
public class MenuHandlerTest extends AbstractContainerDatabaseTest {
    @Autowired private MenuProvider menuProvider;

    @Test
    public void testAllMenusHaveMethods() {

        menuProvider
                .getAllowedModules(item -> true)
                .forEach(
                        (module, actions) -> {
                            for (String action : actions) {
                                try {
                                    Assertions.assertNotNull(
                                            Controller.getMethod(action, Handler.class));
                                } catch (NoSuchMethodException e) {
                                    Assertions.fail();
                                }
                            }
                        });
    }
}
