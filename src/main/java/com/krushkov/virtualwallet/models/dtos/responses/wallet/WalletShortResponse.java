package com.krushkov.virtualwallet.models.dtos.responses.wallet;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record WalletShortResponse(
        @Schema(description = "Wallet ID", example = "1")
        Long id,

        @Schema(description = "Owner user ID", example = "5")
        Long ownerId,

        @Schema(description = "Wallet name", example = "My Savings")
        String name,

        @Schema(description = "Current balance", example = "1250.00")
        BigDecimal balance,

        @Schema(description = "Currency code", example = "USD")
        String currencyCode,

        @Schema(description = "Whether this is the default wallet", example = "true")
        Boolean isDefault
) {}
