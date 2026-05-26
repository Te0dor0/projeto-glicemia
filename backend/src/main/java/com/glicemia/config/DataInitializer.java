package com.glicemia.config;

import com.glicemia.entity.Estrela;
import com.glicemia.entity.Usuario;
import com.glicemia.repository.EstrelRepository;
import com.glicemia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Iniciando DataInitializer...");
                        String adminUser = System.getenv("ADMIN_USERNAME");
            String adminPass = System.getenv("ADMIN_PASSWORD");
            
            if (adminUser != null && adminPass != null && usuarioRepository.findByUsername(adminUser).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername(adminUser);
                admin.setPasswordHash(passwordEncoder.encode(adminPass));
                admin.setRole("ROLE_ADMIN");
                usuarioRepository.save(admin);
                log.info("✅ Admin '{}' criado via variável de ambiente.", adminUser);
            }
        } catch (Exception e) {
            log.error("Erro crítico no DataInitializer: {}. O sistema continuará operando.", e.getMessage(), e);
        }
    }
}
