package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.dtos.responses.auth.UserPrincipalResponse;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.security.auth.UserPrincipal;
import com.krushkov.virtualwallet.security.jwt.JwtCookieUtil;
import com.krushkov.virtualwallet.security.jwt.JwtUtil;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.dtos.requests.auth.LoginRequest;
import com.krushkov.virtualwallet.services.contracts.AuthService;
import com.krushkov.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserService userService;

    @Override
    @Transactional
    public void register(User user) {
        userService.create(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPrincipalResponse login(LoginRequest request, HttpServletResponse response) {
        if (request.identifier() == null || request.identifier().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new InvalidOperationException(ConstantMessages.IDENTIFIER_PASSWORD_MISSING_ERROR);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.identifier(),
                            request.password()
                    )
            );

            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

            String jwt = jwtUtil.generateToken(userDetails);
            jwtCookieUtil.addTokenCookie(response, jwt);

            return new UserPrincipalResponse(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getRole().name(),
                    userDetails.isBlocked()
            );
        } catch (BadCredentialsException e) {
            throw new InvalidOperationException(ConstantMessages.IDENTIFIER_PASSWORD_WRONG_ERROR);
        }
    }

    @Override
    @Transactional
    public void logout(HttpServletResponse response) {
        jwtCookieUtil.clearTokenCookie(response);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserPrincipalResponse getMe() {
        UserPrincipal userPrincipal = PrincipalContext.getPrincipal();
        return new UserPrincipalResponse(
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getRole().name(),
                userPrincipal.isBlocked()
        );
    }
}
