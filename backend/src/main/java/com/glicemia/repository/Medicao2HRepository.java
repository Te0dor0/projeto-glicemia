package com.glicemia.repository;

import com.glicemia.entity.Medicao2H;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface Medicao2HRepository extends JpaRepository<Medicao2H, Long> {

    List<Medicao2H> findByRefeicaoIdOrderByHorarioMedicaoDesc(Long refeicaoId);

    @Query("SELECT m FROM Medicao2H m JOIN m.refeicao r WHERE r.usuario.id = :usuarioId ORDER BY m.horarioMedicao DESC LIMIT 1")
    Optional<Medicao2H> findUltimaMedicaoByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT m FROM Medicao2H m ORDER BY m.horarioMedicao DESC LIMIT 1")
    Optional<Medicao2H> findUltimaMedicao();
}
