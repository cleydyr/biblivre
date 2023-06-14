package biblivre.menu;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.controllers.Controller;
import biblivre.login.MenuProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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
                                            Controller.getMethod(action, null, Handler.class));
                                } catch (NoSuchMethodException e) {
                                    Assertions.fail();
                                }
                            }
                        });
    }
}
