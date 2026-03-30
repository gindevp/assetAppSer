package com.gindevp.app.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.gindevp.app.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import com.gindevp.app.security.AuthoritiesConstants;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    private final boolean apiPermitAll;

    public SecurityConfiguration(
        JHipsterProperties jHipsterProperties,
        @Value("${app.security.api-permit-all:false}") boolean apiPermitAll
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.apiPermitAll = apiPermitAll;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> {
                // prettier-ignore
                authz
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN);
                if (apiPermitAll) {
                    authz.requestMatchers(mvc.pattern("/api/**")).permitAll();
                } else {
                    String[] qmWritePaths = {
                        "/api/asset-types/**",
                        "/api/asset-groups/**",
                        "/api/asset-lines/**",
                        "/api/asset-items/**",
                        "/api/suppliers/**",
                        "/api/equipment/**",
                        "/api/consumable-stocks/**",
                        "/api/stock-receipts/**",
                        "/api/stock-receipt-lines/**",
                        "/api/stock-issues/**",
                        "/api/stock-issue-lines/**",
                        "/api/equipment-assignments/**",
                        "/api/consumable-assignments/**",
                        "/api/employees/**",
                        "/api/departments/**",
                        "/api/locations/**",
                    };
                    HttpMethod[] writeMethods = {
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                    };
                    for (HttpMethod method : writeMethods) {
                        for (String path : qmWritePaths) {
                            authz
                                .requestMatchers(mvc.pattern(method, path))
                                .hasAnyAuthority(
                                    AuthoritiesConstants.ADMIN,
                                    AuthoritiesConstants.ASSET_MANAGER,
                                    AuthoritiesConstants.GD
                                );
                        }
                    }
                    authz.requestMatchers(mvc.pattern("/api/**")).authenticated();
                }
                authz
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN);
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
