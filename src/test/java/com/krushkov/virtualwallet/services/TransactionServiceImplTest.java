package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.dtos.requests.transaction.TransactionFilterOptions;
import com.krushkov.virtualwallet.repositories.TransactionRepository;
import com.krushkov.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asAdmin;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.asUser;
import static com.krushkov.virtualwallet.testsupport.TestAuthentication.clear;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceImplTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final WalletService walletService = mock(WalletService.class);
    private final TransactionServiceImpl service = new TransactionServiceImpl(transactionRepository, walletService);

    @BeforeEach
    void setUp() {
        asUser(10L);
    }

    @AfterEach
    void tearDown() {
        clear();
    }

    @Test
    void getById_returnsAccessibleTransactionForRegularUser() {
        Transaction transaction = new Transaction();
        when(transactionRepository.findAccessibleById(99L, 10L)).thenReturn(Optional.of(transaction));

        assertThat(service.getById(99L)).isSameAs(transaction);
    }

    @Test
    void getById_returnsAnyTransactionForAdmin() {
        clear();
        asAdmin(1L);
        Transaction transaction = new Transaction();
        when(transactionRepository.findById(99L)).thenReturn(Optional.of(transaction));

        assertThat(service.getById(99L)).isSameAs(transaction);
    }

    @Test
    void getById_throwsWhenAccessibleTransactionMissing() {
        when(transactionRepository.findAccessibleById(99L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_forAdminUsesFiltersDirectly() {
        clear();
        asAdmin(1L);
        TransactionFilterOptions filters = emptyFilters();
        Pageable pageable = Pageable.unpaged();
        Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertThat(service.search(filters, pageable)).isSameAs(page);
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_forRegularUserValidatesRequestedWalletOwnership() {
        TransactionFilterOptions filters = new TransactionFilterOptions(
                null,
                20L,
                30L,
                7L,
                8L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Pageable pageable = Pageable.unpaged();
        Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertThat(service.search(filters, pageable)).isSameAs(page);
        verify(walletService).getByIdAndUserId(7L, 10L);
        verify(walletService).getByIdAndUserId(8L, 10L);
    }

    @Test
    void create_savesTransaction() {
        Transaction transaction = new Transaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        assertThat(service.create(transaction)).isSameAs(transaction);
    }

    private TransactionFilterOptions emptyFilters() {
        return new TransactionFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
