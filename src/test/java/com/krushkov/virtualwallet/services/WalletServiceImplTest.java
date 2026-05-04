package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.mappers.WalletMapper;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.repositories.UserRepository;
import com.krushkov.virtualwallet.repositories.WalletRepository;
import com.krushkov.virtualwallet.services.contracts.CurrencyService;
import com.krushkov.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asAdmin;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asBlockedUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.wallet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class WalletServiceImplTest {

    private final WalletRepository walletRepository = mock(WalletRepository.class);
    private final WalletMapper walletMapper = mock(WalletMapper.class);
    private final CurrencyService currencyService = mock(CurrencyService.class);
    private final UserService userService = mock(UserService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final WalletServiceImpl service = new WalletServiceImpl(
            walletRepository,
            walletMapper,
            currencyService,
            userService,
            userRepository
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
    void debit_subtractsAmountFromLockedPrincipalWallet() {
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), new BigDecimal("25.00"), false);
        when(walletRepository.findWithLockByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(wallet));

        service.debit(5L, new BigDecimal("7.50"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("17.50");
    }

    @Test
    void getById_returnsAnyWalletForAdmin() {
        clear();
        asAdmin(1L);
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, false);
        when(walletRepository.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(wallet));

        assertThat(service.getById(5L)).isSameAs(wallet);
        verify(walletRepository).findByIdAndIsDeletedFalse(5L);
    }

    @Test
    void getById_returnsOnlyOwnedWalletForRegularUser() {
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, false);
        when(walletRepository.findByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(wallet));

        assertThat(service.getById(5L)).isSameAs(wallet);
        verify(walletRepository).findByIdAndUserIdAndIsDeletedFalse(5L, 10L);
    }

    @Test
    void getDefault_returnsUsersDefaultWallet() {
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, true);
        when(walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(10L)).thenReturn(Optional.of(wallet));

        assertThat(service.getDefault(10L)).isSameAs(wallet);
    }

    @Test
    void getMyDefault_returnsPrincipalDefaultWallet() {
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, true);
        when(walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(10L)).thenReturn(Optional.of(wallet));

        assertThat(service.getMyDefault()).isSameAs(wallet);
    }

    @Test
    void debit_throwsWhenBalanceIsInsufficient() {
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), new BigDecimal("5.00"), false);
        when(walletRepository.findWithLockByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> service.debit(5L, new BigDecimal("7.50")))
                .isInstanceOf(InvalidOperationException.class);

        assertThat(wallet.getBalance()).isEqualByComparingTo("5.00");
    }

    @Test
    void creditMyWallet_addsAmountToLockedPrincipalWallet() {
        Wallet wallet = wallet(5L, user(10L), currency("EUR", 2), new BigDecimal("25.00"), false);
        when(walletRepository.findWithLockByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(wallet));

        service.creditMyWallet(5L, new BigDecimal("4.25"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("29.25");
    }

    @Test
    void creditRecipientWallet_addsAmountToRecipientWalletWhenRecipientIsAllowed() {
        User recipient = user(20L);
        Wallet wallet = wallet(5L, recipient, currency("EUR", 2), new BigDecimal("25.00"), false);
        when(userRepository.findByIdAndIsDeletedFalse(20L)).thenReturn(Optional.of(recipient));
        when(walletRepository.findWithLockByIdAndUserIdAndIsDeletedFalse(5L, 20L)).thenReturn(Optional.of(wallet));

        service.creditRecipientWallet(5L, 20L, new BigDecimal("3.25"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("28.25");
    }

    @Test
    void creditMyWallet_throwsForBlockedPrincipalBeforeLockingWallet() {
        clear();
        asBlockedUser(10L);

        assertThatThrownBy(() -> service.creditMyWallet(5L, new BigDecimal("4.25")))
                .isInstanceOf(InvalidOperationException.class);

        verify(walletRepository, never()).findWithLockByIdAndUserIdAndIsDeletedFalse(5L, 10L);
    }

    @Test
    void create_marksFirstWalletAsDefaultAndAssignsUserAndCurrency() {
        User owner = user(10L);
        Currency currency = currency("EUR", 2);
        Wallet newWallet = new Wallet();
        newWallet.setName("Main");

        when(walletRepository.countByUserIdAndIsDeletedFalse(10L)).thenReturn(0);
        when(walletRepository.existsByNameAndUserIdAndIsDeletedFalse("Main", 10L)).thenReturn(false);
        when(userService.getById(10L)).thenReturn(owner);
        when(currencyService.getByCode("EUR")).thenReturn(currency);
        when(walletRepository.save(newWallet)).thenReturn(newWallet);

        Wallet result = service.create(newWallet, "EUR");

        assertThat(result).isSameAs(newWallet);
        assertThat(newWallet.isDefault()).isTrue();
        assertThat(newWallet.getUser()).isSameAs(owner);
        assertThat(newWallet.getCurrency()).isSameAs(currency);
        verify(walletRepository).save(newWallet);
    }

    @Test
    void delete_marksNonDefaultEmptyWalletDeletedWhenUserKeepsMinimumWalletCount() {
        Wallet target = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, false);
        when(walletRepository.findByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(target));
        when(walletRepository.countByUserIdAndIsDeletedFalse(10L)).thenReturn(2);

        service.delete(5L);

        assertThat(target.isDeleted()).isTrue();
    }

    @Test
    void setDefault_clearsCurrentDefaultAndMarksTargetDefault() {
        Wallet currentDefault = wallet(4L, user(10L), currency("USD", 2), BigDecimal.ZERO, true);
        Wallet target = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, false);
        when(walletRepository.findByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(target));
        when(walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(10L)).thenReturn(Optional.of(currentDefault));

        service.setDefault(5L);

        assertThat(currentDefault.isDefault()).isFalse();
        assertThat(target.isDefault()).isTrue();
    }

    @Test
    void setDefault_marksTargetDefaultWhenNoCurrentDefaultExists() {
        Wallet target = wallet(5L, user(10L), currency("EUR", 2), BigDecimal.ZERO, false);
        when(walletRepository.findByIdAndUserIdAndIsDeletedFalse(5L, 10L)).thenReturn(Optional.of(target));
        when(walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(10L)).thenReturn(Optional.empty());

        service.setDefault(5L);

        assertThat(target.isDefault()).isTrue();
    }

    @Test
    void getAllByUserId_isAdminOnlyAndReturnsUserWallets() {
        clear();
        asAdmin(1L);
        when(walletRepository.findAllByUserIdAndIsDeletedFalse(10L)).thenReturn(java.util.List.of());

        assertThat(service.getAllByUserId(10L)).isEmpty();
        verify(walletRepository).findAllByUserIdAndIsDeletedFalse(10L);
    }

    @Test
    void getMyAll_returnsPrincipalWallets() {
        when(walletRepository.findAllByUserIdAndIsDeletedFalse(10L)).thenReturn(java.util.List.of());

        assertThat(service.getMyAll()).isEmpty();
        verify(walletRepository).findAllByUserIdAndIsDeletedFalse(10L);
        verifyNoMoreInteractions(walletRepository);
    }
}
