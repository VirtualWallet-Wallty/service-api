package com.krushkov.virtualwallet.testsupport;

import com.krushkov.virtualwallet.models.Card;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.Role;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.enums.CardStatus;
import com.krushkov.virtualwallet.models.enums.RoleType;

import java.math.BigDecimal;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static Currency currency(String code, int decimals) {
        Currency currency = new Currency();
        currency.setCode(code);
        currency.setName(code + " currency");
        currency.setSymbol(code.substring(0, 1));
        currency.setDecimals(decimals);
        currency.setActive(true);
        return currency;
    }

    public static User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        user.setPassword("password");
        user.setEmail("user" + id + "@example.com");
        user.setPhoneNumber("0880000" + id);
        user.setRole(role(RoleType.USER));
        return user;
    }

    public static Role role(RoleType roleType) {
        Role role = new Role();
        role.setName(roleType);
        return role;
    }

    public static Wallet wallet(Long id, User user, Currency currency, BigDecimal balance, boolean isDefault) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setName("Wallet " + id);
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(balance);
        wallet.setDefault(isDefault);
        return wallet;
    }

    public static Card card(Long id, User user, CardStatus status) {
        Card card = new Card();
        card.setId(id);
        card.setUser(user);
        card.setCardHolder("TEST USER");
        card.setCardSuffix("4242");
        card.setExpirationMonth(12);
        card.setExpirationYear(2030);
        card.setStatus(status);
        return card;
    }
}
