package br.com.kaike.stockmanager.dto.stock;

import jakarta.validation.constraints.NotNull;

public record StockMovementRequestDto(

        @NotNull(message = "Quantity is required")
        Integer quantity
) {
}
