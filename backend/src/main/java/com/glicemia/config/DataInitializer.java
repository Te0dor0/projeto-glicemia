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
    private final EstrelRepository estrelRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Cria usuário Admin (Teo) se não existir
        if (usuarioRepository.findByUsername("Teo").isEmpty()) {
            Usuario teo = Usuario.builder()
                    .username("Teo")
                    .passwordHash(passwordEncoder.encode("REMOVED_PASSWORD"))
                    .role("ROLE_ADMIN")
                    .build();
            usuarioRepository.save(teo);
            log.info("✅ Usuário Admin 'Teo' criado.");
        }

        // Cria usuário Lui se não existir
        if (usuarioRepository.findByUsername("Lui").isEmpty()) {
            Usuario lui = Usuario.builder()
                    .username("Lui")
                    .passwordHash(passwordEncoder.encode("REMOVED_PASSWORD"))
                    .role("ROLE_USER")
                    .build();
            Usuario savedLui = usuarioRepository.save(lui);

            // Inicializa estrelas de Lui com 0
            Estrela estrela = Estrela.builder()
                    .quantidade(0)
                    .usuario(savedLui)
                    .build();
            estrelRepository.save(estrela);
            log.info("✅ Usuário 'Lui' criado com 0 estrelas.");
        }

        log.info("🌟 Sistema de Glicemia inicializado com sucesso!");
    }
}
