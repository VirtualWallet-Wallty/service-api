package com.krushkov.virtualwallet.models.dtos.responses.transaction;

import com.krushkov.virtualwallet.models.enums.TransactionStatus;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionShortResponse(
        @Schema(description = "Transaction ID", example = "100")
        Long id,

        @Schema(description = "Transaction label", example = "Payment to merchant")
        String label,

        @Schema(description = "Transaction type", example = "PAYMENT")
        TransactionType type,

        @Schema(description = "Transaction status", example = "COMPLETED")
        TransactionStatus status,

        @Schema(description = "Amount deducted from sender", example = "49.99")
        BigDecimal senderAmount,

        @Schema(description = "Sender currency code", example = "USD")
        String senderCurrencyCode,

        @Schema(description = "Amount received by recipient", example = "43.18")
        BigDecimal recipientAmount,

        @Schema(description = "Recipient currency code", example = "EUR")
        String recipientCurrencyCode,

        @Schema(description = "Direction relative to current user", example = "OUTGOING")
        String direction,

        @Schema(description = "Sender wallet ID", example = "1")
        Long senderWalletId,

        @Schema(description = "Recipient wallet ID", example = "3")
        Long recipientWalletId,

        @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
