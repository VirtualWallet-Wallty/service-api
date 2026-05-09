package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.helpers.validations.CardValidations;
import com.krushkov.virtualwallet.helpers.validations.UserValidations;
import com.krushkov.virtualwallet.models.Card;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.enums.CardStatus;
import com.krushkov.virtualwallet.repositories.CardRepository;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.services.contracts.CardService;
import com.krushkov.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Card> getAllByUserId(Long targetUserId) {
        UserValidations.validateIsAdmin();
        return cardRepository.findAllByUserIdAndIsDeletedFalse(targetUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Card> getAllMyCards() {
        UserValidations.validateIsNotAdmin();
        return cardRepository.findAllByUserIdAndIsDeletedFalse(PrincipalContext.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Card getById(Long targetCardId) {
        if (PrincipalContext.isAdmin()) {
            return findCard(targetCardId);
        }

        return findCardOwnedBy(targetCardId);
    }

    @Override
    @Transactional
    public Card add(Card card) {
        UserValidations.validateIsNotAdmin();
        Long principalId = PrincipalContext.getId();
        CardValidations.validateAlreadyExists(cardRepository, principalId, card.getCardSuffix());
        User user = userService.getById(principalId);
        card.setCardHolder(card.getCardHolder().toUpperCase());

        card.setUser(user);
        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public void remove(Long targetCardId) {
        UserValidations.validateIsNotAdmin();
        Card targetCard = findCardOwnedBy(targetCardId);
        targetCard.setDeleted(true);
    }

    @Override
    @Transactional
    public void activate(Long targetCardId) {
        if (PrincipalContext.isAdmin()) {
            Card targetCard = getById(targetCardId);
            adminActivate(targetCard);
        } else {
            Card targetCard = getById(targetCardId);
            userActivate(targetCard);
        }
    }

    @Override
    @Transactional
    public void deactivate(Long targetCardId) {
        if (PrincipalContext.isAdmin()) {
            Card targetCard = getById(targetCardId);
            adminDeactivate(targetCard);
        } else {
            Card targetCard = getById(targetCardId);
            userDeactivate(targetCard);
        }
    }

    private Card findCardOwnedBy(Long targetCardId) {
        Long principalId = PrincipalContext.getId();
        return cardRepository.findByIdAndUserIdAndIsDeletedFalse(targetCardId, principalId)
                .orElseThrow(() -> new EntityNotFoundException("Card", targetCardId));
    }

    private void userActivate(Card card) {
        CardValidations.validateUserCanActivate(card);
        CardValidations.validateCanActivate(card);
        card.setStatus(CardStatus.ACTIVE);
    }

    private void userDeactivate(Card card) {
        CardValidations.validateUserCanDeactivate(card);
        CardValidations.validateCanDeactivate(card);
        card.setStatus(CardStatus.USER_DEACTIVATED);
    }

    private Card findCard(Long targetCardId) {
        return cardRepository.findByIdAndIsDeletedFalse(targetCardId)
                .orElseThrow(() -> new EntityNotFoundException("Card", targetCardId));
    }

    private void adminActivate(Card card) {
        CardValidations.validateCanActivate(card);
        card.setStatus(CardStatus.ACTIVE);
    }

    private void adminDeactivate(Card card) {
        CardValidations.validateCanDeactivate(card);
        card.setStatus(CardStatus.ADMIN_DEACTIVATED);
    }
}