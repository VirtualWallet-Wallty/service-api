package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.helpers.factories.TransactionFactory;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.PaymentRequest;
import com.krushkov.virtualwallet.models.dtos.responses.currency.ConversionResult;
import com.krushkov.virtualwallet.models.enums.TransactionType;
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
import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.wallet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceImplTest {

    private final WalletService walletService = mock(WalletService.class);
    private final WalletResolutionService walletResolutionService = mock(WalletResolutionService.class);
    private final CurrencyConversionService currencyConversionService = mock(CurrencyConversionService.class);
    private final CurrencyService currencyService = mock(CurrencyService.class);
    private final TransactionService transactionService = mock(TransactionService.class);
    private final PaymentServiceImpl service = new PaymentServiceImpl(
            walletService,
            walletResolutionService,
            currencyConversionService,
            currencyService,
            new TransactionFactory(),
            transactionService
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
    void pay_debitsResolvedWalletAndCreatesPaymentWhenCurrenciesMatch() {
        Currency eur = currency("EUR", 2);
        User payer = user(10L);
        Wallet senderWallet = wallet(5L, payer, eur, new BigDecimal("100.00"), false);
        PaymentRequest request = new PaymentRequest(new BigDecimal("12.50"), "eur", "merchant-1", null);
        when(walletResolutionService.resolveSenderWallet(10L, "eur", null)).thenReturn(senderWallet);
        when(currencyService.getByCode("EUR")).thenReturn(eur);

        Transaction tx = service.pay(request);

        verify(walletService).debit(5L, new BigDecimal("12.50"));
        assertThat(tx.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(tx.getSenderWallet()).isSameAs(senderWallet);
        assertThat(tx.getSenderAmount()).isEqualByComparingTo("12.50");
        assertThat(tx.getRecipientAmount()).isEqualByComparingTo("12.50");
        assertThat(tx.getExchangeRate()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(tx.getExternalReference()).isEqualTo("merchant-1");
    }

    @Test
    void pay_convertsRequestedMerchantAmountToSenderCurrencyBeforeDebit() {
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        Wallet senderWallet = wallet(5L, user(10L), eur, new BigDecimal("100.00"), false);
        PaymentRequest request = new PaymentRequest(new BigDecimal("10.00"), "usd", "merchant-1", 5L);
        ConversionResult conversion = new ConversionResult(
                new BigDecimal("10.00"),
                usd,
                new BigDecimal("9.20"),
                eur,
                new BigDecimal("0.92")
        );

        when(walletResolutionService.resolveSenderWallet(10L, "usd", 5L)).thenReturn(senderWallet);
        when(currencyService.getByCode("USD")).thenReturn(usd);
        when(currencyConversionService.convert(new BigDecimal("10.00"), usd, eur, 2)).thenReturn(conversion);

        Transaction tx = service.pay(request);

        verify(walletService).debit(5L, new BigDecimal("9.20"));
        assertThat(tx.getSenderAmount()).isEqualByComparingTo("9.20");
        assertThat(tx.getSenderCurrency()).isSameAs(eur);
        assertThat(tx.getRecipientAmount()).isEqualByComparingTo("10.00");
        assertThat(tx.getRecipientCurrency()).isSameAs(usd);
        assertThat(tx.getExchangeRate()).isEqualByComparingTo("0.92");
    }
}
