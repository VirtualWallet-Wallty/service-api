package com.krushkov.virtualwallet.helpers.validations;

import com.krushkov.virtualwallet.exceptions.EntityDuplicateException;
import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.enums.RoleType;
import com.krushkov.virtualwallet.repositories.UserRepository;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;

public final class UserValidations {

    private UserValidations() {
    }

    public static void validateUsernameNotTaken(UserRepository repository, String newUsername, Long targetUserId) {
        if (newUsername == null) {
            return;
        }

        repository.findByUsernameAndIsDeletedFalse(newUsername)
                .filter(u -> !u.getId().equals(targetUserId))
                .ifPresent(u -> {
                    throw new EntityDuplicateException("User", "username", newUsername);
                });
    }

    public static void validateEmailNotTaken(UserRepository repository, String newEmail, Long targetUserId) {
        if (newEmail == null) {
            return;
        }

        repository.findByEmailAndIsDeletedFalse(newEmail)
                .filter(u -> !u.getId().equals(targetUserId))
                .ifPresent(u -> {
                    throw new EntityDuplicateException("User", "email", newEmail);
                });
    }

    public static void validatePhoneNumberNotTaken(
            UserRepository repository,
            String newPhoneNumber,
            Long targetUserId
    ) {
        if (newPhoneNumber == null) {
            return;
        }

        repository.findByPhoneNumberAndIsDeletedFalse(newPhoneNumber)
                .filter(u -> !u.getId().equals(targetUserId))
                .ifPresent(u -> {
                    throw new EntityDuplicateException("User", "phone number", newPhoneNumber);
                });
    }

    public static void validateNotBlocked() {
        if (PrincipalContext.isBlocked()) {
            throw new InvalidOperationException(ConstantMessages.USER_BLOCKED_ERROR);
        }
    }

    public static void validateRecipientNotBlocked(UserRepository userRepository, Long userId) {
        if (getUser(userRepository, userId).isBlocked()) {
            throw new InvalidOperationException(ConstantMessages.RECIPIENT_BLOCKED_ERROR);
        }
    }

    public static void validateRecipientNotAdmin(UserRepository userRepository, Long userId) {
        if (getUser(userRepository, userId).getRole().getName().equals(RoleType.ADMIN)) {
            throw new InvalidOperationException(ConstantMessages.RECIPIENT_NOT_ADMIN_ERROR);
        }
    }

    public static void validateIsAdmin() {
        if (!PrincipalContext.isAdmin()) {
            throw new InvalidOperationException(ConstantMessages.ADMIN_ONLY_ERROR);
        }
    }

    public static void validateIsNotAdmin() {
        if (PrincipalContext.isAdmin()) {
            throw new InvalidOperationException(ConstantMessages.USER_ONLY_ERROR);
        }
    }

    private static User getUser(UserRepository userRepository, Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }
}
