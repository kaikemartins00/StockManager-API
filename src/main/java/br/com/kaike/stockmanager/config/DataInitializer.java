package br.com.kaike.stockmanager.config;

import br.com.kaike.stockmanager.domain.entity.RoleEntity;
import br.com.kaike.stockmanager.domain.entity.UserEntity;
import br.com.kaike.stockmanager.domain.enums.RoleTypeEnum;
import br.com.kaike.stockmanager.domain.repository.RoleRepository;
import br.com.kaike.stockmanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        for (RoleTypeEnum roleEnum : RoleTypeEnum.values()) {

            if (roleRepository.findByName(roleEnum.name()).isEmpty()) {

                RoleEntity role = new RoleEntity();

                role.setName(roleEnum.name());

                roleRepository.save(role);
            }
        }


        if (userRepository.findByEmail("admin@stockmanager.com").isEmpty()) {


            RoleEntity adminRole = roleRepository
                    .findByName(RoleTypeEnum.ROLE_ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));


            UserEntity admin = new UserEntity();

            admin.setUserName("Admin");
            admin.setEmail("admin@stockmanager.com");
            admin.setPassword(passwordEncoder.encode("123456"));


            admin.getRole().add(adminRole);


            userRepository.save(admin);
        }
    }
}