package br.com.kaike.stockmanager.dto.stock;

import br.com.kaike.stockmanager.domain.enums.CategoryEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record ProductRequestDto(

        @NotNull(message = "Sku is required")
        Integer sku,

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Price is required")
        BigDecimal price,

        @NotNull(message = "Category is required")
        CategoryEnum category

) {
}