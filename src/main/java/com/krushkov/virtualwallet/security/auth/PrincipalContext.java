package com.krushkov.virtualwallet.security.auth;

import com.krushkov.virtualwallet.exceptions.AuthenticationFailureException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.enums.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class PrincipalContext {

    private PrincipalContext() {}

    public static Long getId() {
        return getPrincipal().getId();
    }

    public static String getUsername() {
        return getPrincipal().getUsername();
    }

    public static boolean isBlocked() {
        return getPrincipal().isBlocked();
    }

    public static boolean isAdmin() {
        return getPrincipal().getRole() == RoleType.ADMIN;
    }

    public static UserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth.getPrincipal() == null
                || "anonymousUser".equals(auth.getPrincipal())
                || !(auth.getPrincipal() instanceof UserPrincipal principal)
        ) {
            throw new AuthenticationFailureException(ConstantMessages.AUTHENTICATION_MISSING_ERROR);
        }

        return principal;
    }
}
