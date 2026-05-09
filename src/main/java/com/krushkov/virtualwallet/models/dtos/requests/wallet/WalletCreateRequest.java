package com.krushkov.virtualwallet.models.dtos.requests.wallet;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletCreateRequest(

        @Schema(description = "Wallet name", example = "My Savings")
        @NotBlank(message = ConstantMessages.WALLET_NAME_NOT_NULL_ERROR)
        @Size(min = 3, max = 16, message = ConstantMessages.WALLET_NAME_LENGTH_ERROR)
        String name,

        @Schema(description = "3-letter ISO 4217 currency code", example = "USD")
        @NotBlank(message = ConstantMessages.CURRENCY_CODE_NOT_NULL_ERROR)
        @Size(min = 3, max = 3, message = ConstantMessages.CURRENCY_CODE_LENGTH_ERROR)
        String currencyCode
) {}
