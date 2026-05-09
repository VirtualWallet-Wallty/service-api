package com.krushkov.virtualwallet.models.dtos.responses.transaction;

import com.krushkov.virtualwallet.models.dtos.responses.currency.CurrencyShortResponse;
import com.krushkov.virtualwallet.models.dtos.responses.user.UserShortResponse;
import com.krushkov.virtualwallet.models.dtos.responses.wallet.WalletShortResponse;
import com.krushkov.virtualwallet.models.enums.TransactionStatus;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionLongResponse(
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

        @Schema(description = "Sender currency")
        CurrencyShortResponse senderCurrency,

        @Schema(description = "Amount received by recipient", example = "43.18")
        BigDecimal recipientAmount,

        @Schema(description = "Recipient currency")
        CurrencyShortResponse recipientCurrency,

        @Schema(description = "Applied exchange rate", example = "0.8637")
        BigDecimal exchangeRate,

        @Schema(description = "Direction relative to the current user", example = "OUTGOING")
        String direction,

        @Schema(description = "Sender user")
        UserShortResponse sender,

        @Schema(description = "Recipient user")
        UserShortResponse recipient,

        @Schema(description = "Sender wallet")
        WalletShortResponse senderWallet,

        @Schema(description = "Recipient wallet")
        WalletShortResponse recipientWallet,

        @Schema(description = "External reference", example = "order-12345")
        String externalReference,

        @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
