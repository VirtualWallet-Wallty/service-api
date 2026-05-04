package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.external.ExchangeRateApiResponse;
import com.krushkov.virtualwallet.external.ExchangeRateClient;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.ExchangeRate;
import com.krushkov.virtualwallet.repositories.CurrencyRepository;
import com.krushkov.virtualwallet.repositories.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateSyncServiceImplTest {

    private final ExchangeRateClient exchangeRateClient = mock(ExchangeRateClient.class);
    private final ExchangeRateRepository exchangeRateRepository = mock(ExchangeRateRepository.class);
    private final CurrencyRepository currencyRepository = mock(CurrencyRepository.class);
    private final ExchangeRateSyncServiceImpl service = new ExchangeRateSyncServiceImpl(
            exchangeRateClient,
            exchangeRateRepository,
            currencyRepository
    );

    @Test
    void syncRates_savesForwardAndReverseRatesForActiveCurrencies() {
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        long timestamp = 1_700_000_000L;
        when(exchangeRateClient.fetchRates("EUR")).thenReturn(new ExchangeRateApiResponse(
                "success",
                timestamp,
                "EUR",
                Map.of("EUR", BigDecimal.ONE, "USD", new BigDecimal("1.25000000"))
        ));
        when(currencyRepository.findByCodeAndIsActiveTrue("EUR")).thenReturn(Optional.of(eur));
        when(currencyRepository.findByCodeAndIsActiveTrue("USD")).thenReturn(Optional.of(usd));
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(any(), any())).thenReturn(Optional.empty());

        service.syncRates(eur);

        ArgumentCaptor<ExchangeRate> captor = ArgumentCaptor.forClass(ExchangeRate.class);
        verify(exchangeRateRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(rate -> rate.getFromCurrency().getCode() + "->" + rate.getToCurrency().getCode())
                .containsExactlyInAnyOrder("EUR->USD", "USD->EUR");
        assertThat(captor.getAllValues())
                .extracting(ExchangeRate::getRate)
                .anySatisfy(rate -> assertThat(rate).isEqualByComparingTo("1.25000000"))
                .anySatisfy(rate -> assertThat(rate).isEqualByComparingTo("0.80000000"));
        assertThat(captor.getValue().getLastUpdated())
                .isEqualTo(LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC));
    }

    @Test
    void syncRates_skipsMissingTargetCurrencies() {
        Currency eur = currency("EUR", 2);
        when(exchangeRateClient.fetchRates("EUR")).thenReturn(new ExchangeRateApiResponse(
                "success",
                1_700_000_000L,
                "EUR",
                Map.of("GBP", new BigDecimal("0.85"))
        ));
        when(currencyRepository.findByCodeAndIsActiveTrue("EUR")).thenReturn(Optional.of(eur));
        when(currencyRepository.findByCodeAndIsActiveTrue("GBP")).thenReturn(Optional.empty());

        service.syncRates(eur);

        verify(exchangeRateRepository, never()).save(any());
    }

    @Test
    void syncRatesScheduled_doesNothingWhenDefaultBaseCurrencyIsMissing() {
        when(currencyRepository.findByCodeAndIsActiveTrue("EUR")).thenReturn(Optional.empty());

        service.syncRatesScheduled();

        verify(exchangeRateClient, never()).fetchRates(any());
    }
}
