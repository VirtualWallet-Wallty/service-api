package com.krushkov.virtualwallet.services.contracts;

import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.dtos.requests.transaction.TransactionFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Page<Transaction> search(TransactionFilterOptions filters, Pageable pageable);

    Transaction getById(Long targetTransactionId);

    Transaction create(Transaction transaction);
}
