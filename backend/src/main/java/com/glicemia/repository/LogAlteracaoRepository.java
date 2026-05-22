package com.glicemia.repository;

import com.glicemia.entity.LogAlteracao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogAlteracaoRepository extends JpaRepository<LogAlteracao, Long> {
    List<LogAlteracao> findAllByOrderByTimestampDesc();
    List<LogAlteracao> findTop50ByOrderByTimestampDesc();
}
