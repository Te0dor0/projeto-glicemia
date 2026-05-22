package com.glicemia.repository;

import com.glicemia.entity.Refeicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RefeicaoRepository extends JpaRepository<Refeicao, Long> {

    List<Refeicao> findByUsuarioIdOrderByHorarioInicioDesc(Long usuarioId);

    @Query("SELECT r FROM Refeicao r ORDER BY r.horarioInicio DESC")
    List<Refeicao> findAllOrderByHorarioInicioDesc();

    @Query("SELECT r FROM Refeicao r WHERE r.usuario.id = :usuarioId ORDER BY r.horarioInicio DESC LIMIT 5")
    List<Refeicao> findTop5ByUsuarioIdOrderByHorarioInicioDesc(@Param("usuarioId") Long usuarioId);

    @Query("SELECT r FROM Refeicao r ORDER BY r.horarioInicio DESC LIMIT 10")
    List<Refeicao> findTop10OrderByHorarioInicioDesc();
}
