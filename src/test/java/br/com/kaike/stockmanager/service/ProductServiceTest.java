package br.com.kaike.stockmanager.service;

import br.com.kaike.stockmanager.domain.entity.ProductEntity;
import br.com.kaike.stockmanager.domain.enums.CategoryEnum;
import br.com.kaike.stockmanager.domain.repository.ProductRepository;
import br.com.kaike.stockmanager.dto.stock.ProductRequestDto;
import br.com.kaike.stockmanager.dto.stock.ProductResponseDto;
import br.com.kaike.stockmanager.dto.stock.ProductUpdateRequestDto;
import br.com.kaike.stockmanager.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private ProductRequestDto productRequestDto;

    private ProductResponseDto productResponseDto;

    private ProductUpdateRequestDto productUpdateRequestDto;

    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        productEntity = ProductEntity.builder().sku(789991).productName("chocolate").price(new BigDecimal("7.99")).quantity(200).build();
        productResponseDto = new ProductResponseDto(789992, "celular samsung a04", new BigDecimal("7.99"), 200, CategoryEnum.ELECTRONICS);
        productRequestDto = new ProductRequestDto(789993, "agua sanitaria", new BigDecimal("7.99"), CategoryEnum.CLEANING);
        productUpdateRequestDto = new ProductUpdateRequestDto("ovos", new BigDecimal("4.99"), CategoryEnum.FOOD);

    }

    @Test
    @DisplayName("Should convert ProductEntity to ProductResponseDto")
    void toResponseSuccess() {

        ProductResponseDto responseDto = productService.toResponse(productEntity);

        assertEquals(productEntity.getSku(), responseDto.sku());
        assertEquals(productEntity.getProductName(), responseDto.productName());
        assertEquals(productEntity.getPrice(), responseDto.price());
        assertEquals(productEntity.getQuantity(), responseDto.quantity());
    }

    @Test
    @DisplayName("Should return all products")
    void findAllProducts() {

        when(productRepository.findAll()).thenReturn(Collections.singletonList(productEntity));
        List<ProductResponseDto> productResponseDto = productService.findAll();

        assertEquals(1, productResponseDto.size());

    }

    @Test
    @DisplayName("Should return product by SKU")
    void findBySkuSuccess() {

        Integer skuTest = productRequestDto.sku();

        when(productRepository.findById(skuTest)).thenReturn(Optional.of(productEntity));

        ProductResponseDto bySku = productService.findBySku(skuTest);


        assertEquals(productEntity.getSku(), bySku.sku());
        assertEquals(productEntity.getProductName(), bySku.productName());

        verify(productRepository).findById(skuTest);
        verifyNoMoreInteractions(productRepository);

    }

    @Test
    @DisplayName("Should throw NotFoundException when SKU does not exist")
    void findBySkuNotFoundError() {

        when(productRepository.findById(productEntity.getSku())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> productService.findBySku(productEntity.getSku()));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Product not found with SKU: " + productEntity.getSku()));

        verify(productRepository).findById(productEntity.getSku());
    }

    @Test
    @DisplayName("Should save product successfully")
    void saveProductSuccess() {

        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductResponseDto response = productService.addProduct(productRequestDto);

        assertEquals(response.sku(), response.sku());
        assertEquals("chocolate", response.productName());
        assertEquals(new BigDecimal("7.99"), response.price());
        assertEquals(200, response.quantity());

        verify(productRepository).save(any(ProductEntity.class));

    }

    @Test
    @DisplayName("Should throw exception when save product fails")
    void saveProductError() {

        when(productRepository.save(any(ProductEntity.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.addProduct(productRequestDto));

        assertEquals("Database error", exception.getMessage());

        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProductSuccess() {

        when(productRepository.getReferenceById(productEntity.getSku())).thenReturn(productEntity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductResponseDto response = productService.updateProduct(productEntity.getSku(), productUpdateRequestDto);

        assertEquals(productEntity.getSku(), response.sku());
        assertEquals(new BigDecimal("4.99"), response.price());
        assertEquals(200, response.quantity());

        verify(productRepository).getReferenceById(productEntity.getSku());
        verify(productRepository).save(any(ProductEntity.class));
        verifyNoMoreInteractions(productRepository);

    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent product")
    void updateProductNotFoundError() {

        when(productRepository.getReferenceById(productEntity.getSku())).thenThrow(new NotFoundException("Cannot update. Product not found with SKU: " + productEntity.getSku()));

        RuntimeException exception = assertThrows(NotFoundException.class, () -> productService.updateProduct(productEntity.getSku(), productUpdateRequestDto));

        assertEquals("Cannot update. Product not found with SKU: " + productEntity.getSku(), exception.getMessage());

        verify(productRepository).getReferenceById(productEntity.getSku());
    }


    @Test
    @DisplayName("Should delete product successfully")
    void deleteProductSuccess() {

        doNothing().when(productRepository).deleteById(productEntity.getSku());

        productService.deleteProduct(productEntity.getSku());

        verify(productRepository).deleteById(productEntity.getSku());
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent product")
    void deleteProductNotFoundError() {

        doThrow(new EmptyResultDataAccessException(1)).when(productRepository).deleteById(productEntity.getSku());

        RuntimeException exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(productEntity.getSku()));

        assertEquals("Cannot delete. Product not found with SKU: " + productEntity.getSku(), exception.getMessage());

        verify(productRepository).deleteById(productEntity.getSku());
    }

    @Test
    @DisplayName("Should throw RuntimeException when database error occurs during delete")
    void deleteProductDatabaseError() {

        doThrow(new RuntimeException("Database Error")).when(productRepository).deleteById(productEntity.getSku());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(productEntity.getSku()));

        assertEquals("Database error while deleting product", exception.getMessage());

        verify(productRepository).deleteById(productEntity.getSku());
    }
}