package com.krushkov.virtualwallet.models.dtos.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Username or email", example = "johndoe")
        @NotBlank String identifier,

        @Schema(description = "Account password", example = "secret123")
        @NotBlank String password
) {}
