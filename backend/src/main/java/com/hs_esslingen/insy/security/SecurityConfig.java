package com.hs_esslingen.insy.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Profile("production")
public class SecurityConfig {

    @Value("${required.keycloak.role}")
    private String requiredKeycloakRole;

    @Value("${allowed.origin}")
    private String insyFrontendUrl;

    @Value("${keycloak.frontend.client-id}")
    private String clientId;

    @Value("${keycloak.besy.client-id}")
    private String besyClientId;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${besy.username}")
    private String besyUsername;

    @Value("${besy.password}")
    private String besyPassword;

    private static final String BESY_ROLE = "BESY_ACCESS";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            Converter<Jwt, AbstractAuthenticationToken> authenticationConverter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> {
                    authorize
                            // Allow BeSy to create orders
                            .requestMatchers(HttpMethod.POST, "/orders/**")
                            .hasAnyAuthority(requiredKeycloakRole, BESY_ROLE)
                            .anyRequest().hasAuthority(requiredKeycloakRole);
                })
                .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic authentication
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtDecoder -> jwtDecoder.jwtAuthenticationConverter(authenticationConverter)));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(insyFrontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders
                .fromIssuerLocation(issuerUri);
    }

    // Hardcoded user to allow BeSy to access the API
    // Uses basic authentication
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username(besyUsername)
                .password(encoder.encode(besyPassword))
                .authorities(BESY_ROLE)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // Password encoder bean for encoding passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Converter to extract roles from JWT claims
    interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    }

    @Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            Optional<Map<String, Object>> realmAccess = Optional
                    .ofNullable((Map<String, Object>) claims.get("resource_access"))
                    .flatMap(map -> Optional.ofNullable((Map<String, Object>) map.get(clientId)));
            Optional<List<String>> roles = realmAccess
                    .flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));

            List<String> presentRoles = roles.orElse(new java.util.ArrayList<>());
            if (besyClientId.equals(claims.get("client_id")))
                presentRoles.add(BESY_ROLE);

            return presentRoles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        };
    }

    @Bean
    JwtAuthenticationConverter authenticationConverter(AuthoritiesConverter authoritiesConverter) {
        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> authoritiesConverter.convert(jwt.getClaims()));
        return authenticationConverter;
    }
}
