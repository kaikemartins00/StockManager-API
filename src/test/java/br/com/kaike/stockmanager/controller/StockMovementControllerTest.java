package br.com.kaike.stockmanager.controller;

import br.com.kaike.stockmanager.domain.enums.MovementTypeEnum;
import br.com.kaike.stockmanager.dto.stock.StockMovementRequestDto;
import br.com.kaike.stockmanager.dto.stock.StockMovementResponseDto;
import br.com.kaike.stockmanager.service.StockMovementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StockMovementControllerTest {

    @InjectMocks
    private StockMovementController controller;

    @Mock
    private StockMovementService service;

    private MockMvc mockMvc;

    private String jsonRequest;

    private String url;

    private StockMovementRequestDto requestDto;

    private StockMovementResponseDto responseDto;

    private StockMovementResponseDto responseAddDto;

    private StockMovementResponseDto responseRemoveDto;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();
        url = "/v1/movements";
        requestDto = new StockMovementRequestDto( 200);
        responseDto = new StockMovementResponseDto(MovementTypeEnum.ADD, 17L, 78999, "Feijao", 200, LocalDateTime.of(2026, 6, 12, 15, 30, 0), "kaike@email.com");
        responseAddDto = new StockMovementResponseDto(MovementTypeEnum.ADD, 17L, 78999, "Feijao", 200, LocalDateTime.of(2026, 6, 12, 15, 30, 0), "kaike@email.com");
        responseRemoveDto = new StockMovementResponseDto(MovementTypeEnum.REMOVE, 17L, 78999, "Feijao", 200, LocalDateTime.of(2026, 6, 12, 15, 30, 0), "kaike@email.com");
        jsonRequest = objectMapper.writeValueAsString(requestDto);

    }

    @Test
    @DisplayName("Should return all stock movements successfully")
    void findAllSuccessfully() throws Exception {

        when(service.findAll()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Should return stock movements by product SKU successfully")
    void findByProductSkuSuccessfully() throws Exception {

        when(service.findByProductSku(responseDto.sku())).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get(url + "/historic/" + responseDto.sku())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).findByProductSku(responseDto.sku());
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Should return BadRequest when SKU format is invalid")
    void findByProductSkuInvalid() throws Exception {

        mockMvc.perform(get(url + "/historic/" + "abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Should increase product stock successfully")
    void toAddProductSuccessfully() throws Exception {

        when(service.toAdd(78999, requestDto)).thenReturn(responseAddDto);

        mockMvc.perform(post(url + "/toAdd/" + 78999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).toAdd(78999, requestDto);
        verifyNoMoreInteractions(service);

    }

    @Test
    @DisplayName("Should return BadRequest when request body is missing or invalid")
    void toAddProductError() throws Exception {

        mockMvc.perform(post(url + "/toAdd/" + 78999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);

    }

    @Test
    @DisplayName("Should remove the desired quantity from product SKU successfully")
    void removeProductSuccessfully() throws Exception {

        when(service.remove(78999, requestDto)).thenReturn(responseRemoveDto);

        mockMvc.perform(post(url + "/remove/" + 78999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).remove(78999, requestDto);
        verifyNoMoreInteractions(service);

    }

    @Test
    @DisplayName("Should return BadRequest when quantity is greater than available stock")
    void removeProductError() throws Exception {

        mockMvc.perform(post(url + "/remove/" + 78999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);

    }
}