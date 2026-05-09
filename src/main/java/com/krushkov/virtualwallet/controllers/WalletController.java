package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = ConstantMessages.WALLET_CONTROLLER_NAME, description = ConstantMessages.WALLET_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    @GetMapping
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_SEARCH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<Page<WalletShortResponse>>> search(
            @Parameter(description = "Filter by owner user ID", example = "1")
            @RequestParam(required = false) Long userId,

            @Parameter(description = "Filter by currency code", example = "USD")
            @RequestParam(required = false) String currencyCode,

            @Parameter(description = "Minimum wallet balance", example = "100.00")
            @RequestParam(required = false) BigDecimal minBalance,

            @Parameter(description = "Maximum wallet balance", example = "5000.00")
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

    @GetMapping("/users/{targetUserId}")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_GET_ALL_BY_USER_ID)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<List<WalletShortResponse>>> getAllByUserId(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long targetUserId
    ) {
        List<WalletShortResponse> walletShortResponseList = walletService.getAllByUserId(targetUserId).stream()
                .map(walletMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(walletShortResponseList);
    }

    @GetMapping("/my")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_GET_MY_ALL)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<List<WalletShortResponse>>> getMyAll() {
        List<WalletShortResponse> walletShortResponseList = walletService.getMyAll().stream()
                .map(walletMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(walletShortResponseList);
    }

    @GetMapping("/{targetWalletId}")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_GET_BY_ID)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<WalletLongResponse>> getById(
            @Parameter(description = "Wallet ID", example = "1")
            @PathVariable Long targetWalletId
    ) {
        WalletLongResponse walletLongResponse = walletMapper.toLong(walletService.getById(targetWalletId));
        return ApiResponseFactory.ok(walletLongResponse);
    }

    @GetMapping("/default")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_GET_MY_DEFAULT)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<WalletLongResponse>> getMyDefault() {
        WalletLongResponse walletLongResponse = walletMapper.toLong(walletService.getMyDefault());
        return ApiResponseFactory.ok(walletLongResponse);
    }

    @PostMapping
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_CREATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = ConstantMessages.HTTP_CONFLICT),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = ConstantMessages.VALIDATION_ERROR)
    })
    public ResponseEntity<ApiResponse<WalletLongResponse>> create(@Valid @RequestBody WalletCreateRequest request) {
        Wallet wallet = walletService.create(walletMapper.fromCreate(request), request.currencyCode());
        WalletLongResponse walletLongResponse = walletMapper.toLong(wallet);

        return ApiResponseFactory.ok(ConstantMessages.WALLET_CREATE_SUCCESS_MESSAGE, walletLongResponse);
    }

    @PutMapping("/{targetWalletId}")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_UPDATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = ConstantMessages.VALIDATION_ERROR)
    })
    public ResponseEntity<ApiResponse<WalletLongResponse>> update(
            @Valid @RequestBody WalletUpdateRequest request,
            @Parameter(description = "Wallet ID", example = "1")
            @PathVariable Long targetWalletId
    ) {
        WalletLongResponse walletLongResponse = walletMapper.toLong(walletService.update(request, targetWalletId));
        return ApiResponseFactory.ok(ConstantMessages.WALLET_UPDATE_SUCCESS_MESSAGE, walletLongResponse);
    }

    @DeleteMapping("/{targetWalletId}")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_DELETE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Wallet ID", example = "1")
            @PathVariable Long targetWalletId
    ) {
        walletService.delete(targetWalletId);

        return ApiResponseFactory.noContent(ConstantMessages.WALLET_DELETE_SUCCESS_MESSAGE);
    }

    @PatchMapping("/{targetWalletId}/set-default")
    @Operation(summary = ConstantMessages.WALLET_CONTROLLER_SET_DEFAULT)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @Parameter(description = "Wallet ID", example = "1")
            @PathVariable Long targetWalletId
    ) {
        walletService.setDefault(targetWalletId);

        return ApiResponseFactory.noContent(ConstantMessages.WALLET_SET_DEFAULT_SUCCESS_MESSAGE);
    }
}
