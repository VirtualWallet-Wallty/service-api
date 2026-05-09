package com.krushkov.virtualwallet.models.dtos.responses.wallet;

import com.krushkov.virtualwallet.models.dtos.responses.currency.CurrencyShortResponse;
import com.krushkov.virtualwallet.models.dtos.responses.user.UserShortResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletLongResponse(
        @Schema(description = "Wallet ID", example = "1")
        Long id,

        @Schema(description = "Wallet owner")
        UserShortResponse owner,

        @Schema(description = "Wallet name", example = "My Savings")
        String name,

        @Schema(description = "Current balance", example = "1250.00")
        BigDecimal balance,

        @Schema(description = "Wallet currency")
        CurrencyShortResponse currency,

        @Schema(description = "Optimistic locking version", example = "3")
        Long version,

        @Schema(description = "Whether this is the default wallet", example = "true")
        Boolean isDefault,

        @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp", example = "2024-06-01T14:00:00")
        LocalDateTime updatedAt
) {}
