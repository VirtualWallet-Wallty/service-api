package com.krushkov.virtualwallet.models.dtos.responses.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserLongResponse(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "johndoe")
        String username,

        @Schema(description = "First name", example = "John")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "10-digit phone number", example = "0888123456")
        String phoneNumber,

        @Schema(description = "Profile photo URL", example = "https://example.com/photo.jpg")
        String photoUrl,

        @Schema(description = "User role", example = "ROLE_USER")
        String role,

        @Schema(description = "Whether the user is blocked", example = "false")
        Boolean isBlocked,

        @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp", example = "2024-06-01T14:00:00")
        LocalDateTime updatedAt
) {}
