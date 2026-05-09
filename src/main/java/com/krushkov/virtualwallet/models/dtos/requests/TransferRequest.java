package com.krushkov.virtualwallet.models.dtos.requests;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferRequest(

        @Schema(description = "Transfer amount", example = "200.00")
        @DecimalMin(value = "0.01", inclusive = true, message = ConstantMessages.AMOUNT_LENGTH_ERROR)
        @Digits(integer = 15, fraction = 2)
        @NotNull(message = ConstantMessages.AMOUNT_NOT_NULL_ERROR)
        BigDecimal amount,

        @Schema(description = "3-letter ISO 4217 currency code", example = "EUR")
        @Size(min = 3, max = 3, message = ConstantMessages.CURRENCY_CODE_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CURRENCY_CODE_NOT_NULL_ERROR)
        String currencyCode,

        @Schema(description = "Recipient user ID", example = "5")
        @NotNull(message = ConstantMessages.RECIPIENT_ID_NOT_NULL_ERROR)
        Long recipientId,

        @Schema(description = "Sender wallet ID (null = default wallet)", example = "1")
        @Positive(message = ConstantMessages.WALLET_ID_LENGTH_ERROR)
        Long sourceWalletId,

        @Schema(description = "Recipient wallet ID (null = default wallet)", example = "3")
        @Positive(message = ConstantMessages.WALLET_ID_LENGTH_ERROR)
        Long targetWalletId
) {}
