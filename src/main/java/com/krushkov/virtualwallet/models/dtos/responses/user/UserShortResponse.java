package com.krushkov.virtualwallet.models.dtos.responses.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserShortResponse(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "johndoe")
        String username,

        @Schema(description = "First name", example = "John")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Profile photo URL", example = "https://example.com/photo.jpg")
        String photoUrl
) {}
