package br.com.kaike.stockmanager.controller;

import br.com.kaike.stockmanager.domain.enums.CategoryEnum;
import br.com.kaike.stockmanager.dto.stock.ProductRequestDto;
import br.com.kaike.stockmanager.dto.stock.ProductResponseDto;
import br.com.kaike.stockmanager.dto.stock.ProductUpdateRequestDto;
import br.com.kaike.stockmanager.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController controller;

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    private String jsonRequest;

    private String jsonResponse;

    private String jsonUpdateRequest;

    private String url;

    private ProductRequestDto productRequestDto;

    private ProductResponseDto productResponseDto;

    private ProductUpdateRequestDto productUpdateRequestDto;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();
        url = "/v1/products";
        productRequestDto = new ProductRequestDto(789991, "agua sanitaria", new BigDecimal("7.99"), CategoryEnum.CLEANING);
        productResponseDto = new ProductResponseDto(789991, "celular samsung a04", new BigDecimal("7.99"), 200, CategoryEnum.ELECTRONICS);
        productUpdateRequestDto = new ProductUpdateRequestDto("ovos", new BigDecimal("4.99"), CategoryEnum.FOOD);
        jsonRequest = objectMapper.writeValueAsString(productRequestDto);
        jsonResponse = objectMapper.writeValueAsString(productResponseDto);
        jsonUpdateRequest = objectMapper.writeValueAsString(productUpdateRequestDto);
    }

    @Test
    @DisplayName("Should return all products")
    void findAllSuccess() throws Exception {

        when(productService.findAll()).thenReturn(Collections.singletonList(productResponseDto));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService).findAll();
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("Should return product by SKU")
    void findBySkuSuccess() throws Exception {

        Integer id = 1234;

        when(productService.findBySku(id)).thenReturn(productResponseDto);

        mockMvc.perform(get(url + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService).findBySku(id);
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("Should return BadRequest when SKU is invalid")
    void findBySkuInvalid() throws Exception {

        mockMvc.perform(get(url + "/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should save product successfully")
    void saveProductSuccess() throws Exception {

        when(productService.addProduct(productRequestDto)).thenReturn(productResponseDto);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(productService).addProduct(productRequestDto);
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("Should return BadRequest when product request is invalid")
    void notSaveProduct() throws Exception {

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProductSuccess() throws Exception {

        Integer id = 1234;
        when(productService.updateProduct(id, productUpdateRequestDto)).thenReturn(productResponseDto);

        mockMvc.perform(put(url + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService).updateProduct(id, productUpdateRequestDto);
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("Should return BadRequest when update request is invalid")
    void notUpdateProduct() throws Exception {

        Integer id = 1234;

        mockMvc.perform(put(url + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProductSuccess() throws Exception {

        doNothing().when(productService).deleteProduct(productRequestDto.sku());

        mockMvc.perform(delete(url + "/" + productRequestDto.sku())
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productRequestDto.sku());
        verifyNoMoreInteractions(productService);
    }

    @Test
    @DisplayName("Should return BadRequest when SKU is invalid for delete")
    void deleteProductError() throws Exception {

        mockMvc.perform(delete(url + "/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }
}