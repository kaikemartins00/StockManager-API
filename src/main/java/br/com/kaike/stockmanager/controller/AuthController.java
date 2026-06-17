package br.com.kaike.stockmanager.controller;

import br.com.kaike.stockmanager.dto.auth.LoginRequestDto;
import br.com.kaike.stockmanager.dto.auth.RegisterRequestDto;
import br.com.kaike.stockmanager.dto.auth.TokenResponseDto;
import br.com.kaike.stockmanager.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Authentication", description = "Authentication endpoints and JWT generation")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Method for registering users", description = "User sends requested data to register")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Email already registered")
    public void register(@RequestBody @Valid RegisterRequestDto request) {

        authenticationService.register(request);

    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Method for creating the JWT token", description = "The users sends login details and receives a token to log in")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public TokenResponseDto login(@RequestBody @Valid LoginRequestDto loginDto) {

        return authenticationService.login(loginDto);

    }
}