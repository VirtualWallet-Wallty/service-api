package com.krushkov.virtualwallet.models.dtos.requests.wallet;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record WalletFilterOptions(

        @Positive(message = ConstantMessages.WALLET_USER_ID_LENGTH_ERROR)
        Long userId,

        String currencyCode,

        @PositiveOrZero(message = ConstantMessages.WALLET_MIN_BALANCE_LENGTH_ERROR)
        BigDecimal minBalance,

        @PositiveOrZero(message = ConstantMessages.WALLET_MAX_BALANCE_LENGTH_ERROR)
        BigDecimal maxBalance
) {
    @AssertTrue(message = ConstantMessages.WALLET_BALANCE_RANGE_ERROR)
    public boolean isValidBalanceRange() {
        if (minBalance == null || maxBalance == null) {
            return true;
        }
        return minBalance.compareTo(maxBalance) <= 0;
    }
}
