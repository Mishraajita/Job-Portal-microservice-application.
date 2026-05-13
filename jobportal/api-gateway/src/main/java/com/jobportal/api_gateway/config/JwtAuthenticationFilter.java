package com.jobportal.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet filter that validates the JWT_TOKEN cookie on every incoming request.
 * - Public paths are passed through without validation.
 * - Authenticated paths require a valid JWT; missing/expired token → redirect to /login.
 * - Valid JWT: injects X-Auth-User (email) and X-Auth-Roles (comma-separated) headers
 *   into the proxied request so backend services can identify the caller.
 */
@Component
@Order(1)
public class JwtAuthenticationFilter implements Filter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** Paths that do not require authentication */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/",
            "/login",
            "/logout",
            "/register",
            "/register/**",
            "/global-search",
            "/global-search/**",
            "/photos/**",
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/assets/**",
            "/fonts/**",
            "/summernote/**",
            "/favicon.ico",
            "/error",
            "/h2-users/**",
            "/h2-recruiter/**",
            "/h2-jobs/**",
            "/h2-jobseeker/**",
            "/h2-applications/**",
            "/h2-saved-jobs/**",
            "/api/**"
    );

    private final SecretKey secretKey;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        // Pass through public paths without any JWT check
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Locate the JWT cookie
        String token = extractJwtFromCookies(request);

        if (token == null) {
            response.sendRedirect("/login");
            return;
        }

        // Validate token
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            // Token expired or tampered — clear the cookie and redirect to login
            Cookie expired = new Cookie("JWT_TOKEN", "");
            expired.setPath("/");
            expired.setMaxAge(0);
            expired.setHttpOnly(true);
            response.addCookie(expired);
            response.sendRedirect("/login");
            return;
        }

        // Token is valid — inject user info as headers so backend services can read them
        String email = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        String rolesHeader = (roles != null) ? String.join(",", roles) : "";

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.putHeader("X-Auth-User", email);
        mutableRequest.putHeader("X-Auth-Roles", rolesHeader);

        chain.doFilter(mutableRequest, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "JWT_TOKEN".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * HttpServletRequestWrapper that allows adding custom headers.
     * Spring Cloud Gateway WebMVC reads headers from the wrapped request when proxying.
     */
    private static class MutableHttpServletRequest extends HttpServletRequestWrapper {

        private final Map<String, String> customHeaders = new HashMap<>();

        MutableHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        void putHeader(String name, String value) {
            customHeaders.put(name.toLowerCase(), value);
        }

        @Override
        public String getHeader(String name) {
            String custom = customHeaders.get(name.toLowerCase());
            return (custom != null) ? custom : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String custom = customHeaders.get(name.toLowerCase());
            if (custom != null) {
                return Collections.enumeration(List.of(custom));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(customHeaders.keySet());
            return Collections.enumeration(names);
        }
    }
}
