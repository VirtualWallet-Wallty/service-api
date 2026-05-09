package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.mappers.UserMapper;
import com.krushkov.virtualwallet.helpers.validations.UserValidations;
import com.krushkov.virtualwallet.models.Role;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.dtos.requests.user.UserFilterOptions;
import com.krushkov.virtualwallet.models.dtos.requests.user.UserUpdateRequest;
import com.krushkov.virtualwallet.models.enums.RoleType;
import com.krushkov.virtualwallet.repositories.RoleRepository;
import com.krushkov.virtualwallet.repositories.UserRepository;
import com.krushkov.virtualwallet.repositories.specifications.UserSpecifications;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<User> search(UserFilterOptions filters, Pageable pageable) {
        return userRepository.findAll(UserSpecifications.withFilters(filters), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long targetUserId) {
        return userRepository.findByIdAndIsDeletedFalse(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", targetUserId));
    }

    @Override
    @Transactional
    public User create(User user) {
        UserValidations.validateUsernameNotTaken(userRepository, user.getUsername(), null);
        UserValidations.validateEmailNotTaken(userRepository, user.getEmail(), null);
        UserValidations.validatePhoneNumberNotTaken(userRepository, user.getPhoneNumber(), null);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role", "name", "USER"));

        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(UserUpdateRequest request) {
        Long principalId = PrincipalContext.getId();
        User targetUser = getById(principalId);

        UserValidations.validateUsernameNotTaken(userRepository, request.username(), principalId);
        UserValidations.validateEmailNotTaken(userRepository, request.email(), principalId);
        UserValidations.validatePhoneNumberNotTaken(userRepository, request.phoneNumber(), principalId);

        if (request.password() != null) {
            targetUser.setPassword(passwordEncoder.encode(request.password()));
        }

        userMapper.update(targetUser, request);

        return userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public void block(Long targetUserId) {
        UserValidations.validateIsAdmin();
        User targetUser = getById(targetUserId);
        if (targetUser.isBlocked()) {
            throw new InvalidOperationException(String.format(
                    ConstantMessages.USER_ALREADY_BLOCKED, targetUser.getUsername()
            ));
        }

        targetUser.setBlocked(true);
    }

    @Override
    @Transactional
    public void unblock(Long targetUserId) {
        UserValidations.validateIsAdmin();
        User targetUser = getById(targetUserId);
        if (!targetUser.isBlocked()) {
            throw new InvalidOperationException(String.format(
                    ConstantMessages.USER_NOT_BLOCKED, targetUser.getUsername()
            ));
        }

        targetUser.setBlocked(false);
    }
}
