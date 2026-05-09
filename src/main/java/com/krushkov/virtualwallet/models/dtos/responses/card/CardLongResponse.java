package com.krushkov.virtualwallet.models.dtos.responses.card;

import com.krushkov.virtualwallet.models.dtos.responses.user.UserShortResponse;
import com.krushkov.virtualwallet.models.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CardLongResponse(
        @Schema(description = "Card ID", example = "1")
        Long id,

        @Schema(description = "Card owner")
        UserShortResponse owner,

        @Schema(description = "Name on the card", example = "JOHN DOE")
        String cardHolder,

        @Schema(description = "Last 4 digits of the card number", example = "1111")
        String cardSuffix,

        @Schema(description = "Expiration month", example = "12")
        Integer expirationMonth,

        @Schema(description = "Expiration year", example = "2027")
        Integer expirationYear,

        @Schema(description = "Card status", example = "ACTIVE")
        CardStatus status,

        @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
