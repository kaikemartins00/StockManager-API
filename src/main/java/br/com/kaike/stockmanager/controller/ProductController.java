package br.com.kaike.stockmanager.controller;

import br.com.kaike.stockmanager.dto.stock.ProductRequestDto;
import br.com.kaike.stockmanager.dto.stock.ProductResponseDto;
import br.com.kaike.stockmanager.dto.stock.ProductUpdateRequestDto;
import br.com.kaike.stockmanager.security.SecurityConfig;
import br.com.kaike.stockmanager.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Products", description = "Product inventory management")
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class ProductController {

    private final ProductService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all products", description = "Returns all registered products ")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public List<ProductResponseDto> findAll() {

        return service.findAll();

    }

    @GetMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find product by SKU", description = "Returns a product by its SKU")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductResponseDto findBySku(@PathVariable Integer sku) {

        return service.findBySku(sku);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create product", description = "Creates a new product in stock")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product data")
    @ApiResponse(responseCode = "409", description = "SKU already exists")
    public ProductResponseDto addProduct(@Valid @RequestBody ProductRequestDto request) {

        return service.addProduct(request);

    }

    @PutMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update product", description = "Updates all product information")
    @ApiResponse(responseCode = "200", description = "Product update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product data")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductResponseDto updateProduct(@PathVariable Integer sku, @Valid @RequestBody ProductUpdateRequestDto request) {

        return service.updateProduct(sku, request);

    }

    @DeleteMapping("/{sku}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product", description = "Removes a product from stock")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public void deleteProduct(@PathVariable Integer sku) {

        service.deleteProduct(sku);

    }
}