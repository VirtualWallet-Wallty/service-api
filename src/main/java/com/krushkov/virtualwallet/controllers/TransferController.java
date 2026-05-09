package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.TransactionMapper;
import com.krushkov.virtualwallet.models.dtos.requests.TransferRequest;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.transaction.TransactionLongResponse;
import com.krushkov.virtualwallet.security.auth.PrincipalContext;
import com.krushkov.virtualwallet.services.contracts.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.TRANSFER_CONTROLLER_NAME, description = ConstantMessages.TRANSFER_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class TransferController {

    private final TransactionMapper transactionMapper;
    private final TransferService transferService;

    @PostMapping
    @Operation(summary = ConstantMessages.TRANSFER_CONTROLLER_TRANSFER)
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
    public ResponseEntity<ApiResponse<TransactionLongResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        Long userId = PrincipalContext.getId();

        TransactionLongResponse transactionLongResponse = transactionMapper
                .toLong(transferService.transfer(request), userId);

        return ApiResponseFactory.ok(ConstantMessages.TRANSFER_SUCCESS_MESSAGE, transactionLongResponse);
    }
}
