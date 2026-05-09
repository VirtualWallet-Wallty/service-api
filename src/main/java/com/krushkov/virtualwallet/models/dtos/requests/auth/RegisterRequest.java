package com.krushkov.virtualwallet.models.dtos.requests.auth;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(description = "Unique username", example = "johndoe")
        @NotBlank(message = ConstantMessages.USERNAME_NOT_NULL_ERROR)
        @Size(min = 4, max = 32, message = ConstantMessages.USERNAME_LENGTH_ERROR)
        String username,

        @Schema(description = "Account password", example = "secret123")
        @NotBlank(message = ConstantMessages.PASSWORD_NOT_NULL_ERROR)
        @Size(min = 6, max = 128, message = ConstantMessages.PASSWORD_LENGTH_ERROR)
        String password,

        @Schema(description = "First name", example = "John")
        @NotBlank(message = ConstantMessages.FIRST_NAME_NOT_NULL_ERROR)
        @Size(min = 4, max = 50, message = ConstantMessages.FIRST_NAME_LENGTH_ERROR)
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        @NotBlank(message = ConstantMessages.LAST_NAME_NOT_NULL_ERROR)
        @Size(min = 4, max = 50, message = ConstantMessages.LAST_NAME_LENGTH_ERROR)
        String lastName,

        @Schema(description = "Email address", example = "john.doe@example.com")
        @NotBlank(message = ConstantMessages.EMAIL_NOT_NULL_ERROR)
        @Email(message = ConstantMessages.EMAIL_INVALID_ERROR)
        @Size(min = 6, max = 255, message = ConstantMessages.EMAIL_LENGTH_ERROR)
        String email
) {}
