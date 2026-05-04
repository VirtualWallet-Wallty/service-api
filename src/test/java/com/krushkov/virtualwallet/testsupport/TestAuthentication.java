package com.krushkov.virtualwallet.testsupport;

import com.krushkov.virtualwallet.models.Role;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.enums.RoleType;
import com.krushkov.virtualwallet.security.auth.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TestAuthentication {

    private TestAuthentication() {
    }

    public static void asUser(Long id) {
        authenticate(id, RoleType.USER, false);
    }

    public static void asBlockedUser(Long id) {
        authenticate(id, RoleType.USER, true);
    }

    public static void asAdmin(Long id) {
        authenticate(id, RoleType.ADMIN, false);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }

    private static void authenticate(Long id, RoleType roleType, boolean blocked) {
        User user = new User();
        user.setId(id);
        user.setUsername(roleType.name().toLowerCase() + id);
        user.setPassword("password");
        user.setBlocked(blocked);

        Role role = new Role();
        role.setName(roleType);
        user.setRole(role);

        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
