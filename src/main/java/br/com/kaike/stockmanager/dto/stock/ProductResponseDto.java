package br.com.kaike.stockmanager.dto.stock;

import br.com.kaike.stockmanager.domain.enums.CategoryEnum;

import java.math.BigDecimal;

public record ProductResponseDto(

        Integer sku,

        String productName,

        BigDecimal price,

        Integer quantity,

        CategoryEnum category
) {
}