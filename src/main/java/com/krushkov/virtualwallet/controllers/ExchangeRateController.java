package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.ExchangeRateMapper;
import com.krushkov.virtualwallet.models.dtos.responses.ExchangeRateResponse;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.services.contracts.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.EXCHANGE_RATE_CONTROLLER_NAME,
        description = ConstantMessages.EXCHANGE_RATE_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateMapper exchangeRateMapper;

    @GetMapping
    @Operation(summary = ConstantMessages.EXCHANGE_RATE_CONTROLLER_GET_ALL)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<List<ExchangeRateResponse>>> getAllRates() {
        List<ExchangeRateResponse> rates = exchangeRateService.getAllRates().stream()
                .map(exchangeRateMapper::toResponse)
                .toList();

        return ApiResponseFactory.ok(rates);
    }

    @GetMapping("/{baseCurrencyCode}")
    @Operation(summary = ConstantMessages.EXCHANGE_RATE_CONTROLLER_GET_ALL_BY_CURRENCY)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<List<ExchangeRateResponse>>> getRates(
            @Parameter(description = "Base currency code", example = "USD")
            @PathVariable String baseCurrencyCode
    ) {
        List<ExchangeRateResponse> rates = exchangeRateService.getRates(baseCurrencyCode).stream()
                .map(exchangeRateMapper::toResponse)
                .toList();

        return ApiResponseFactory.ok(rates);
    }

    @GetMapping("/{fromCurrencyCode}/{toCurrencyCode}")
    @Operation(summary = ConstantMessages.EXCHANGE_RATE_CONTROLLER_GET_RATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<BigDecimal>> getRate(
            @Parameter(description = "Source currency code", example = "USD")
            @PathVariable String fromCurrencyCode,
            @Parameter(description = "Target currency code", example = "EUR")
            @PathVariable String toCurrencyCode
    ){
        BigDecimal rate = exchangeRateService.getRate(fromCurrencyCode, toCurrencyCode);
        return ApiResponseFactory.ok(rate);
    }

    @PostMapping("/sync/{baseCurrencyCode}")
    @Operation(summary = ConstantMessages.EXCHANGE_RATE_CONTROLLER_SYNC)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> syncRates(
            @Parameter(description = "Base currency code to sync rates for", example = "USD")
            @PathVariable String baseCurrencyCode
    ) {
        exchangeRateService.syncRates(baseCurrencyCode);
        return ApiResponseFactory.noContent(ConstantMessages.EXCHANGE_RATE_SYNC_SUCCESS_MESSAGE);
    }
}
