package com.krushkov.virtualwallet.models.dtos.responses.card;

import com.krushkov.virtualwallet.models.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CardShortResponse(
        @Schema(description = "Card ID", example = "1")
        Long id,

        @Schema(description = "Owner user ID", example = "5")
        Long ownerId,

        @Schema(description = "Last 4 digits of the card number", example = "1111")
        String cardSuffix,

        @Schema(description = "Card status", example = "ACTIVE")
        CardStatus status
) {}
