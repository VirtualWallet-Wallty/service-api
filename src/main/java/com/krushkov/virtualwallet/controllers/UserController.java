package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.UserMapper;
import com.krushkov.virtualwallet.models.dtos.requests.user.UserFilterOptions;
import com.krushkov.virtualwallet.models.dtos.requests.user.UserUpdateRequest;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.user.UserLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.user.UserShortResponse;
import com.krushkov.virtualwallet.services.contracts.UserService;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.USER_CONTROLLER_NAME, description = ConstantMessages.USER_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @Operation(summary = ConstantMessages.USER_CONTROLLER_SEARCH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK)
    })
    public ResponseEntity<ApiResponse<Page<UserShortResponse>>> search(
            @Parameter(description = "Filter by username", example = "johndoe")
            @RequestParam(required = false) String username,

            @Parameter(description = "Filter by first name", example = "John")
            @RequestParam(required = false) String firstName,

            @Parameter(description = "Filter by last name", example = "Doe")
            @RequestParam(required = false) String lastName,

            @Parameter(description = "Filter by email address", example = "john.doe@example.com")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filter by phone number", example = "0888123456")
            @RequestParam(required = false) String phoneNumber,

            @Parameter(description = "Filter by blocked status", example = "false")
            @RequestParam(required = false) boolean isBlocked,

            @Parameter(description = "Filter users created on or after this date-time", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime createdFrom,

            @Parameter(description = "Filter users created on or before this date-time", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime createdTo,

            Pageable pageable
    ) {
        UserFilterOptions filters = new UserFilterOptions(
                username, firstName, lastName,
                email, phoneNumber, isBlocked,
                createdFrom, createdTo
        );

        Page<UserShortResponse> userShortResponsePage = userService.search(filters, pageable)
                .map(userMapper::toShort);

        return ApiResponseFactory.ok(userShortResponsePage);
    }

    @GetMapping("/{targetUserId}")
    @Operation(summary = ConstantMessages.USER_CONTROLLER_GET_BY_ID)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    public ResponseEntity<ApiResponse<UserLongResponse>> getById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long targetUserId
    ) {
        UserLongResponse userLongResponse = userMapper.toLong(userService.getById(targetUserId));
        return ApiResponseFactory.ok(userLongResponse);
    }

    @PutMapping
    @Operation(summary = ConstantMessages.USER_CONTROLLER_UPDATE)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = ConstantMessages.VALIDATION_ERROR)
    })
    public ResponseEntity<ApiResponse<UserLongResponse>> update(@Valid @RequestBody UserUpdateRequest request) {
        UserLongResponse userLongResponse = userMapper.toLong(userService.update(request));
        return ApiResponseFactory.ok(ConstantMessages.USER_UPDATE_SUCCESS_MESSAGE, userLongResponse);
    }

    @Operation(summary = ConstantMessages.USER_CONTROLLER_BLOCK)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    @PatchMapping("/{targetUserId}/block")
    public ResponseEntity<ApiResponse<Void>> block(
            @Parameter(description = "User ID to block", example = "1")
            @PathVariable Long targetUserId
    ) {
        userService.block(targetUserId);
        return ApiResponseFactory.noContent(ConstantMessages.USER_BLOCK_SUCCESS_MESSAGE);
    }

    @Operation(summary = ConstantMessages.USER_CONTROLLER_UNBLOCK)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = ConstantMessages.HTTP_NOT_FOUND)
    })
    @PatchMapping("/{targetUserId}/unblock")
    public ResponseEntity<ApiResponse<Void>> unblock(
            @Parameter(description = "User ID to unblock", example = "1")
            @PathVariable Long targetUserId
    ) {
        userService.unblock(targetUserId);
        return ApiResponseFactory.noContent(ConstantMessages.USER_UNBLOCK_SUCCESS_MESSAGE);
    }
}
