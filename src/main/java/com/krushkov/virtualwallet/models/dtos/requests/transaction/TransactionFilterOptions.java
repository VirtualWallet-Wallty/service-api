package com.krushkov.virtualwallet.models.dtos.requests.transaction;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.enums.TransactionStatus;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionFilterOptions(
        String label,

        Long senderId,
        Long recipientId,

        Long senderWalletId,
        Long recipientWalletId,

        TransactionType type,
        TransactionStatus status,

        String senderCurrencyCode,
        String recipientCurrencyCode,

        BigDecimal minSenderAmount,
        BigDecimal maxSenderAmount,

        BigDecimal minRecipientAmount,
        BigDecimal maxRecipientAmount,

        String externalReference,

        LocalDateTime createdFrom,
        LocalDateTime createdTo
) {
    @AssertTrue(message = ConstantMessages.TRANSACTION_CREATE_RANGE_ERROR)
    public boolean isValidCreateRange() {
        if (createdFrom == null || createdTo == null) {
            return true;
        }
        return !createdFrom.isAfter(createdTo);
    }

    @AssertTrue(message = ConstantMessages.TRANSACTION_AMOUNT_RANGE_ERROR)
    public boolean isValidSenderAmountRange() {
        if (minSenderAmount == null || maxSenderAmount == null) {
            return true;
        }
        return minSenderAmount.compareTo(maxSenderAmount) <= 0;
    }

    @AssertTrue(message = ConstantMessages.TRANSACTION_AMOUNT_RANGE_ERROR)
    public boolean isValidRecipientAmountRange() {
        if (minRecipientAmount == null || maxRecipientAmount == null) {
            return true;
        }
        return minRecipientAmount.compareTo(maxRecipientAmount) <= 0;
    }

    public TransactionFilterOptions withoutUserId() {
        return new TransactionFilterOptions(
                label,
                null,
                null,
                senderWalletId,
                recipientWalletId,
                type,
                status,
                senderCurrencyCode,
                recipientCurrencyCode,
                minSenderAmount,
                maxSenderAmount,
                minRecipientAmount,
                maxRecipientAmount,
                externalReference,
                createdFrom,
                createdTo
        );
    }
}
