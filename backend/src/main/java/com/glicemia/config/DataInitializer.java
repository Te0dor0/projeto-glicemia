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

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            if (usuarioRepository.findByUsername("Teo").isEmpty()) {
                Usuario teo = new Usuario();
                teo.setUsername("Teo");
                teo.setPasswordHash(passwordEncoder.encode("REMOVED_PASSWORD"));
                teo.setRole("ROLE_ADMIN");
                usuarioRepository.save(teo);
                log.info("✅ Admin 'Teo' criado.");
            }
        } catch (Exception e) {
            log.error("Erro ao criar Teo: {}", e.getMessage());
        }
    }
}
