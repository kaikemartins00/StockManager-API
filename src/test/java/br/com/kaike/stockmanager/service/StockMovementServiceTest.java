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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @InjectMocks
    private StockMovementService movementService;

    @Mock
    private StockMovementRepository movementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext context;

    @Mock
    private UserRepository userRepository;

    private StockMovementRequestDto requestDto;

    private StockMovementResponseDto responseDto;

    private StockMovementEntity movementEntity;

    private ProductEntity productEntity;

    private UserEntity userEntity;


    @BeforeEach
    void setUp() {

        userEntity = UserEntity.builder().id(20L).userName("Kaike Alves").email("kaike@email.com").password("12345678").build();
        productEntity = ProductEntity.builder().sku(78999).productName("Feijao").quantity(0).build();
        movementEntity = StockMovementEntity.builder().id(12L).quantity(200).movementDate(LocalDateTime.of(2026, 6, 12, 15, 30)).product(productEntity).user(userEntity).build();
        requestDto = new StockMovementRequestDto(200);
        responseDto = new StockMovementResponseDto(MovementTypeEnum.ADD, 12L, 78999, "Feijao", 200, movementEntity.getMovementDate(), "kaike@email.com");
       }


    @Test
    @DisplayName("Should convert entity to response DTO successfully")
    void toResponseSuccess() {

        StockMovementResponseDto response = movementService.toResponse(movementEntity);

        assertEquals(movementEntity.getId(), response.id());
        assertEquals(movementEntity.getProduct().getSku(), response.sku());
        assertEquals(movementEntity.getProduct().getProductName(), response.productName());
        assertEquals(movementEntity.getQuantity(), response.quantity());
        assertEquals(movementEntity.getUser().getUsername(), response.userName());

    }


    @Test
    @DisplayName("Should return all movements successfully")
    void findAllMovements() {

        when(movementRepository.findAll()).thenReturn(Collections.singletonList(movementEntity));

        List<StockMovementResponseDto> response = movementService.findAll();

        assertEquals(1, response.size());

        verify(movementRepository).findAll();
        verifyNoMoreInteractions(movementRepository);

    }


    @Test
    @DisplayName("Should find movement by SKU successfully")
    void findByProductSkuSuccess() {

        when(movementRepository.findByProductSku(78999)).thenReturn(Optional.of(Collections.singletonList(movementEntity)));

        List<StockMovementResponseDto> response = movementService.findByProductSku(78999);

        assertEquals(1, response.size());

        verify(movementRepository).findByProductSku(78999);
        verifyNoMoreInteractions(movementRepository);

    }

    @Test
    @DisplayName("Should add movement successfully")
    void toAddSuccess() {

        when(authentication.getName()).thenReturn("kaike@email.com");

        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("kaike@email.com")).thenReturn(Optional.of(userEntity));

        when(productRepository.findById(78999)).thenReturn(Optional.of(productEntity));

        when(movementRepository.save(any(StockMovementEntity.class))).thenReturn(movementEntity);

        StockMovementResponseDto response = movementService.toAdd(78999, requestDto);

        assertEquals(78999, response.sku());
        assertEquals(200, response.quantity());
        assertEquals("kaike@email.com", response.userName());

        verify(productRepository).findById(78999);
        verify(productRepository).save(productEntity);

        verify(movementRepository).save(any(StockMovementEntity.class));

        verify(userRepository).findByEmail("kaike@email.com");

    }



    @Test
    @DisplayName("Should throw exception when product SKU does not exist")
    void toAddError() {

        when(productRepository.findById(78999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> movementService.toAdd(78999, requestDto));

        assertEquals("Product not found with SKU: 78999", exception.getMessage());

        verify(productRepository).findById(78999);

    }

    @Test
    @DisplayName("Should remove stock successfully")
    void removeSuccess() {

        when(authentication.getName()).thenReturn("kaike@email.com");
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("kaike@email.com")).thenReturn(Optional.of(userEntity));

        productEntity.setQuantity(200);

        when(productRepository.findById(78999)).thenReturn(Optional.of(productEntity));
        when(movementRepository.save(any(StockMovementEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockMovementResponseDto response = movementService.remove(78999, requestDto);

        assertEquals(78999, response.sku());
        assertEquals(200, response.quantity());
        assertEquals(MovementTypeEnum.REMOVE, response.movementTypeEnum());
        assertEquals("kaike@email.com", response.userName());

        verify(productRepository).findById(78999);
        verify(productRepository).save(productEntity);
        verify(movementRepository).save(any(StockMovementEntity.class));
        verify(userRepository).findByEmail("kaike@email.com");

    }

    @Test
    @DisplayName("Should throw exception when product SKU does not exist")
    void removeNotFoundError() {

        when(productRepository.findById(78999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> movementService.remove(78999, requestDto));

        assertEquals("Product not found with SKU: 78999", exception.getMessage());

        verify(productRepository).findById(78999);

    }

    @Test
    @DisplayName("Should throw exception when stock is insufficient")
    void removeInsufficientStockError() {
        productEntity.setQuantity(100);

        when(productRepository.findById(78999)).thenReturn(Optional.of(productEntity));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> movementService.remove(78999, requestDto));

        assertEquals("Insufficient stock: 100", exception.getMessage());

        verify(productRepository).findById(78999);

    }

}