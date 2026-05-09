package com.krushkov.virtualwallet.models.dtos.requests.user;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDateTime;

public record UserFilterOptions(
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,

        Boolean isBlocked,

        LocalDateTime createdFrom,
        LocalDateTime createdTo
) {
    @AssertTrue(message = ConstantMessages.USER_CREATE_RANGE_ERROR)
    public boolean isValidCreateRange() {
        if (createdFrom == null || createdTo == null) {
            return true;
        }
        return !createdFrom.isAfter(createdTo);
    }
}
