package biblivre.core.security;

import biblivre.login.LoginBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired private LoginBO loginBO;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // Allow access to static resources
        http.authorizeHttpRequests(
                        (requests) ->
                                requests.requestMatchers(
                                                PathRequest.toStaticResources().atCommonLocations())
                                        .permitAll()
                                        .requestMatchers("/api/v2/reports/**")
                                        .authenticated()
                                        .anyRequest()
                                        .permitAll())
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
