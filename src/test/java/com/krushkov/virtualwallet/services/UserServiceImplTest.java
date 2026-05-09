package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityDuplicateException;
import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.mappers.UserMapper;
import com.krushkov.virtualwallet.models.Role;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.dtos.requests.user.UserUpdateRequest;
import com.krushkov.virtualwallet.models.enums.RoleType;
import com.krushkov.virtualwallet.repositories.RoleRepository;
import com.krushkov.virtualwallet.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asAdmin;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.role;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final UserServiceImpl service = new UserServiceImpl(
            userRepository,
            roleRepository,
            passwordEncoder,
            userMapper
    );

    @BeforeEach
    void setUp() {
        asUser(10L);
    }

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void getById_returnsExistingUser() {
        User user = user(10L);
        when(userRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(user));

        assertThat(service.getById(10L)).isSameAs(user);
    }

    @Test
    void create_encodesPasswordAssignsUserRoleAndSaves() {
        User user = user(12L);
        user.setPassword("raw");
        Role userRole = role(RoleType.USER);
        when(userRepository.findByUsernameAndIsDeletedFalse(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumberAndIsDeletedFalse(user.getPhoneNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenReturn(user);

        User result = service.create(user);

        assertThat(result).isSameAs(user);
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRole()).isSameAs(userRole);
        verify(userRepository).save(user);
    }

    @Test
    void create_throwsWhenUsernameIsTaken() {
        User user = user(12L);
        User existing = user(13L);
        when(userRepository.findByUsernameAndIsDeletedFalse(user.getUsername())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(user))
                .isInstanceOf(EntityDuplicateException.class);
    }

    @Test
    void create_throwsWhenDefaultRoleIsMissing() {
        User user = user(12L);
        when(userRepository.findByUsernameAndIsDeletedFalse(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumberAndIsDeletedFalse(user.getPhoneNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded");
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(user))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_validatesUniqueFieldsEncodesPasswordMapsAndSavesPrincipal() {
        User target = user(10L);
        UserUpdateRequest request = new UserUpdateRequest(
                "newname",
                "new-password",
                "First",
                "Last",
                "new@example.com",
                "0888888888",
                "photo-url"
        );
        when(userRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(target));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded");
        when(userRepository.save(target)).thenReturn(target);

        User result = service.update(request);

        assertThat(result).isSameAs(target);
        assertThat(target.getPassword()).isEqualTo("encoded");
        verify(userMapper).update(target, request);
        verify(userRepository).save(target);
    }

    @Test
    void block_setsBlockedForAdmin() {
        clear();
        asAdmin(1L);
        User target = user(10L);
        when(userRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(target));

        service.block(10L);

        assertThat(target.isBlocked()).isTrue();
    }

    @Test
    void block_throwsWhenUserAlreadyBlocked() {
        clear();
        asAdmin(1L);
        User target = user(10L);
        target.setBlocked(true);
        when(userRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(target));

        assertThatThrownBy(() -> service.block(10L))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void unblock_setsUnblockedForAdmin() {
        clear();
        asAdmin(1L);
        User target = user(10L);
        target.setBlocked(true);
        when(userRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(target));

        service.unblock(10L);

        assertThat(target.isBlocked()).isFalse();
    }
}
