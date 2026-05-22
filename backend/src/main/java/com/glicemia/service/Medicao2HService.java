package com.glicemia.service;

import com.glicemia.dto.*;
import com.glicemia.entity.*;
import com.glicemia.excel.ExcelService;
import com.glicemia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Medicao2HService {

    private final Medicao2HRepository medicao2HRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final Pendencia2HRepository pendencia2HRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogAlteracaoRepository logRepository;
    private final ExcelService excelService;

    @Transactional
    public Medicao2HResponse registrar(Long refeicaoId, Medicao2HRequest req) {
        Refeicao refeicao = refeicaoRepository.findById(refeicaoId)
                .orElseThrow(() -> new RuntimeException("Refeição não encontrada: " + refeicaoId));

        Medicao2H medicao = Medicao2H.builder()
                .refeicao(refeicao)
                .horarioMedicao(req.horario)
                .valorGlicemia(req.valor)
                .observacao(req.observacao)
                .build();

        // variável final para usar no lambda
        final Medicao2H medicaoSalva = medicao2HRepository.save(medicao);

        // Atualizar pendência para CONCLUIDO
        pendencia2HRepository.findByRefeicaoId(refeicaoId).ifPresent(p -> {
            p.setStatus(Pendencia2H.StatusPendencia.CONCLUIDO);
            p.setMedicao(medicaoSalva);
            pendencia2HRepository.save(p);
        });

        // Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        LogAlteracao log = LogAlteracao.builder()
                .timestamp(LocalDateTime.now())
                .usuario(usuario)
                .refeicao(refeicao)
                .acao("REGISTRAR_MEDICAO_2H")
                .detalhes(String.format("Medição 2H para refeição ID %d: %d mg/dL", refeicaoId, req.valor))
                .build();
        logRepository.save(log);

        excelService.exportarTodosParaExcel();

        return Medicao2HResponse.builder()
                .id(medicaoSalva.getId())
                .refeicaoId(refeicaoId)
                .horarioMedicao(medicaoSalva.getHorarioMedicao())
                .valorGlicemia(medicaoSalva.getValorGlicemia())
                .observacao(medicaoSalva.getObservacao())
                .statusGlicemia(RefeicaoService.calcStatusGlicemia(medicaoSalva.getValorGlicemia()))
                .build();
    }
}
