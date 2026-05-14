package com.jobportal.user_service.config;

import com.jobportal.user_service.services.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    private final String[] publicUrl = {
            "/",
            "/login",
            "/global-search/**",
            "/register",
            "/register/**",
            "/webjars/**",
            "/resources/**",
            "/assets/**",
            "/css/**",
            "/summernote/**",
            "/js/**",
            "/*.css",
            "/*.js",
            "/*.js.map",
            "/fonts/**",
            "/favicon.ico",
            "/error",
            "/api/**"};

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(publicUrl).permitAll();
            auth.anyRequest().authenticated();
        });

        // Stateless: user-service only processes login; JWT carries state after that
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, auth) -> {
                            // Clear the JWT cookie on logout
                            Cookie jwtCookie = new Cookie("JWT_TOKEN", "");
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setPath("/");
                            jwtCookie.setMaxAge(0);
                            response.addCookie(jwtCookie);
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Redirect back to the gateway port, not user-service port
                            String forwardedHost = request.getHeader("X-Forwarded-Host");
                            String forwardedProto = request.getHeader("X-Forwarded-Proto");
                            String forwardedPort = request.getHeader("X-Forwarded-Port");
                            String host;
                            if (forwardedHost != null && !forwardedHost.isEmpty()) {
                                host = forwardedHost.split(",")[0].trim();
                                if (host.contains(":")) host = host.substring(0, host.lastIndexOf(":"));
                            } else {
                                host = "localhost";
                            }
                            String proto = (forwardedProto != null && !forwardedProto.isEmpty()) ? forwardedProto.split(",")[0].trim() : "http";
                            String port = (forwardedPort != null && !forwardedPort.isEmpty()) ? forwardedPort.split(",")[0].trim() : "8080";
                            response.sendRedirect(proto + "://" + host + ":" + port + "/");
                        })
                        .permitAll())
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
