package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityDuplicateException;
import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.repositories.CurrencyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asAdmin;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static com.krushkov.virtualwallet.testsupport.TestFixtures.currency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyServiceImplTest {

    private final CurrencyRepository currencyRepository = mock(CurrencyRepository.class);
    private final CurrencyServiceImpl service = new CurrencyServiceImpl(currencyRepository);

    @BeforeEach
    void setUp() {
        asAdmin(1L);
    }

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void getAllActive_returnsActiveCurrencies() {
        List<Currency> currencies = List.of(currency("EUR", 2), currency("USD", 2));
        when(currencyRepository.findByIsActiveTrue()).thenReturn(currencies);

        assertThat(service.getAllActive()).isSameAs(currencies);
    }

    @Test
    void getByCode_normalizesAndReturnsCurrency() {
        Currency eur = currency("EUR", 2);
        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(eur));

        assertThat(service.getByCode(" eur ")).isSameAs(eur);
    }

    @Test
    void getByCode_throwsWhenCurrencyDoesNotExist() {
        when(currencyRepository.findById("EUR")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByCode("eur"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_uppercasesCodeAndSavesWhenUnique() {
        Currency currency = currency("eur", 2);
        when(currencyRepository.existsById("EUR")).thenReturn(false);
        when(currencyRepository.save(currency)).thenReturn(currency);

        Currency result = service.create(currency);

        assertThat(result).isSameAs(currency);
        assertThat(currency.getCode()).isEqualTo("EUR");
        verify(currencyRepository).save(currency);
    }

    @Test
    void create_throwsWhenCodeAlreadyExists() {
        Currency currency = currency("eur", 2);
        when(currencyRepository.existsById("EUR")).thenReturn(true);

        assertThatThrownBy(() -> service.create(currency))
                .isInstanceOf(EntityDuplicateException.class);
    }

    @Test
    void activateAndDeactivateToggleCurrencyState() {
        Currency eur = currency("EUR", 2);
        eur.setActive(false);
        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(eur));

        service.activate("eur");
        assertThat(eur.isActive()).isTrue();

        service.deactivate("eur");
        assertThat(eur.isActive()).isFalse();
        verify(currencyRepository, times(2)).save(eur);
    }
}
