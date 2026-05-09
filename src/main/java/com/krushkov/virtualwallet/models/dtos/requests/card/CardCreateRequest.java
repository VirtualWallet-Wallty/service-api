package com.krushkov.virtualwallet.models.dtos.requests.card;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.YearMonth;

public record CardCreateRequest(

        @Schema(description = "Name as it appears on the card", example = "JOHN DOE")
        @Size(min = 6, max = 100, message = ConstantMessages.CARD_HOLDER_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CARD_HOLDER_NOT_NULL_ERROR)
        String cardHolder,

        @Schema(description = "16-digit card number", example = "4111111111111111")
        @Pattern(regexp = "\\d{16}", message = ConstantMessages.CARD_NUMBER_LENGTH_ERROR)
        @NotBlank(message = ConstantMessages.CARD_NUMBER_NOT_NULL_ERROR)
        String cardNumber,

        @Schema(description = "Card expiration month (1-12)", example = "12")
        @Min(1) @Max(12)
        @NotNull(message = ConstantMessages.CARD_EXPIRATION_MONTH_NOT_NULL_ERROR)
        Integer expirationMonth,

        @Schema(description = "Card expiration year", example = "2027")
        @NotNull(message = ConstantMessages.CARD_EXPIRATION_YEAR_NOT_NULL_ERROR)
        Integer expirationYear
) {
    @AssertTrue(message = ConstantMessages.CARD_EXPIRED_ERROR)
    public boolean isExpirationDateValid() {
        if (expirationMonth == null || expirationYear == null) {
            return true;
        }

        YearMonth expiration = YearMonth.of(expirationYear, expirationMonth);
        return !expiration.isBefore(YearMonth.now());
    }
}
