    package com.cmms.lite.security.config;

    import com.cmms.lite.security.filter.JwtFilter;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
    import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import java.util.Arrays;
    import java.util.List;

    @RequiredArgsConstructor
    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
    public class SecurityConfig {

        private final JwtFilter jwtFilter;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        public static PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public RoleHierarchy roleHierarchy() {
            String hierarchy = "ROLE_ADMIN > ROLE_TECHNICAN \n ROLE_TECHNICAN > ROLE_SUBCONTRACTOR";
            return RoleHierarchyImpl.fromHierarchy(hierarchy);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .headers(headers -> headers
                            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(customAuthenticationEntryPoint)
                    )
                    .authorizeHttpRequests((authorize) -> authorize
                            .requestMatchers(
                                    "/auth/**",
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/h2-console/**",
                                    "/api/breakdowns/performance/**",
                                    "/api/breakdowns/report",
                                    "/api/breakdown-types",
                                    "/api/machines/list",
                                    "/api/dashboard"
                            ).permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of("http://localhost:[*]"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
    }