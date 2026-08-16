package com.acme.salarymanagement.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.auth.dto.AuthResponse;
import com.acme.salarymanagement.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // Purpose: verifies successful login creates an authenticated session response.
    @Test
    void loginStoresAuthenticatedUserInSession() {
        // Arrange: AuthenticationManager accepts the submitted username/password.
        LoginRequest loginRequest = new LoginRequest("hr@acme.com", "admin123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("hr@acme.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(request.getSession(true)).thenReturn(session);

        // Act: login through the service.
        AuthResponse response = authService.login(loginRequest, request);

        // Assert: response contains authenticated HR user details.
        assertThat(response.username()).isEqualTo("hr@acme.com");
        assertThat(response.authenticated()).isTrue();

        // Assert: Spring Security context is stored in memory and in the HTTP session.
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        verify(session).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }

    // Purpose: verifies invalid login is returned as a clear 401 error.
    @Test
    void loginThrowsUnauthorizedWhenCredentialsAreInvalid() {
        // Arrange: AuthenticationManager rejects the submitted password.
        LoginRequest loginRequest = new LoginRequest("hr@acme.com", "wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act + Assert: service converts Spring Security failure into a 401 API error.
        assertThatThrownBy(() -> authService.login(loginRequest, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(responseStatusException.getReason()).isEqualTo("Invalid username or password");
                });
    }

    // Purpose: verifies logout removes the server-side session authentication.
    @Test
    void logoutInvalidatesExistingSessionAndClearsSecurityContext() {
        // Arrange: current request has an authenticated security context and session.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(request.getSession(false)).thenReturn(session);

        // Act: logout through the service.
        authService.logout(request);

        // Assert: session is invalidated and authentication is removed from SecurityContextHolder.
        verify(session).invalidate();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
