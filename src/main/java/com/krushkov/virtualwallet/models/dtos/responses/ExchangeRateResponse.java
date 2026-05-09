package com.krushkov.virtualwallet.models.dtos.responses;

import com.krushkov.virtualwallet.models.dtos.responses.currency.CurrencyShortResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateResponse(
        @Schema(description = "Exchange rate record ID", example = "42")
        Long id,

        @Schema(description = "Source currency")
        CurrencyShortResponse fromCurrency,

        @Schema(description = "Target currency")
        CurrencyShortResponse toCurrency,

        @Schema(description = "Exchange rate multiplier", example = "1.0856")
        BigDecimal rate,

        @Schema(description = "Last update timestamp", example = "2024-01-15T08:00:00")
        LocalDateTime lastUpdated
) {}
