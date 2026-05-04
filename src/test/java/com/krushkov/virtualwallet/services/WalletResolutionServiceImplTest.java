package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.repositories.WalletRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.wallet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class WalletResolutionServiceImplTest {

    private final WalletRepository walletRepository = mock(WalletRepository.class);
    private final WalletResolutionServiceImpl service = new WalletResolutionServiceImpl(walletRepository);

    @Test
    void resolveSenderWallet_usesExplicitSourceWalletWhenProvided() {
        User owner = user(7L);
        Wallet wallet = wallet(11L, owner, currency("EUR", 2), BigDecimal.TEN, false);
        when(walletRepository.findByIdAndUserIdAndIsDeletedFalse(11L, 7L)).thenReturn(Optional.of(wallet));

        Wallet result = service.resolveSenderWallet(7L, "USD", 11L);

        assertThat(result).isSameAs(wallet);
        verify(walletRepository).findByIdAndUserIdAndIsDeletedFalse(11L, 7L);
        verifyNoMoreInteractions(walletRepository);
    }

    @Test
    void resolveSenderWallet_picksDefaultSameCurrencyWalletBeforeLowerId() {
        User owner = user(7L);
        Currency eur = currency("EUR", 2);
        Wallet lowerId = wallet(1L, owner, eur, BigDecimal.TEN, false);
        Wallet defaultWallet = wallet(2L, owner, eur, BigDecimal.TEN, true);
        when(walletRepository.findAllByUserIdAndIsDeletedFalse(7L)).thenReturn(List.of(lowerId, defaultWallet));

        Wallet result = service.resolveSenderWallet(7L, "eur", null);

        assertThat(result).isSameAs(defaultWallet);
    }

    @Test
    void resolveRecipientWallet_fallsBackToDefaultWhenCurrencyIsMissing() {
        User recipient = user(8L);
        Wallet defaultWallet = wallet(3L, recipient, currency("BGN", 2), BigDecimal.TEN, true);
        when(walletRepository.findAllByUserIdAndIsDeletedFalse(8L)).thenReturn(List.of());
        when(walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(8L)).thenReturn(Optional.of(defaultWallet));

        Wallet result = service.resolveRecipientWallet(8L, "USD");

        assertThat(result).isSameAs(defaultWallet);
    }

    @Test
    void resolveRecipientWallet_throwsWhenNoDefaultFallbackExists() {
        when(walletRepository.findAllByUserIdAndIsDeletedFalse(8L)).thenReturn(List.of());
        when(walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolveRecipientWallet(8L, "USD"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
