package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.ExchangeRate;
import com.krushkov.virtualwallet.repositories.ExchangeRateRepository;
import com.krushkov.virtualwallet.services.contracts.CurrencyService;
import com.krushkov.virtualwallet.services.contracts.ExchangeRateSyncService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asAdmin;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateServiceImplTest {

    private final ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);
    private final ExchangeRateSyncService exchangeRateSyncService = mock(ExchangeRateSyncService.class);
    private final CurrencyService currencyService = mock(CurrencyService.class);
    private final ExchangeRateServiceImpl service = new ExchangeRateServiceImpl(
            exchangeRateRepository,
            exchangeRateSyncService,
            currencyService
    );

    @BeforeEach
    void setUp() {
        asAdmin(1L);
    }

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void getAllRates_delegatesToRepository() {
        List<ExchangeRate> rates = List.of(new ExchangeRate());
        when(exchangeRateRepository.findAll()).thenReturn(rates);

        assertThat(service.getAllRates()).isSameAs(rates);
    }

    @Test
    void getRates_normalizesBaseCurrencyAndQueriesRepository() {
        Currency eur = currency("EUR", 2);
        List<ExchangeRate> rates = List.of(new ExchangeRate());
        when(currencyService.getByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.findAllByFromCurrency(eur)).thenReturn(rates);

        assertThat(service.getRates(" eur ")).isSameAs(rates);
    }

    @Test
    void getRate_returnsOneForSameCurrency() {
        Currency eur = currency("EUR", 2);
        when(currencyService.getByCode("EUR")).thenReturn(eur);

        assertThat(service.getRate("eur", "EUR")).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void getRate_returnsStoredExchangeRate() {
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(new BigDecimal("1.08"));
        when(currencyService.getByCode("EUR")).thenReturn(eur);
        when(currencyService.getByCode("USD")).thenReturn(usd);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(eur, usd)).thenReturn(Optional.of(exchangeRate));

        assertThat(service.getRate("eur", "usd")).isEqualByComparingTo("1.08");
    }

    @Test
    void getRate_throwsWhenExchangeRateIsMissing() {
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        when(currencyService.getByCode("EUR")).thenReturn(eur);
        when(currencyService.getByCode("USD")).thenReturn(usd);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(eur, usd)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRate("eur", "usd"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void syncRates_normalizesBaseCurrencyAndDelegates() {
        Currency eur = currency("EUR", 2);
        when(currencyService.getByCode("EUR")).thenReturn(eur);

        service.syncRates(" eur ");

        verify(exchangeRateSyncService).syncRates(eur);
    }
}
