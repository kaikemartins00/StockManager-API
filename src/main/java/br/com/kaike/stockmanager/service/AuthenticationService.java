package br.com.kaike.stockmanager.service;

import br.com.kaike.stockmanager.domain.entity.RoleEntity;
import br.com.kaike.stockmanager.domain.entity.UserEntity;
import br.com.kaike.stockmanager.domain.enums.RoleTypeEnum;
import br.com.kaike.stockmanager.domain.repository.RoleRepository;
import br.com.kaike.stockmanager.domain.repository.UserRepository;
import br.com.kaike.stockmanager.dto.auth.LoginRequestDto;
import br.com.kaike.stockmanager.dto.auth.RegisterRequestDto;
import br.com.kaike.stockmanager.dto.auth.TokenResponseDto;
import br.com.kaike.stockmanager.exception.BadRequestException;
import br.com.kaike.stockmanager.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Value("${spring.jwt.expiration}")
    private long expirationTime;

    public void register(RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("The email already exists.");
        }

        RoleEntity role = roleRepository.findByName(RoleTypeEnum.ROLE_USER.name()).orElseGet(() -> roleRepository.save(RoleEntity.builder().name(RoleTypeEnum.ROLE_USER.name()).build()));

        userRepository.save(UserEntity.builder().userName(dto.userName()).email(dto.email()).role(Set.of(role)).password(passwordEncoder.encode(dto.password())).build());

    }

    public TokenResponseDto login(LoginRequestDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));

            String token = tokenProvider.generateToken(authentication);

            return new TokenResponseDto(token, expirationTime);

        } catch (BadCredentialsException e) {
            throw new BadRequestException("invalid credentials");
        }
    }

}