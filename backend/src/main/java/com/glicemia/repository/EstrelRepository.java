package com.glicemia.repository;

import com.glicemia.entity.Estrela;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstrelRepository extends JpaRepository<Estrela, Long> {
    Optional<Estrela> findByUsuarioId(Long usuarioId);
    Optional<Estrela> findByUsuarioUsername(String username);
}
