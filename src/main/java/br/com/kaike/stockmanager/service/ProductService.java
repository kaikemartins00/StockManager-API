package br.com.kaike.stockmanager.service;

import br.com.kaike.stockmanager.domain.entity.ProductEntity;
import br.com.kaike.stockmanager.domain.repository.ProductRepository;
import br.com.kaike.stockmanager.dto.stock.ProductRequestDto;
import br.com.kaike.stockmanager.dto.stock.ProductResponseDto;
import br.com.kaike.stockmanager.dto.stock.ProductUpdateRequestDto;
import br.com.kaike.stockmanager.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponseDto toResponse(ProductEntity entity) {
        return new ProductResponseDto(entity.getSku(),
                entity.getProductName(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getCategory());
    }

    public List<ProductResponseDto> findAll() {
        return repository.findAll().
                stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponseDto findBySku(Integer sku) {
        ProductEntity productEntity = repository.findById(sku)
                .orElseThrow(() -> new NotFoundException("Product not found with SKU: " + sku));

        return toResponse(productEntity);
    }

    public ProductResponseDto addProduct(ProductRequestDto request) {
        ProductEntity entity = ProductEntity.builder()
                .sku(request.sku())
                .productName(request.name())
                .price(request.price())
                .quantity(0)
                .category(request.category())
                .build();

        ProductEntity saved = repository.save(entity);

        return toResponse(saved);
    }

    public void deleteProduct(Integer sku) {
        try {
            repository.deleteById(sku);

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Cannot delete. Product not found with SKU: " + sku);

        } catch (Exception e) {
            throw new RuntimeException("Database error while deleting product");
        }
    }

    public ProductResponseDto updateProduct(Integer sku, ProductUpdateRequestDto request) {
        try {
            ProductEntity entity = repository.getReferenceById(sku);

            updateData(entity, request);

            entity = repository.save(entity);

            return toResponse(entity);

        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Cannot update. Product not found with SKU: " + sku);
        }
    }

    private void updateData(ProductEntity entity, ProductUpdateRequestDto request) {
        entity.setProductName(request.name());
        entity.setPrice(request.price());
        entity.setCategory(request.category());
    }


}