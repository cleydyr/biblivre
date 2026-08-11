package biblivre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.reports.generated.api.CirculationApiController;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

class OpenApiBasePathTest {

    @Test
    void generatedControllersDefaultBasePathToApiV2() {
        RequestMapping requestMapping =
                CirculationApiController.class.getAnnotation(RequestMapping.class);

        assertEquals(
                "${openapi.biblivreREST.base-path:/api/v2}",
                requestMapping.value()[0],
                "Generated OpenAPI controllers must default to /api/v2 so SPA clients keep"
                        + " working if application.yml omits openapi.biblivreREST.base-path");
    }
}
