package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.dtos.requests.auth.LoginRequest;
import com.krushkov.virtualwallet.models.dtos.responses.auth.UserPrincipalResponse;
import com.krushkov.virtualwallet.security.auth.UserPrincipal;
import com.krushkov.virtualwallet.security.jwt.JwtCookieUtil;
import com.krushkov.virtualwallet.security.jwt.JwtUtil;
import com.krushkov.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asBlockedUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final JwtCookieUtil jwtCookieUtil = mock(JwtCookieUtil.class);
    private final UserService userService = mock(UserService.class);
    private final AuthServiceImpl service = new AuthServiceImpl(
            authenticationManager,
            jwtUtil,
            jwtCookieUtil,
            userService
    );

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void register_delegatesToUserServiceCreate() {
        User user = user(10L);

        service.register(user);

        verify(userService).create(user);
    }

    @Test
    void login_authenticatesGeneratesJwtCookieAndReturnsPrincipalResponse() {
        User user = user(10L);
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = mock(Authentication.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(principal)).thenReturn("jwt-token");

        UserPrincipalResponse result = service.login(new LoginRequest("user10", "password"), response);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.username()).isEqualTo("user10");
        assertThat(result.role()).isEqualTo("USER");
        assertThat(result.isBlocked()).isFalse();
        verify(jwtCookieUtil).addTokenCookie(response, "jwt-token");
    }

    @Test
    void login_rejectsMissingIdentifierOrPassword() {
        assertThatThrownBy(() -> service.login(new LoginRequest(" ", "password"), mock(HttpServletResponse.class)))
                .isInstanceOf(InvalidOperationException.class);

        assertThatThrownBy(() -> service.login(new LoginRequest("user", null), mock(HttpServletResponse.class)))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void login_mapsBadCredentialsToInvalidOperation() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> service.login(new LoginRequest("user", "wrong"), mock(HttpServletResponse.class)))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void logout_clearsCookieAndSecurityContext() {
        asUser(10L);
        HttpServletResponse response = mock(HttpServletResponse.class);

        service.logout(response);

        verify(jwtCookieUtil).clearTokenCookie(response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void getMe_returnsCurrentPrincipal() {
        asBlockedUser(10L);

        UserPrincipalResponse result = service.getMe();

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.username()).isEqualTo("user10");
        assertThat(result.role()).isEqualTo("USER");
        assertThat(result.isBlocked()).isTrue();
    }
}
