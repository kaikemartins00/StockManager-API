package br.com.kaike.stockmanager.controller;

import br.com.kaike.stockmanager.dto.stock.StockMovementRequestDto;
import br.com.kaike.stockmanager.dto.stock.StockMovementResponseDto;
import br.com.kaike.stockmanager.security.SecurityConfig;
import br.com.kaike.stockmanager.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/movements")
@RequiredArgsConstructor
@Tag(name = "MovementController")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class StockMovementController {

    private final StockMovementService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all stock movements", description = "Returns the complete history of stock entries and exits")
    @ApiResponse(responseCode = "200", description = "Movements history retrieved successfully")
    public List<StockMovementResponseDto> findAll() {

        return service.findAll();

    }

    @GetMapping("/historic/{sku}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find stock movement by product SKU", description = "Returns the stock movement history for a specific product")
    @ApiResponse(responseCode = "200", description = "Product stock history found")
    @ApiResponse(responseCode = "404", description = "Product not found in movements history")
    public List<StockMovementResponseDto> findByProductSku(@Valid @PathVariable Integer sku) {

        return service.findByProductSku(sku);

    }

    @PostMapping("/toAdd/{sku}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add stock entry", description = "Increments the quantity of an existing product in stock")
    @ApiResponse(responseCode = "201", description = "Stock entry processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public StockMovementResponseDto toAdd(@PathVariable Integer sku, @Valid @RequestBody StockMovementRequestDto request) {

        return service.toAdd(sku, request);

    }

    @PostMapping("/remove/{sku}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Remove stock exit", description = "Decrements the quantity of an existing product from stock")
    @ApiResponse(responseCode = "201", description = "Stock exit processed successfully")
    @ApiResponse(responseCode = "400", description = "Insufficient stock or invalid data")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public StockMovementResponseDto remove(@PathVariable Integer sku, @Valid @RequestBody StockMovementRequestDto request) {

        return service.remove(sku, request);

    }

}