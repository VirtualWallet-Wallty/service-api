package com.krushkov.virtualwallet.services.contracts;

import com.krushkov.virtualwallet.models.Card;

import java.util.List;

public interface CardService {

    List<Card> getAllByUserId(Long targetCardId);

    List<Card> getAllMyCards();

    Card getById(Long targetCardId);

    Card add(Card card);

    void remove(Long targetCardId);

    void activate(Long targetCardId);

    void deactivate(Long targetCardId);

}
