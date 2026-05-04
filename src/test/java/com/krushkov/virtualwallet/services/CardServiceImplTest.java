package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.exceptions.InvalidOperationException;
import com.krushkov.virtualwallet.models.Card;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.enums.CardStatus;
import com.krushkov.virtualwallet.repositories.CardRepository;
import com.krushkov.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asAdmin;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.card;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardServiceImplTest {

    private final CardRepository cardRepository = mock(CardRepository.class);
    private final UserService userService = mock(UserService.class);
    private final CardServiceImpl service = new CardServiceImpl(cardRepository, userService);

    @BeforeEach
    void setUp() {
        asUser(10L);
    }

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void getById_returnsOwnedCardForRegularUser() {
        Card card = card(3L, user(10L), CardStatus.ACTIVE);
        when(cardRepository.findByIdAndUserIdAndIsDeletedFalse(3L, 10L)).thenReturn(Optional.of(card));

        assertThat(service.getById(3L)).isSameAs(card);
    }

    @Test
    void getById_returnsAnyCardForAdmin() {
        clear();
        asAdmin(1L);
        Card card = card(3L, user(10L), CardStatus.ACTIVE);
        when(cardRepository.findByIdAndIsDeletedFalse(3L)).thenReturn(Optional.of(card));

        assertThat(service.getById(3L)).isSameAs(card);
    }

    @Test
    void getById_throwsWhenOwnedCardMissing() {
        when(cardRepository.findByIdAndUserIdAndIsDeletedFalse(3L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(3L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllMyCards_returnsPrincipalCards() {
        List<Card> cards = List.of(card(3L, user(10L), CardStatus.ACTIVE));
        when(cardRepository.findAllByUserIdAndIsDeletedFalse(10L)).thenReturn(cards);

        assertThat(service.getAllMyCards()).isSameAs(cards);
    }

    @Test
    void add_uppercasesHolderAssignsPrincipalUserAndSaves() {
        User owner = user(10L);
        Card card = card(3L, null, CardStatus.ACTIVE);
        card.setCardHolder("test user");
        when(cardRepository.existsByUserIdAndCardSuffixAndIsDeletedFalse(10L, "4242")).thenReturn(false);
        when(userService.getById(10L)).thenReturn(owner);
        when(cardRepository.save(card)).thenReturn(card);

        Card result = service.add(card);

        assertThat(result).isSameAs(card);
        assertThat(card.getCardHolder()).isEqualTo("TEST USER");
        assertThat(card.getUser()).isSameAs(owner);
        verify(cardRepository).save(card);
    }

    @Test
    void add_throwsWhenCardSuffixAlreadyExists() {
        Card card = card(3L, null, CardStatus.ACTIVE);
        when(cardRepository.existsByUserIdAndCardSuffixAndIsDeletedFalse(10L, "4242")).thenReturn(true);

        assertThatThrownBy(() -> service.add(card))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void userDeactivate_setsUserDeactivatedStatus() {
        Card card = card(3L, user(10L), CardStatus.ACTIVE);
        when(cardRepository.findByIdAndUserIdAndIsDeletedFalse(3L, 10L)).thenReturn(Optional.of(card));

        service.deactivate(3L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.USER_DEACTIVATED);
    }

    @Test
    void userActivate_setsActiveStatus() {
        Card card = card(3L, user(10L), CardStatus.USER_DEACTIVATED);
        when(cardRepository.findByIdAndUserIdAndIsDeletedFalse(3L, 10L)).thenReturn(Optional.of(card));

        service.activate(3L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.ACTIVE);
    }

    @Test
    void adminDeactivate_setsAdminDeactivatedStatus() {
        clear();
        asAdmin(1L);
        Card card = card(3L, user(10L), CardStatus.ACTIVE);
        when(cardRepository.findByIdAndIsDeletedFalse(3L)).thenReturn(Optional.of(card));

        service.deactivate(3L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.ADMIN_DEACTIVATED);
    }

    @Test
    void remove_marksOwnedCardDeleted() {
        Card card = card(3L, user(10L), CardStatus.ACTIVE);
        when(cardRepository.findByIdAndUserIdAndIsDeletedFalse(3L, 10L)).thenReturn(Optional.of(card));

        service.remove(3L);

        assertThat(card.isDeleted()).isTrue();
    }
}
