package com.krushkov.virtualwallet.models.dtos.requests;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaymentRequest(

        @Schema(description = "Payment amount", example = "49.99")
        @DecimalMin(value = "0.01", inclusive = true, message = ConstantMessages.AMOUNT_LENGTH_ERROR)
        @Digits(integer = 15, fraction = 2)
        @NotNull(message = ConstantMessages.AMOUNT_NOT_NULL_ERROR)
        BigDecimal amount,

        @Schema(description = "3-letter ISO 4217 currency code", example = "USD")
        @Size(min = 3, max = 3, message = ConstantMessages.CURRENCY_CODE_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CURRENCY_CODE_NOT_NULL_ERROR)
        String currencyCode,

        @Schema(description = "External reference from the merchant", example = "order-12345")
        String merchantReference,

        @Schema(description = "Wallet ID to deduct funds from (null = default wallet)", example = "1")
        @Positive(message = ConstantMessages.WALLET_ID_LENGTH_ERROR)
        Long sourceWalletId
) {}
