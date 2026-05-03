package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.WalletMapper;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletCreateRequest;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletFilterOptions;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletUpdateRequest;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.wallet.WalletLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.wallet.WalletShortResponse;
import com.krushkov.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WalletShortResponse>>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            Pageable pageable
    ) {
        WalletFilterOptions filters = new WalletFilterOptions(
                userId,
                currencyCode,
                minBalance, maxBalance
        );

        Page<WalletShortResponse> walletShortResponsePage = walletService.search(filters, pageable)
                .map(walletMapper::toShort);

        return ApiResponseFactory.ok(walletShortResponsePage);
    }

    @GetMapping("/{targetWalletId}")
    public ResponseEntity<ApiResponse<WalletLongResponse>> getById(@PathVariable Long targetWalletId) {
        WalletLongResponse walletLongResponse = walletMapper.toLong(walletService.getById(targetWalletId));
        return ApiResponseFactory.ok(walletLongResponse);
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<WalletLongResponse>> getMyDefault() {
        WalletLongResponse walletLongResponse = walletMapper.toLong(walletService.getMyDefault());
        return ApiResponseFactory.ok(walletLongResponse);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<WalletShortResponse>>> getMyAll() {
        List<WalletShortResponse> walletShortResponseList = walletService.getMyAll().stream()
                .map(walletMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(walletShortResponseList);
    }

    @GetMapping("/users/{targetUserId}")
    public ResponseEntity<ApiResponse<List<WalletShortResponse>>> getAllByUserId(@PathVariable Long targetUserId) {
        List<WalletShortResponse> walletShortResponseList = walletService.getAllByUserId(targetUserId).stream()
                .map(walletMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(walletShortResponseList);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WalletLongResponse>> create(@Valid @RequestBody WalletCreateRequest request) {
        Wallet wallet = walletService.create(walletMapper.fromCreate(request), request.currencyCode());
        WalletLongResponse walletLongResponse = walletMapper.toLong(wallet);

        return ApiResponseFactory.ok("Wallet created successfully.", walletLongResponse);
    }

    @PutMapping("/{targetWalletId}")
    public ResponseEntity<ApiResponse<WalletLongResponse>> update(
            @Valid @RequestBody WalletUpdateRequest request,
            @PathVariable Long targetWalletId
    ) {
        WalletLongResponse walletLongResponse = walletMapper.toLong(walletService.update(request, targetWalletId));
        return ApiResponseFactory.ok("Wallet updated successfully.", walletLongResponse);
    }

    @PatchMapping("/{targetWalletId}/set-default")
    public ResponseEntity<ApiResponse<Void>> setDefault(@PathVariable Long targetWalletId) {
        walletService.setDefault(targetWalletId);

        return ApiResponseFactory.noContent("Wallet set default successfully.");
    }

    @DeleteMapping("/{targetWalletId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long targetWalletId) {
        walletService.delete(targetWalletId);

        return ApiResponseFactory.noContent("Wallet deleted successfully.");
    }
}
