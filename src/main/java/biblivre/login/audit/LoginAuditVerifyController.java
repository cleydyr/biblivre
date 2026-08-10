package biblivre.login.audit;

import biblivre.core.SchemaThreadLocal;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Credential check for the Login Audit sidecar. Does not open a Biblivre session and does not
 * produce a Successful Login.
 */
@RestController
@RequestMapping("/api/v2/login-audit")
public class LoginAuditVerifyController {

    private final LoginBO loginBO;
    private final String apiKey;

    public LoginAuditVerifyController(
            LoginBO loginBO, @Value("${login-audit.api-key:}") String apiKey) {
        this.loginBO = loginBO;
        this.apiKey = apiKey;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(
            @RequestHeader(value = "X-Login-Audit-Key", required = false) String key,
            @RequestBody VerifyRequest request) {

        if (apiKey == null || apiKey.isBlank() || key == null || !apiKey.equals(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new VerifyResponse(false, 0, null));
        }

        if (request.schema() == null || request.username() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(new VerifyResponse(false, 0, null));
        }

        LoginDTO login =
                SchemaThreadLocal.withSchema(
                        request.schema(),
                        () -> loginBO.login(request.username(), request.password()));

        if (login == null) {
            return ResponseEntity.ok(new VerifyResponse(false, 0, null));
        }

        return ResponseEntity.ok(new VerifyResponse(true, login.getId(), login.getLogin()));
    }

    public record VerifyRequest(String schema, String username, String password) {}

    public record VerifyResponse(boolean valid, int loginId, String username) {}
}
