package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.TransactionMapper;
import com.krushkov.virtualwallet.models.dtos.requests.transaction.TransactionFilterOptions;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.transaction.TransactionLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.transaction.TransactionShortResponse;
import com.krushkov.virtualwallet.models.enums.TransactionStatus;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.services.contracts.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.TRANSACTION_CONTROLLER_NAME,
        description = ConstantMessages.TRANSACTION_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    @Operation(summary = ConstantMessages.TRANSACTION_CONTROLLER_SEARCH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<Page<TransactionShortResponse>>> search(
            @Parameter(description = "Filter by transaction label", example = "Payment to merchant")
            @RequestParam(required = false) String label,

            @Parameter(description = "Filter by sender user ID", example = "1")
            @RequestParam(required = false) Long senderId,

            @Parameter(description = "Filter by recipient user ID", example = "5")
            @RequestParam(required = false) Long recipientId,

            @Parameter(description = "Filter by sender wallet ID", example = "2")
            @RequestParam(required = false) Long senderWalletId,

            @Parameter(description = "Filter by recipient wallet ID", example = "3")
            @RequestParam(required = false) Long recipientWalletId,

            @Parameter(description = "Filter by transaction type (PAYMENT, TOP_UP, TRANSFER)", example = "PAYMENT")
            @RequestParam(required = false) TransactionType type,

            @Parameter(description = "Filter by transaction status (PENDING, COMPLETED, FAILED)", example = "COMPLETED")
            @RequestParam(required = false) TransactionStatus status,

            @Parameter(description = "Filter by sender currency code", example = "USD")
            @RequestParam(required = false) String senderCurrencyCode,

            @Parameter(description = "Filter by recipient currency code", example = "EUR")
            @RequestParam(required = false) String recipientCurrencyCode,

            @Parameter(description = "Minimum sender amount", example = "10.00")
            @RequestParam(required = false) BigDecimal minSenderAmount,

            @Parameter(description = "Maximum sender amount", example = "500.00")
            @RequestParam(required = false) BigDecimal maxSenderAmount,

            @Parameter(description = "Minimum recipient amount", example = "8.63")
            @RequestParam(required = false) BigDecimal minRecipientAmount,

            @Parameter(description = "Maximum recipient amount", example = "431.50")
            @RequestParam(required = false) BigDecimal maxRecipientAmount,

            @Parameter(description = "Filter by external reference", example = "order-12345")
            @RequestParam(required = false) String externalReference,

            @Parameter(description = "Filter transactions created on or after this date-time", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime createdFrom,

            @Parameter(description = "Filter transactions created on or before this date-time", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime createdTo,

            Pageable pageable
    ) {
        Long userId = PrincipalContext.getId();
        TransactionFilterOptions filters = new TransactionFilterOptions(
                label,
                senderId, recipientId,
                senderWalletId, recipientWalletId,
                type, status,
                senderCurrencyCode, recipientCurrencyCode,
                minSenderAmount, maxSenderAmount,
                minRecipientAmount, maxRecipientAmount,
                externalReference,
                createdFrom, createdTo

        );

        Page<TransactionShortResponse> transactionShortResponsePage = transactionService.search(filters, pageable)
                .map(tx -> transactionMapper.toShort(tx, userId));

        return ApiResponseFactory.ok(transactionShortResponsePage);
    }

    @GetMapping("/{targetTransactionId}")
    @Operation(summary = ConstantMessages.TRANSACTION_CONTROLLER_GET_BY_ID)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<TransactionLongResponse>> getById(
            @Parameter(description = "Transaction ID", example = "100")
            @PathVariable Long targetTransactionId
    ) {
        Long userId = PrincipalContext.getId();
        TransactionLongResponse transactionLongResponse =
                transactionMapper.toLong(transactionService.getById(targetTransactionId), userId);

        return ApiResponseFactory.ok(transactionLongResponse);
    }
}
