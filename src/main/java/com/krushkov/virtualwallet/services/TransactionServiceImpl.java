package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.dtos.requests.transaction.TransactionFilterOptions;
import com.krushkov.virtualwallet.repositories.TransactionRepository;
import com.krushkov.virtualwallet.repositories.specifications.TransactionSpecifications;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.services.contracts.TransactionService;
import com.krushkov.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> search(TransactionFilterOptions filters, Pageable pageable) {
        if (PrincipalContext.isAdmin()) {
            return adminSearch(filters, pageable);
        }

        return userSearch(filters.withoutUserId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getById(Long transactionId) {
        if (PrincipalContext.isAdmin()) {
            return findTransaction(transactionId);
        }

        return findTransactionOwnedBy(transactionId);
    }

    @Override
    @Transactional
    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    private Page<Transaction> adminSearch(TransactionFilterOptions filters, Pageable pageable) {
        return transactionRepository.findAll(TransactionSpecifications.withFilters(filters), pageable);
    }

    private Page<Transaction> userSearch(TransactionFilterOptions filters, Pageable pageable) {
        Long principalId = PrincipalContext.getId();
        TransactionFilterOptions safeFilters = filters.withoutUserId();

        validateWalletOwnership(safeFilters, principalId);

        return transactionRepository.findAll(
                TransactionSpecifications.principal(principalId)
                        .and(TransactionSpecifications.withFilters(safeFilters)),
                pageable
        );
    }

    private void validateWalletOwnership(TransactionFilterOptions filters, Long principalId) {
        if (filters.senderWalletId() != null) {
            walletService.getByIdAndUserId(filters.senderWalletId(), principalId);
        }

        if (filters.recipientWalletId() != null) {
            walletService.getByIdAndUserId(filters.recipientWalletId(), principalId);
        }
    }

    private Transaction findTransaction(Long targetTransactionId) {
        return transactionRepository.findById(targetTransactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", targetTransactionId));
    }

    private Transaction findTransactionOwnedBy(Long targetTransactionId) {
        Long principalId = PrincipalContext.getId();
        return transactionRepository.findAccessibleById(targetTransactionId, principalId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", targetTransactionId));
    }
}
