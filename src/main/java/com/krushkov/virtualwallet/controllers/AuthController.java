package com.krushkov.virtualwallet.controllers;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.helpers.factories.ApiResponseFactory;
import com.krushkov.virtualwallet.helpers.mappers.UserMapper;
import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.dtos.requests.auth.LoginRequest;
import com.krushkov.virtualwallet.models.dtos.requests.auth.RegisterRequest;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import com.krushkov.virtualwallet.models.dtos.responses.auth.UserPrincipalResponse;
import com.krushkov.virtualwallet.services.contracts.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = ConstantMessages.AUTH_CONTROLLER_NAME, description = ConstantMessages.AUTH_CONTROLLER_DESCRIPTION)
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = ConstantMessages.INTERNAL_SERVER_ERROR)
})
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = ConstantMessages.AUTH_CONTROLLER_REGISTER)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = ConstantMessages.HTTP_CONFLICT),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422", description = ConstantMessages.VALIDATION_ERROR)
    })
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userMapper.fromRegister(request);
        authService.register(user);
        return ApiResponseFactory.noContent(ConstantMessages.REGISTER_SUCCESS_MESSAGE);
    }

    @PostMapping("/login")
    @Operation(summary = ConstantMessages.AUTH_CONTROLLER_LOGIN)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = ConstantMessages.HTTP_BAD_REQUEST)
    })
    public ResponseEntity<ApiResponse<UserPrincipalResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        UserPrincipalResponse userPrincipal = authService.login(request, response);
        return ApiResponseFactory.ok(ConstantMessages.LOGIN_SUCCESS_MESSAGE, userPrincipal);
    }

    @PostMapping("/logout")
    @Operation(summary = ConstantMessages.AUTH_CONTROLLER_LOGOUT)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR)
    })
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        authService.logout(response);
        return ApiResponseFactory.noContent(ConstantMessages.LOGOUT_SUCCESS_MESSAGE);
    }

    @GetMapping("/me")
    @Operation(summary = ConstantMessages.AUTH_CONTROLLER_GET_ME)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = ConstantMessages.HTTP_OK),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = ConstantMessages.AUTHENTICATION_MISSING_ERROR)
    })
    public ResponseEntity<ApiResponse<UserPrincipalResponse>> getMe() {
        UserPrincipalResponse userPrincipalResponse = authService.getMe();
        return ApiResponseFactory.ok(userPrincipalResponse);
    }
}
