package br.com.kaike.stockmanager.dto.stock;

import br.com.kaike.stockmanager.domain.enums.MovementTypeEnum;

import java.time.LocalDateTime;

public record StockMovementResponseDto(
        MovementTypeEnum movementTypeEnum,
        Long id,
        Integer sku,
        String productName,
        Integer quantity,
        LocalDateTime movementDate,
        String userName
) {
}
