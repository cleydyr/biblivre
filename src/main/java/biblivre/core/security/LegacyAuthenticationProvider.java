package biblivre.core.security;

import biblivre.core.SchemaThreadLocal;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class LegacyAuthenticationProvider implements AuthenticationProvider {
    private LoginBO loginBO;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();

        if (username == null || username.isEmpty()) {
            throw new BadCredentialsException("Username is empty");
        }

        String password = authentication.getCredentials().toString();

        LoginDTO login = loginBO.login(username, password);

        if (login == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(
                login, password, List.of(new SimpleGrantedAuthority(SchemaThreadLocal.get())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    @Autowired
    public void setLoginBO(LoginBO loginBO) {
        this.loginBO = loginBO;
    }
}
