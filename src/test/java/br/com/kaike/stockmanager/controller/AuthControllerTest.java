package br.com.kaike.stockmanager.controller;

import br.com.kaike.stockmanager.dto.auth.LoginRequestDto;
import br.com.kaike.stockmanager.dto.auth.RegisterRequestDto;
import br.com.kaike.stockmanager.dto.auth.TokenResponseDto;
import br.com.kaike.stockmanager.service.AuthenticationService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController controller;

    @Mock
    private AuthenticationService service;

    private MockMvc mockMvc;

    private String jsonRegister;

    private String jsonLogin;

    private String url;

    private RegisterRequestDto registerRequestDto;

    private LoginRequestDto loginRequestDto;

    private TokenResponseDto tokenResponseDto;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();
        url = "/v1/auth";
        registerRequestDto = new RegisterRequestDto("kaike", "kaike@email.com", "12345678");
        loginRequestDto = new LoginRequestDto("kaike@email.com", "12345678");
        tokenResponseDto = new TokenResponseDto("912673jhghjklsd87152381bnb8t6112365210987nkcbxlhjgva", 90000);
        jsonRegister = objectMapper.writeValueAsString(registerRequestDto);
        jsonLogin = objectMapper.writeValueAsString(loginRequestDto);
    }

    @Test
    @DisplayName("Should register user successfully")
    void saveUser() throws Exception {

        doNothing().when(service).register(registerRequestDto);

        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegister)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).register(registerRequestDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Returns an error if the expected parameters are not passed")
    void registerUserInvalid() throws Exception {

        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Should login successfully and return token")
    void loginUser() throws Exception {

        when(service.login(loginRequestDto)).thenReturn(tokenResponseDto);

        mockMvc.perform(post(url + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).login(loginRequestDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Should return BadRequest when login request is invalid")
    void loginError() throws Exception {

        mockMvc.perform(post(url + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

}
