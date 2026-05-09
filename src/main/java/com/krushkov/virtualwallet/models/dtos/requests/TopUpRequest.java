package com.krushkov.virtualwallet.models.dtos.requests;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TopUpRequest(

        @Schema(description = "Wallet ID to top up", example = "1")
        @Positive(message = ConstantMessages.WALLET_ID_LENGTH_ERROR)
        @NotNull(message = ConstantMessages.WALLET_ID_NOT_NULL_ERROR)
        Long walletId,

        @Schema(description = "Top-up amount", example = "100.00")
        @DecimalMin(value = "0.01", inclusive = true, message = ConstantMessages.AMOUNT_LENGTH_ERROR)
        @Digits(integer = 15, fraction = 2)
        @NotNull(message = ConstantMessages.AMOUNT_NOT_NULL_ERROR)
        BigDecimal amount,

        @Schema(description = "3-letter ISO 4217 currency code", example = "USD")
        @Size(min = 3, max = 3, message = ConstantMessages.CURRENCY_CODE_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CURRENCY_CODE_NOT_NULL_ERROR)
        String currencyCode,

        @Schema(description = "Optional external reference", example = "ref-789")
        String externalReference,

        @Schema(description = "Card ID to charge", example = "2")
        @Positive(message = ConstantMessages.CARD_ID_LENGTH_ERROR)
        @NotNull(message = ConstantMessages.CARD_ID_NOT_NULL_ERROR)
        Long cardId
) {}
