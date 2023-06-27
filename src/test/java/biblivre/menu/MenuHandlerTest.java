package biblivre.menu;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.controllers.Controller;
import biblivre.login.MenuProvider;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MenuHandlerTest extends AbstractContainerDatabaseTest {
    @Autowired private MenuProvider menuProvider;

    //    @Test
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
