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

//@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EstrelRepository estrelRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Garante que o usuário Admin (Teo) exista e tenha a senha correta
        Usuario teo = usuarioRepository.findByUsername("Teo").orElse(new Usuario());
        teo.setUsername("Teo");
        teo.setPasswordHash(passwordEncoder.encode("REMOVED_PASSWORD"));
        teo.setRole("ROLE_ADMIN");
        usuarioRepository.save(teo);
        log.info("✅ Usuário Admin 'Teo' configurado com sucesso.");

        // Garante que o usuário Lui exista e tenha a senha correta
        Usuario lui = usuarioRepository.findByUsername("Lui").orElse(new Usuario());
        lui.setUsername("Lui");
        lui.setPasswordHash(passwordEncoder.encode("REMOVED_PASSWORD"));
        lui.setRole("ROLE_USER");
        Usuario savedLui = usuarioRepository.save(lui);

        // Inicializa estrelas de Lui se não existirem
        try {
            if (estrelRepository.findAll().stream().noneMatch(e -> e.getUsuario() != null && "Lui".equals(e.getUsuario().getUsername()))) {
                Estrela estrela = Estrela.builder()
                        .quantidade(0)
                        .usuario(savedLui)
                        .build();
                estrelRepository.save(estrela);
                log.info("✅ Estrelas do usuário 'Lui' inicializadas.");
            }
        } catch (Exception e) {
            log.error("Erro ao inicializar estrelas para Lui: {}", e.getMessage());
        }
        log.info("✅ Usuário 'Lui' configurado com sucesso.");

        log.info("🌟 Sistema de Glicemia inicializado com sucesso!");
    }
}
