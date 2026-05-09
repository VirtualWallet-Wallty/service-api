package com.krushkov.virtualwallet.models.dtos.responses.currency;

import io.swagger.v3.oas.annotations.media.Schema;

public record CurrencyShortResponse(
        @Schema(description = "3-letter ISO 4217 currency code", example = "USD")
        String code,

        @Schema(description = "Full currency name", example = "US Dollar")
        String name,

        @Schema(description = "Currency symbol", example = "$")
        String symbol
) {}
