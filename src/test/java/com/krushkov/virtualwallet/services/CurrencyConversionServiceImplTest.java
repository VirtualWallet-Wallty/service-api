package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.dtos.responses.currency.ConversionResult;
import com.krushkov.virtualwallet.services.contracts.ExchangeRateService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConversionServiceImplTest {

    private final ExchangeRateService exchangeRateService = mock(ExchangeRateService.class);
    private final CurrencyConversionServiceImpl service = new CurrencyConversionServiceImpl(exchangeRateService);

    @Test
    void convert_multipliesByRateAndRoundsToTargetScale() {
        Currency eur = currency("EUR", 2);
        Currency usd = currency("USD", 2);
        when(exchangeRateService.getRate("EUR", "USD")).thenReturn(new BigDecimal("1.08765"));

        ConversionResult result = service.convert(new BigDecimal("10.00"), eur, usd, 2);

        assertThat(result.senderAmount()).isEqualByComparingTo("10.00");
        assertThat(result.senderCurrency()).isSameAs(eur);
        assertThat(result.recipientAmount()).isEqualByComparingTo("10.88");
        assertThat(result.recipientCurrency()).isSameAs(usd);
        assertThat(result.rate()).isEqualByComparingTo("1.08765");
    }
}
