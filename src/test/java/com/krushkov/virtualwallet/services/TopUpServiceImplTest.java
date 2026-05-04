package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.factories.TransactionFactory;
import com.krushkov.virtualwallet.models.Card;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.TopUpRequest;
import com.krushkov.virtualwallet.models.dtos.responses.currency.ConversionResult;
import com.krushkov.virtualwallet.models.enums.CardStatus;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import com.krushkov.virtualwallet.services.contracts.CardService;
import com.krushkov.virtualwallet.services.contracts.CurrencyConversionService;
import com.krushkov.virtualwallet.services.contracts.CurrencyService;
import com.krushkov.virtualwallet.services.contracts.TransactionService;
import com.krushkov.virtualwallet.services.contracts.WalletResolutionService;
import com.krushkov.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.card;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.wallet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TopUpServiceImplTest {

    private final WalletService walletService = mock(WalletService.class);
    private final WalletResolutionService walletResolutionService = mock(WalletResolutionService.class);
    private final CurrencyConversionService currencyConversionService = mock(CurrencyConversionService.class);
    private final CurrencyService currencyService = mock(CurrencyService.class);
    private final TransactionService transactionService = mock(TransactionService.class);
    private final CardService cardService = mock(CardService.class);
    private final TopUpServiceImpl service = new TopUpServiceImpl(
            walletService,
            walletResolutionService,
            currencyConversionService,
            currencyService,
            new TransactionFactory(),
            transactionService,
            cardService
    );

    @BeforeEach
    void setUp() {
        asUser(10L);
        when(transactionService.create(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void topUp_creditsConvertedAmountAndCreatesTopUpTransaction() {
        User owner = user(10L);
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        Wallet wallet = wallet(5L, owner, eur, BigDecimal.ZERO, true);
        Card card = card(3L, owner, CardStatus.ACTIVE);
        TopUpRequest request = new TopUpRequest(5L, new BigDecimal("10.00"), "usd", "topup-1", 3L);
        ConversionResult conversion = new ConversionResult(
                new BigDecimal("10.00"),
                usd,
                new BigDecimal("9.20"),
                eur,
                new BigDecimal("0.92")
        );

        when(walletResolutionService.resolveSenderWallet(10L, "usd", 5L)).thenReturn(wallet);
        when(cardService.getById(3L)).thenReturn(card);
        when(currencyService.getByCode("USD")).thenReturn(usd);
        when(currencyConversionService.convert(new BigDecimal("10.00"), usd, eur, 2)).thenReturn(conversion);

        Transaction tx = service.topUp(request);

        verify(walletService).creditMyWallet(5L, new BigDecimal("9.20"));
        assertThat(tx.getType()).isEqualTo(TransactionType.TOP_UP);
        assertThat(tx.getRecipientWallet()).isSameAs(wallet);
        assertThat(tx.getSenderAmount()).isEqualByComparingTo("10.00");
        assertThat(tx.getRecipientAmount()).isEqualByComparingTo("9.20");
        assertThat(tx.getExchangeRate()).isEqualByComparingTo("0.92");
        assertThat(tx.getExternalReference()).isEqualTo("topup-1");
        assertThat(tx.getLabel()).isEqualTo("FROM ⋅⋅4242");
    }

    @Test
    void topUp_rejectsInactiveCardBeforeCreditingWallet() {
        User owner = user(10L);
        Currency eur = currency("EUR", 2);
        Wallet wallet = wallet(5L, owner, eur, BigDecimal.ZERO, true);
        TopUpRequest request = new TopUpRequest(5L, new BigDecimal("10.00"), "eur", "topup-1", 3L);
        when(walletResolutionService.resolveSenderWallet(10L, "eur", 5L)).thenReturn(wallet);
        when(cardService.getById(3L)).thenReturn(card(3L, owner, CardStatus.USER_DEACTIVATED));

        assertThatThrownBy(() -> service.topUp(request))
                .isInstanceOf(InvalidOperationException.class);

        verify(walletService, never()).creditMyWallet(any(), any());
        verify(transactionService, never()).create(any());
    }
}
