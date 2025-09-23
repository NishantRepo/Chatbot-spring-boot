package com.nish.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static final String[] UNAUTHENTICATED_URL = {"/", "/login", "/public", "/logout-success", "/chat", "/css/**", "/js/**"};

    @Value("${app.logout.page}")
    private String logoutPage;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/logout") // disables CSRF for /logout
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(UNAUTHENTICATED_URL).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage(String.format("%s/my-client", OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI)) // redirect to Keycloak login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // endpoint users hit to log out
                        .invalidateHttpSession(true)   // invalidate local Spring session
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                                String idToken = oidcUser.getIdToken().getTokenValue();
                                String issuer = oidcUser.getIssuer().toString();

                                String logoutUrl = issuer + "/protocol/openid-connect/logout?id_token_hint="
                                        + idToken + "&post_logout_redirect_uri=" + logoutPage;

                                response.sendRedirect(logoutUrl);
                            } else {
                                response.sendRedirect("/logout-success");
                            }
                        })
                );

        return http.build();
    }
}
