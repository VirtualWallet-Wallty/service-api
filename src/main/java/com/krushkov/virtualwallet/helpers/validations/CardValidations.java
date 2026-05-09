package com.krushkov.virtualwallet.helpers.validations;

import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.Card;
import com.krushkov.virtualwallet.models.enums.CardStatus;
import com.krushkov.virtualwallet.repositories.CardRepository;

public final class CardValidations {

    private CardValidations() {}

    public static void validateIsActive(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidOperationException(ConstantMessages.CARD_ACTIVATE_ERROR);
        }
    }

    public static void validateCanActivate(Card card) {
        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new InvalidOperationException(ConstantMessages.CARD_ALREADY_ACTIVE_ERROR);
        }
    }

    public static void validateCanDeactivate(Card card) {
        if (card.getStatus() == CardStatus.ADMIN_DEACTIVATED) {
            throw new InvalidOperationException(ConstantMessages.CARD_DEACTIVATED_BY_ADMIN_ERROR);
        }
    }

    public static void validateUserCanActivate(Card card) {
        if (card.getStatus() == CardStatus.ADMIN_DEACTIVATED) {
            throw new InvalidOperationException(ConstantMessages.CARD_DEACTIVATED_BY_ADMIN_ERROR);
        }
    }

    public static void validateUserCanDeactivate(Card card) {
        if (card.getStatus() == CardStatus.USER_DEACTIVATED) {
            throw new InvalidOperationException(ConstantMessages.CARD_ALREADY_DEACTIVATE_ERROR);
        }

        if (card.getStatus() == CardStatus.ADMIN_DEACTIVATED) {
            throw new InvalidOperationException(ConstantMessages.CARD_DEACTIVATED_BY_ADMIN_ERROR);
        }
    }

    public static void validateAlreadyExists(CardRepository cardRepository, Long userId, String cardSuffix) {
        if (cardRepository.existsByUserIdAndCardSuffixAndIsDeletedFalse(userId, cardSuffix)) {
            throw new InvalidOperationException(ConstantMessages.CARD_ALREADY_EXISTS_ERROR);
        }
    }
}

