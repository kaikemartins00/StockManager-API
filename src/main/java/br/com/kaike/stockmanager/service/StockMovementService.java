package br.com.kaike.stockmanager.service;


import br.com.kaike.stockmanager.domain.entity.ProductEntity;
import br.com.kaike.stockmanager.domain.entity.StockMovementEntity;
import br.com.kaike.stockmanager.domain.entity.UserEntity;
import br.com.kaike.stockmanager.domain.enums.MovementTypeEnum;
import br.com.kaike.stockmanager.domain.repository.ProductRepository;
import br.com.kaike.stockmanager.domain.repository.StockMovementRepository;
import br.com.kaike.stockmanager.domain.repository.UserRepository;
import br.com.kaike.stockmanager.dto.stock.StockMovementRequestDto;
import br.com.kaike.stockmanager.dto.stock.StockMovementResponseDto;
import br.com.kaike.stockmanager.exception.BadRequestException;
import br.com.kaike.stockmanager.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public StockMovementResponseDto toResponse(StockMovementEntity entity) {
        return new StockMovementResponseDto(
                entity.getMovementTypeEnum(),
                entity.getId(),
                entity.getProduct().getSku(),
                entity.getProduct().getProductName(),
                entity.getQuantity(),
                entity.getMovementDate(),
                entity.getUser().getUsername()
        );
    }

    public List<StockMovementResponseDto> findAll() {
        List<StockMovementEntity> movementsEntity = movementRepository.findAll();
        return movementsEntity.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StockMovementResponseDto> findByProductSku(Integer sku) {
        List<StockMovementEntity> movements = movementRepository.findByProductSku(sku)
                .orElseThrow(() -> new NotFoundException("Product not found with SKU: " + sku));

        return movements.stream()
                .map(this::toResponse)
                .toList();
    }

    public StockMovementResponseDto toAdd(Integer sku, StockMovementRequestDto request) {
        ProductEntity productEntity = productRepository.findById(sku)
                .orElseThrow(() -> new NotFoundException("Product not found with SKU: " + sku));

        productEntity.setQuantity(productEntity.getQuantity() + request.quantity());

        productRepository.save(productEntity);

        StockMovementEntity stockMovementEntity = new StockMovementEntity();

        stockMovementEntity.setProduct(productEntity);
        stockMovementEntity.setMovementTypeEnum(MovementTypeEnum.ADD);
        stockMovementEntity.setQuantity(request.quantity());
        stockMovementEntity.setMovementDate(LocalDateTime.now());
        stockMovementEntity.setUser(getAuthenticatedUser());

        StockMovementEntity saved = movementRepository.save(stockMovementEntity);
        return toResponse(saved);


    }

    public StockMovementResponseDto remove(Integer sku, StockMovementRequestDto request) {
        ProductEntity productEntity = productRepository.findById(sku)
                .orElseThrow(() -> new NotFoundException("Product not found with SKU: " + sku));

        if (productEntity.getQuantity() >= request.quantity()) {

            System.out.println("Remove");

            productEntity.setQuantity(productEntity.getQuantity() - request.quantity());

            productRepository.save(productEntity);

            StockMovementEntity stockMovementEntity = new StockMovementEntity();
            stockMovementEntity.setMovementTypeEnum(MovementTypeEnum.REMOVE);
            stockMovementEntity.setProduct(productEntity);
            stockMovementEntity.setQuantity(request.quantity());
            stockMovementEntity.setMovementDate(LocalDateTime.now());
            stockMovementEntity.setUser(getAuthenticatedUser());

            StockMovementEntity saved = movementRepository.save(stockMovementEntity);
            return toResponse(saved);

        } else {
            throw new BadRequestException("Insufficient stock: " + productEntity.getQuantity());
        }
    }

    private UserEntity getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("User not found"));
    }
}