package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.CurrencyMapper;
import com.krushkov.virtualwallet.models.Currency;
import com.krushkov.virtualwallet.models.dtos.requests.currency.CurrencyCreateRequest;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.currency.CurrencyLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.currency.CurrencyShortResponse;
import com.krushkov.virtualwallet.services.contracts.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.CURRENCY_CONTROLLER_NAME, description = ConstantMessages.CURRENCY_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CurrencyMapper currencyMapper;

    @GetMapping
    @Operation(summary = ConstantMessages.CURRENCY_CONTROLLER_GET_ALL_ACTIVE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<List<CurrencyShortResponse>>> getAllActive() {
        List<CurrencyShortResponse> currencyShortResponseList = currencyService.getAllActive().stream()
                .map(currencyMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(currencyShortResponseList);
    }

    @GetMapping("/{targetCurrencyCode}")
    @Operation(summary = ConstantMessages.CURRENCY_CONTROLLER_GET_BY_CODE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<CurrencyLongResponse>> getByCode(
            @Parameter(description = "Currency code", example = "USD")
            @PathVariable String targetCurrencyCode
    ) {
        CurrencyLongResponse currencyLongResponse = currencyMapper
                .toLong(currencyService.getByCode(targetCurrencyCode));

        return ApiResponseFactory.ok(currencyLongResponse);
    }

    @PostMapping
    @Operation(summary = ConstantMessages.CURRENCY_CONTROLLER_CREATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = ConstantMessages.HTTP_CONFLICT),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = ConstantMessages.VALIDATION_ERROR)
    })
    public ResponseEntity<ApiResponse<CurrencyLongResponse>> create(
            @Valid @RequestBody CurrencyCreateRequest request
    ) {
        Currency currency = currencyMapper.fromCreate(request);
        CurrencyLongResponse currencyLongResponse = currencyMapper.toLong(currencyService.create(currency));
        return ApiResponseFactory.ok(ConstantMessages.CURRENCY_CREATE_SUCCESS_MESSAGE, currencyLongResponse);
    }

    @PatchMapping("/{targetCurrencyCode}/activate")
    @Operation(summary = ConstantMessages.CURRENCY_CONTROLLER_ACTIVATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> activate(
            @Parameter(description = "Currency code", example = "USD")
            @PathVariable String targetCurrencyCode
    ) {
        currencyService.activate(targetCurrencyCode);
        return ApiResponseFactory.noContent(ConstantMessages.CURRENCY_ACTIVATE_SUCCESS_MESSAGE);
    }

    @PatchMapping("/{targetCurrencyCode}/deactivate")
    @Operation(summary = ConstantMessages.CURRENCY_CONTROLLER_DEACTIVATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @Parameter(description = "Currency code", example = "USD")
            @PathVariable String targetCurrencyCode
    ) {
        currencyService.deactivate(targetCurrencyCode);
        return ApiResponseFactory.noContent(ConstantMessages.CURRENCY_DEACTIVATE_SUCCESS_MESSAGE);
    }
}
