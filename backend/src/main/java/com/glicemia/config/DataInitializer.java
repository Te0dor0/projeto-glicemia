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
            if (usuarioRepository.findByUsername("Teo").isEmpty()) {
                Usuario teo = new Usuario();
                teo.setUsername("Teo");
                teo.setPasswordHash(passwordEncoder.encode("REMOVED_PASSWORD"));
                teo.setRole("ROLE_ADMIN");
                usuarioRepository.save(teo);
                log.info("✅ Admin 'Teo' criado.");
            }
            
            if (usuarioRepository.findByUsername("Lui").isEmpty()) {
                Usuario lui = new Usuario();
                lui.setUsername("Lui");
                lui.setPasswordHash(passwordEncoder.encode("REMOVED_PASSWORD"));
                lui.setRole("ROLE_USER");
                usuarioRepository.save(lui);
                log.info("✅ Usuário 'Lui' criado.");
            }
        } catch (Exception e) {
            log.error("Erro crítico no DataInitializer: {}. O sistema continuará operando.", e.getMessage(), e);
        }
    }
}
