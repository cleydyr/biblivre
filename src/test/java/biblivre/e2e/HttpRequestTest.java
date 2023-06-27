package biblivre.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import biblivre.AbstractContainerDatabaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @Testcontainers
public class HttpRequestTest extends AbstractContainerDatabaseTest {

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    //    @Test
    public void testHomePageWithTitle() {
        execute(
                () -> {
                    String forObject =
                            this.restTemplate.getForObject(
                                    "http://localhost:" + port, String.class);

                    assertThat(forObject).contains("Biblivre V");
                    assertThat(forObject).contains("menu_search");
                    assertThat(forObject).contains("search_bibliographic");
                    assertThat(forObject).contains("search_vocabulary");
                    assertThat(forObject).contains("search_authorities");
                });
    }
}
