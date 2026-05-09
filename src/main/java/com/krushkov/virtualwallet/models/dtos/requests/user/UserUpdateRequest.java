package com.krushkov.virtualwallet.models.dtos.requests.user;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Schema(description = "New username", example = "johndoe2")
        @Size(min = 4, max = 32, message = ConstantMessages.USERNAME_LENGTH_ERROR)
        String username,

        @Schema(description = "New password", example = "newpassword123")
        @NotBlank(message = ConstantMessages.PASSWORD_NOT_NULL_ERROR)
        @Size(min = 6, max = 128, message = ConstantMessages.PASSWORD_LENGTH_ERROR)
        String password,

        @Schema(description = "First name", example = "John")
        @Size(min = 4, max = 50, message = ConstantMessages.FIRST_NAME_LENGTH_ERROR)
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        @Size(min = 4, max = 50, message = ConstantMessages.LAST_NAME_LENGTH_ERROR)
        String lastName,

        @Schema(description = "Email address", example = "john.new@example.com")
        @Email(message = ConstantMessages.EMAIL_INVALID_ERROR)
        @Size(min = 6, max = 255, message = ConstantMessages.EMAIL_LENGTH_ERROR)
        String email,

        @Schema(description = "10-digit phone number", example = "0888123456")
        @Size(min = 10, max = 10, message = ConstantMessages.PHONE_NUMBER_LENGTH_ERROR)
        String phoneNumber,

        @Schema(description = "Profile photo URL", example = "https://example.com/photo.jpg")
        @Size(min = 6, max = 512, message = ConstantMessages.PHOTO_URL_LENGTH_ERROR)
        String photoUrl

) {}
