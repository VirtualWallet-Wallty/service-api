package com.krushkov.virtualwallet.helpers.validations;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.repositories.WalletRepository;

import java.math.BigDecimal;

public final class WalletValidations {

    private static final int WALLET_MAX_COUNT = 5;
    private static final int WALLET_MIN_COUNT = 1;

    private WalletValidations() {}

    public static void validateNameExists(WalletRepository walletRepository, String walletName, Long userId) {
        if (walletRepository.existsByNameAndUserIdAndIsDeletedFalse(walletName, userId)) {
            throw new InvalidOperationException(ConstantMessages.WALLET_NAME_ALREADY_EXISTS_ERROR);
        }
    }

    public static void validateMaxWalletCount(WalletRepository walletRepository, Long userId) {
        if (walletRepository.countByUserIdAndIsDeletedFalse(userId) >= WALLET_MAX_COUNT) {
            throw new InvalidOperationException(
                    String.format(ConstantMessages.WALLET_MAX_COUNT_ERROR, WALLET_MAX_COUNT)
            );
        }
    }

    public static void validateMinWalletCount(WalletRepository walletRepository, Long userId) {
        if (walletRepository.countByUserIdAndIsDeletedFalse(userId) <= WALLET_MIN_COUNT) {
            throw new InvalidOperationException(
                    String.format(ConstantMessages.WALLET_MIN_COUNT_ERROR, WALLET_MIN_COUNT)
            );
        }
    }

    public static void validateAlreadyDefault(Wallet wallet) {
        if (wallet.isDefault()) {
            throw new InvalidOperationException(ConstantMessages.WALLET_ALREADY_DEFAULT_ERROR);
        }
    }

    public static void validateBalanceEmpty(Wallet wallet) {
        if (wallet.getBalance() == null || wallet.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidOperationException(ConstantMessages.WALLET_BALANCE_NOT_EMPTY_ERROR);
        }
    }
}
