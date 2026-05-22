package com.glicemia.repository;

import com.glicemia.entity.Pendencia2H;
import com.glicemia.entity.Pendencia2H.StatusPendencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface Pendencia2HRepository extends JpaRepository<Pendencia2H, Long> {

    Optional<Pendencia2H> findByRefeicaoId(Long refeicaoId);

    @Query("SELECT p FROM Pendencia2H p WHERE p.refeicao.usuario.id = :usuarioId AND p.status = :status ORDER BY p.horarioPrevisto ASC")
    List<Pendencia2H> findByUsuarioIdAndStatus(@Param("usuarioId") Long usuarioId, @Param("status") StatusPendencia status);

    @Query("SELECT p FROM Pendencia2H p WHERE p.status = :status ORDER BY p.horarioPrevisto ASC")
    List<Pendencia2H> findAllByStatus(@Param("status") StatusPendencia status);

    @Query("SELECT COUNT(p) FROM Pendencia2H p WHERE p.refeicao.usuario.id = :usuarioId AND p.status = 'CONCLUIDO'")
    long countConcluidasByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(p) FROM Pendencia2H p WHERE p.refeicao.usuario.id = :usuarioId")
    long countTotalByUsuarioId(@Param("usuarioId") Long usuarioId);
}
