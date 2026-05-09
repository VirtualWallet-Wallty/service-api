package com.krushkov.virtualwallet.models.dtos.requests.wallet;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletUpdateRequest(
        @Schema(description = "New wallet name", example = "Travel Fund")
        @NotBlank(message = ConstantMessages.WALLET_NAME_NOT_NULL_ERROR)
        @Size(min = 3, max = 16, message = ConstantMessages.WALLET_NAME_LENGTH_ERROR)
        String name
) {}
