package br.com.kaike.stockmanager.service;

import br.com.kaike.stockmanager.domain.entity.RoleEntity;
import br.com.kaike.stockmanager.domain.entity.UserEntity;
import br.com.kaike.stockmanager.domain.repository.RoleRepository;
import br.com.kaike.stockmanager.domain.repository.UserRepository;
import br.com.kaike.stockmanager.dto.auth.LoginRequestDto;
import br.com.kaike.stockmanager.dto.auth.RegisterRequestDto;
import br.com.kaike.stockmanager.dto.auth.TokenResponseDto;
import br.com.kaike.stockmanager.exception.BadRequestException;
import br.com.kaike.stockmanager.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static br.com.kaike.stockmanager.domain.enums.RoleTypeEnum.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider tokenProvider;

    private Authentication authentication;

    private RegisterRequestDto registerRequestDto;

    private LoginRequestDto loginRequestDto;


    @BeforeEach
    void setUp() {
        registerRequestDto = new RegisterRequestDto("kaike", "kaike@email.com", "12345678");
        loginRequestDto = new LoginRequestDto("kaike@email.com", "12345678");

    }

    @Test
    @DisplayName("Should register user successfully")
    void registerSuccess() {

        RoleEntity role = RoleEntity.builder().name(ROLE_USER.name()).build();

        when(userRepository.findByEmail(registerRequestDto.email())).thenReturn(Optional.empty());

        when(roleRepository.findByName(ROLE_USER.name())).thenReturn(Optional.of(role));

        when(passwordEncoder.encode(registerRequestDto.password())).thenReturn("encodedPassword");

        authService.register(registerRequestDto);

        verify(userRepository).findByEmail(registerRequestDto.email());
        verify(roleRepository).findByName(ROLE_USER.name());
        verify(passwordEncoder).encode(registerRequestDto.password());

        verify(userRepository).save(any(UserEntity.class));
        verify(roleRepository, never()).save(any(RoleEntity.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when email already exists")
    void registerEmailAlreadyExistsError() {

        UserEntity user = UserEntity.builder().email(registerRequestDto.email()).build();

        when(userRepository.findByEmail(registerRequestDto.email())).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.register(registerRequestDto));

        assertEquals("The email already exists.", exception.getMessage());

        verify(userRepository).findByEmail(registerRequestDto.email());
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    @DisplayName("Should create ROLE_USER when role does not exist")
    void registerCreateDefaultRoleSuccess() {

        RoleEntity role = RoleEntity.builder().name(ROLE_USER.name()).build();

        when(userRepository.findByEmail(registerRequestDto.email())).thenReturn(Optional.empty());

        when(roleRepository.findByName(ROLE_USER.name())).thenReturn(Optional.empty());

        when(roleRepository.save(any(RoleEntity.class))).thenReturn(role);

        when(passwordEncoder.encode(registerRequestDto.password())).thenReturn("encodedPassword");

        authService.register(registerRequestDto);

        verify(roleRepository).findByName(ROLE_USER.name());
        verify(roleRepository).save(any(RoleEntity.class));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should login successfully and return token")
    void loginSuccess() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        TokenResponseDto response = authService.login(loginRequestDto);

        assertEquals("jwt-token", response.token());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    @DisplayName("Should throw BadRequestException when credentials are invalid")
    void loginInvalidCredentialsError() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("invalid credentials"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequestDto));

        assertEquals("invalid credentials", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

    }
}