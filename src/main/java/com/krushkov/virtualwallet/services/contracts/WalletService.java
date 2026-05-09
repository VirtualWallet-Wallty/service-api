package com.krushkov.virtualwallet.services.contracts;

import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletFilterOptions;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    Page<Wallet> search(WalletFilterOptions filters, Pageable pageable);

    List<Wallet> getAllByUserId(Long targetUserId);

    List<Wallet> getMyAll();

    Wallet getById(Long targetWalletId);

    Wallet getByIdAndUserId(Long targetWalletId, Long targetUserId);

    Wallet getMyDefault();

    Wallet getDefault(Long targetUserId);

    Wallet create(Wallet wallet, String currencyCode);

    Wallet update(WalletUpdateRequest request, Long targetWalletId);

    void delete(Long targetWalletId);

    void setDefault(Long targetWalletId);

    void creditMyWallet(Long targetWalletId, BigDecimal amount);

    void creditRecipientWallet(Long targetWalletId, Long recipientId, BigDecimal amount);

    void debit(Long targetWalletId, BigDecimal amount);
}
