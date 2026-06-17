package br.com.kaike.stockmanager.dto.auth;

public record TokenResponseDto(
        String token,
        long expiresIn
) {
}