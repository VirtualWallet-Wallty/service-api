package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.factories.TransactionFactory;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.TransferRequest;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferServiceImplTest {

    private final WalletService walletService = mock(WalletService.class);
    private final WalletResolutionService walletResolutionService = mock(WalletResolutionService.class);
    private final CurrencyConversionService currencyConversionService = mock(CurrencyConversionService.class);
    private final CurrencyService currencyService = mock(CurrencyService.class);
    private final TransactionService transactionService = mock(TransactionService.class);
    private final TransferServiceImpl service = new TransferServiceImpl(
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
    void transfer_rejectsSameWalletBeforeMovingMoney() {
        Currency eur = currency("EUR", 2);
        Wallet sameWallet = wallet(5L, user(10L), eur, new BigDecimal("100.00"), true);
        TransferRequest request = new TransferRequest(new BigDecimal("10.00"), "eur", 20L, 5L, 5L);
        when(walletResolutionService.resolveSenderWallet(10L, "eur", 5L)).thenReturn(sameWallet);
        when(walletService.getById(5L)).thenReturn(sameWallet);

        assertThatThrownBy(() -> service.transfer(request))
                .isInstanceOf(InvalidOperationException.class);

        verify(walletService, never()).debit(any(), any());
        verify(transactionService, never()).create(any());
    }

    @Test
    void transfer_convertsRequestCurrencyForSenderAndRecipientWallets() {
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        Currency bgn = currency("BGN", 2);
        User sender = user(10L);
        User recipient = user(20L);
        Wallet senderWallet = wallet(1L, sender, eur, new BigDecimal("100.00"), true);
        Wallet recipientWallet = wallet(2L, recipient, bgn, BigDecimal.ZERO, true);
        TransferRequest request = new TransferRequest(new BigDecimal("10.00"), "usd", 20L, 1L, null);

        when(walletResolutionService.resolveSenderWallet(10L, "usd", 1L)).thenReturn(senderWallet);
        when(walletResolutionService.resolveRecipientWallet(20L, "usd")).thenReturn(recipientWallet);
        when(currencyService.getByCode("USD")).thenReturn(usd);
        when(currencyConversionService.convert(new BigDecimal("10.00"), usd, bgn, 2))
                .thenReturn(new ConversionResult(new BigDecimal("10.00"), usd, new BigDecimal("18.00"), bgn, new BigDecimal("1.80")));
        when(currencyConversionService.convert(BigDecimal.ONE, eur, bgn, 2))
                .thenReturn(new ConversionResult(BigDecimal.ONE, eur, new BigDecimal("1.96"), bgn, new BigDecimal("1.96")));
        when(currencyConversionService.convert(new BigDecimal("10.00"), usd, eur, 2))
                .thenReturn(new ConversionResult(new BigDecimal("10.00"), usd, new BigDecimal("9.20"), eur, new BigDecimal("0.92")));

        Transaction tx = service.transfer(request);

        verify(walletService).debit(1L, new BigDecimal("9.20"));
        verify(walletService).creditRecipientWallet(2L, 20L, new BigDecimal("18.00"));
        assertThat(tx.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(tx.getSenderAmount()).isEqualByComparingTo("9.20");
        assertThat(tx.getSenderCurrency()).isSameAs(eur);
        assertThat(tx.getRecipientAmount()).isEqualByComparingTo("18.00");
        assertThat(tx.getRecipientCurrency()).isSameAs(bgn);
        assertThat(tx.getExchangeRate()).isEqualByComparingTo("1.96");
    }
}
