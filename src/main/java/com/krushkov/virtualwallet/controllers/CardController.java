package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.CardMapper;
import com.krushkov.virtualwallet.models.Card;
import com.krushkov.virtualwallet.models.dtos.requests.card.CardCreateRequest;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.card.CardLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.card.CardShortResponse;
import com.krushkov.virtualwallet.services.contracts.CardService;
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
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.CARD_CONTROLLER_NAME, description = ConstantMessages.CARD_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class CardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @GetMapping("/users/{targetUserId}")
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_GET_ALL_BY_USER_ID)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<List<CardShortResponse>>> getAllByUserId(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long targetUserId
    ) {
        List<CardShortResponse> cardShortResponseList = cardService.getAllByUserId(targetUserId).stream()
                .map(cardMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(cardShortResponseList);
    }

    @GetMapping("/my")
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_GET_MY_ALL)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<List<CardShortResponse>>> getMyAll() {
        List<CardShortResponse> cardShortResponseList = cardService.getAllMyCards().stream()
                .map(cardMapper::toShort)
                .toList();

        return ApiResponseFactory.ok(cardShortResponseList);
    }

    @GetMapping("/{targetCardId}")
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_GET_BY_ID)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<CardLongResponse>> getById(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable Long targetCardId
    ) {
        CardLongResponse cardLongResponse = cardMapper.toLong(cardService.getById(targetCardId));
        return ApiResponseFactory.ok(cardLongResponse);
    }

    @PostMapping
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_ADD)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = ConstantMessages.HTTP_CONFLICT),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = ConstantMessages.VALIDATION_ERROR)
    })
    public ResponseEntity<ApiResponse<CardLongResponse>> add(@Valid @RequestBody CardCreateRequest request) {
        Card card = cardMapper.fromCreate(request);
        CardLongResponse cardLongResponse = cardMapper.toLong(cardService.add(card));
        return ApiResponseFactory.ok(ConstantMessages.CARD_ADDED_SUCCESSFULLY_MESSAGE, cardLongResponse);
    }

    @DeleteMapping("/{targetCardId}")
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_REMOVE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> remove(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable Long targetCardId
    ) {
        cardService.remove(targetCardId);
        return ApiResponseFactory.noContent(ConstantMessages.CARD_REMOVED_SUCCESSFULLY_MESSAGE);
    }

    @PatchMapping("/{targetCardId}/activate")
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_ACTIVATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> activate(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable Long targetCardId
    ) {
        cardService.activate(targetCardId);
        return ApiResponseFactory.noContent(ConstantMessages.CARD_ACTIVATED_SUCCESSFULLY_MESSAGE);
    }

    @PatchMapping("/{targetCardId}/deactivate")
    @Operation(summary = ConstantMessages.CARD_CONTROLLER_DEACTIVATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable Long targetCardId
    ) {
        cardService.deactivate(targetCardId);
        return ApiResponseFactory.noContent(ConstantMessages.CARD_DEACTIVATED_SUCCESSFULLY_MESSAGE);
    }
}
