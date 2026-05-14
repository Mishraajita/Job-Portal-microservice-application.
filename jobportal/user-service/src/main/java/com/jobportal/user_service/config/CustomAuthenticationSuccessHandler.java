package com.jobportal.user_service.config;

import com.jobportal.user_service.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public CustomAuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("The username " + username + " is logged in.");

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean hasJobSeekerRole = roles.contains("Job Seeker");
        boolean hasRecruiterRole = roles.contains("Recruiter");

        if (hasRecruiterRole || hasJobSeekerRole) {
            // Generate JWT
            String token = jwtTokenProvider.generateToken(username, roles);

            // Set as HttpOnly, SameSite=Lax cookie (not accessible from JS)
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400); // 1 day in seconds
            response.addCookie(jwtCookie);

            // Build redirect URL using X-Forwarded-Host so it matches the browser's
            // original host (e.g. localhost or machine-name), avoiding cookie domain mismatch
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
            String redirectUrl = proto + "://" + host + ":" + port + "/dashboard";
            response.sendRedirect(redirectUrl);
        }
    }
}
