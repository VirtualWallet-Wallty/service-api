package com.krushkov.virtualwallet.models.dtos.responses.currency;

import io.swagger.v3.oas.annotations.media.Schema;

public record CurrencyLongResponse(
        @Schema(description = "3-letter ISO 4217 currency code", example = "USD")
        String code,

        @Schema(description = "Full currency name", example = "US Dollar")
        String name,

        @Schema(description = "Currency symbol", example = "$")
        String symbol,

        @Schema(description = "Number of decimal places", example = "2")
        Integer decimals,

        @Schema(description = "Whether the currency is active", example = "true")
        Boolean isActive
) {}
