package com.krushkov.virtualwallet.models.dtos.responses.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserPrincipalResponse(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "johndoe")
        String username,

        @Schema(description = "User role", example = "ROLE_USER")
        String role,

        @Schema(description = "Whether the user is blocked", example = "false")
        Boolean isBlocked
) {}
