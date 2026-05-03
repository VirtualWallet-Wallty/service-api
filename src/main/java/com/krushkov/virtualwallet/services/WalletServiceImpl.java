package com.krushkov.virtualwallet.services;

import com.krushkov.virtualwallet.exceptions.EntityNotFoundException;
import com.krushkov.virtualwallet.helpers.mappers.WalletMapper;
import com.krushkov.virtualwallet.helpers.validations.TransactionValidations;
import com.krushkov.virtualwallet.helpers.validations.UserValidations;
import com.krushkov.virtualwallet.helpers.validations.WalletValidations;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletFilterOptions;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletUpdateRequest;
import com.krushkov.virtualwallet.repositories.UserRepository;
import com.krushkov.virtualwallet.repositories.WalletRepository;
import com.krushkov.virtualwallet.repositories.specifications.WalletSpecifications;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.services.contracts.CurrencyService;
import com.krushkov.virtualwallet.services.contracts.UserService;
import com.krushkov.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Wallet> search(WalletFilterOptions filters, Pageable pageable) {
        UserValidations.validateIsAdmin();
        return walletRepository.findAll(WalletSpecifications.withFilters(filters), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getById(Long targetWalletId) {
        if (PrincipalContext.isAdmin()) {
            return findWallet(targetWalletId);
        }

        return findWalletOwnedBy(targetWalletId);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getByIdAndUserId(Long targetWalletId, Long userId) {
        return walletRepository.findByIdAndUserIdAndIsDeletedFalse(targetWalletId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", targetWalletId));
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getDefault(Long userId) {
        return walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "default", "user id = " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getMyDefault() {
        UserValidations.validateIsNotAdmin();
        Long principalId = PrincipalContext.getId();
        return walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(principalId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "default", "user id = " + principalId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getMyAll() {
        UserValidations.validateIsNotAdmin();
        return walletRepository.findAllByUserIdAndIsDeletedFalse(PrincipalContext.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getAllByUserId(Long userId) {
        UserValidations.validateIsAdmin();
        return walletRepository.findAllByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    @Transactional
    public Wallet create(Wallet wallet, String currencyCode) {
        UserValidations.validateIsNotAdmin();

        Long principalId = PrincipalContext.getId();

        WalletValidations.validateMaxWalletCount(walletRepository, principalId);
        WalletValidations.validateNameExists(walletRepository, wallet.getName(), principalId);

        User user = userService.getById(principalId);
        Currency currency = currencyService.getByCode(currencyCode);

        wallet.setUser(user);
        wallet.setCurrency(currency);

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet update(WalletUpdateRequest request, Long targetWalletId) {
        UserValidations.validateIsNotAdmin();

        Wallet targetWallet = getById(targetWalletId);
        walletMapper.update(targetWallet, request);

        walletRepository.save(targetWallet);
        return targetWallet;
    }

    @Override
    @Transactional
    public void delete(Long targetWalletId) {
        UserValidations.validateIsNotAdmin();
        Long principalId = PrincipalContext.getId();

        Wallet targetWallet = getByIdAndUserId(targetWalletId, principalId);
        WalletValidations.validateMinWalletCount(walletRepository, principalId);

        WalletValidations.validateAlreadyDefault(targetWallet);
        WalletValidations.validateBalanceEmpty(targetWallet);

        targetWallet.setDeleted(true);
    }

    @Override
    @Transactional
    public void setDefault(Long targetWalletId) {
        UserValidations.validateIsNotAdmin();
        Long principalId = PrincipalContext.getId();
        Wallet targetWallet = getByIdAndUserId(targetWalletId, principalId);

        WalletValidations.validateAlreadyDefault(targetWallet);

        walletRepository.findByUserIdAndIsDefaultTrueAndIsDeletedFalse(principalId)
                .ifPresent(w -> w.setDefault(false));

        targetWallet.setDefault(true);
    }

    @Override
    @Transactional
    public void creditMyWallet(Long targetWalletId, BigDecimal amount) {
        UserValidations.validateIsNotAdmin();
        UserValidations.validateNotBlocked();

        Wallet targetWallet = lockWallet(targetWalletId);

        TransactionValidations.validateAmount(amount);

        targetWallet.setBalance(targetWallet.getBalance().add(amount));
    }

    @Override
    @Transactional
    public void creditRecipientWallet(Long targetWalletId, Long recipientId, BigDecimal amount) {
        UserValidations.validateIsNotAdmin();
        UserValidations.validateRecipientNotBlocked(userRepository, recipientId);
        UserValidations.validateRecipientNotAdmin(userRepository, recipientId);

        Wallet targetWallet = lockWalletOwnedByUser(targetWalletId, recipientId);

        TransactionValidations.validateAmount(amount);

        targetWallet.setBalance(targetWallet.getBalance().add(amount));
    }

    @Override
    @Transactional
    public void debit(Long targetWalletId, BigDecimal amount) {
        UserValidations.validateIsNotAdmin();
        UserValidations.validateNotBlocked();

        Wallet targetWallet = lockWallet(targetWalletId);

        TransactionValidations.validateAmount(amount);
        TransactionValidations.validateSufficientFunds(targetWallet, amount);

        targetWallet.setBalance(targetWallet.getBalance().subtract(amount));
    }

    private Wallet findWallet(Long targetWalletId) {
        return walletRepository.findByIdAndIsDeletedFalse(targetWalletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", targetWalletId));
    }

    private Wallet findWalletOwnedBy(Long targetWalletId) {
        Long principalId = PrincipalContext.getId();
        return walletRepository.findByIdAndUserIdAndIsDeletedFalse(targetWalletId, principalId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", targetWalletId));
    }

    private Wallet lockWalletOwnedByUser(Long targetWalletId, Long recipientId) {
        return walletRepository.findWithLockByIdAndUserIdAndIsDeletedFalse(targetWalletId, recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", targetWalletId));
    }

    private Wallet lockWallet(Long targetWalletId) {
        Long principalId = PrincipalContext.getId();
        return walletRepository.findWithLockByIdAndUserIdAndIsDeletedFalse(targetWalletId, principalId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", targetWalletId));
    }
}
