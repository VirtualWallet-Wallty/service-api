package com.krushkov.virtualwallet.models.dtos.requests.currency;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CurrencyCreateRequest(
        @Schema(description = "3-letter ISO 4217 currency code", example = "USD")
        @Size(min = 3, max = 3, message = ConstantMessages.CURRENCY_CODE_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CURRENCY_CODE_NOT_NULL_ERROR)
        String code,

        @Schema(description = "Full currency name", example = "US Dollar")
        @Size(max = 50, message = ConstantMessages.CURRENCY_NAME_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CURRENCY_NAME_NOT_NULL_ERROR)
        String name,

        @Schema(description = "Currency symbol", example = "$")
        @Size(min = 6, max = 100, message = ConstantMessages.CURRENCY_SYMBOL_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CURRENCY_SYMBOL_NOT_NULL_ERROR)
        String symbol,

        @Schema(description = "Number of decimal places", example = "2")
        @Min(value = 0, message = ConstantMessages.CURRENCY_MIN_DECIMALS_LENGTH_ERROR)
        @Min(value = 10, message = ConstantMessages.CURRENCY_MAX_DECIMALS_LENGTH_ERROR)
        @NotNull(message = ConstantMessages.CURRENCY_DECIMALS_NOT_NULL_ERROR)
        Integer decimals
) {
}
