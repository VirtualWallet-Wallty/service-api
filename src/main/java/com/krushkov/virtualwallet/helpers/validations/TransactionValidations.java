package com.krushkov.virtualwallet.helpers.validations;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.transaction.TransactionFilterOptions;

import java.math.BigDecimal;

public final class TransactionValidations {

    private TransactionValidations() {
    }

    public static void validateSameCurrency(Wallet senderWallet, Wallet recipientWallet) {
        if (!senderWallet.getCurrency().equals(recipientWallet.getCurrency())) {
            throw new InvalidOperationException(ConstantMessages.WRONG_CURRENCY_ERROR);
        }
    }

    public static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidOperationException(ConstantMessages.POSITIVE_AMOUNT_ERROR);
        }
    }

    public static void validateSufficientFunds(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InvalidOperationException(ConstantMessages.INSUFFICIENT_FUNDS_ERROR);
        }
    }

    public static void validateUserSender(TransactionFilterOptions filters, Long principalId) {
        if (filters.senderId() != null && !filters.senderId().equals(principalId)) {
            throw new InvalidOperationException("Sender filter not allowed.");
        }
    }

    public static void validateUserRecipient(TransactionFilterOptions filters, Long principalId) {
        if (filters.recipientId() != null && !filters.recipientId().equals(principalId)) {
            throw new InvalidOperationException("Recipient filter not allowed.");
        }
    }
}
