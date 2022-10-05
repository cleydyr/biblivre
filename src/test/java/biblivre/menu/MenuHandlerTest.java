package biblivre.menu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.controllers.Controller;
import biblivre.login.MenuProvider;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MenuHandlerTest extends AbstractContainerDatabaseTest {
    @Autowired private MenuProvider menuProvider;

    @Test
    public void testAllMenusHaveMethods() throws IOException, URISyntaxException {

        menuProvider
                .getAllowedModules(item -> true)
                .forEach(
                        (module, actions) -> {
                            for (String action : actions) {
                                try {
                                    assertNotNull(
                                            Controller.getMethod(
                                                    action, null, biblivre.menu.Handler.class));
                                } catch (NoSuchMethodException e) {
                                    fail();
                                }
                            }
                        });
    }
}
