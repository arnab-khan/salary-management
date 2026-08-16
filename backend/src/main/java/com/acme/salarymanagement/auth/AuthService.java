package com.acme.salarymanagement.auth;

import com.acme.salarymanagement.auth.dto.AuthResponse;
import com.acme.salarymanagement.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication;
        try {
            // Validate credentials against the HR user configured in Spring Security.
            // Internally this checks LoginRequest username/password against
            // InMemoryUserDetailsManager created in SecurityConfig.userDetailsService(...).
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Store the authenticated user in Spring Security's context.
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Create an HTTP session and save the security context.
        // The client receives a JSESSIONID cookie and sends it on later requests.
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return me(authentication);
    }

    // Returns the currently authenticated HR user from the active session.
    public AuthResponse me(Authentication authentication) {
        return new AuthResponse(authentication.getName(), authentication.isAuthenticated());
    }

    public void logout(HttpServletRequest request) {
        // Remove the server-side session so the existing JSESSIONID is no longer
        // authenticated.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
