package com.glicemia.service;

import com.glicemia.dto.*;
import com.glicemia.entity.*;
import com.glicemia.excel.ExcelService;
import com.glicemia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefeicaoService {

    private final RefeicaoRepository refeicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Pendencia2HRepository pendencia2HRepository;
    private final LogAlteracaoRepository logRepository;
    private final ExcelService excelService;

    @Transactional
    public RefeicaoResponse criar(RefeicaoRequest req) {
        Usuario usuario = getUsuarioAutenticado();

        Refeicao refeicao = Refeicao.builder()
                .usuario(usuario)
                .tipoRefeicao(req.tipo)
                .medicaoAntesHorario(req.medicaoAntesHora)
                .valorAntes(req.valorAntes)
                .horarioInicio(req.inicio)
                .horarioFim(req.fim)
                .observacao(req.observacao)
                .build();

        refeicao = refeicaoRepository.save(refeicao);

        // Criar pendência automática 2h após o término da refeição
        Pendencia2H pendencia = Pendencia2H.builder()
                .refeicao(refeicao)
                .horarioPrevisto(req.fim.plusHours(2))
                .status(Pendencia2H.StatusPendencia.PENDENTE)
                .build();
        pendencia2HRepository.save(pendencia);

        // Log
        registrarLog(usuario, refeicao, "CRIAR_REFEICAO",
                String.format("Refeição %s criada. Glicemia antes: %d mg/dL", req.tipo, req.valorAntes));

        // Sincronizar Excel
        excelService.exportarTodosParaExcel();

        return toResponse(refeicaoRepository.findById(refeicao.getId()).orElseThrow());
    }

    @Transactional
    public RefeicaoResponse atualizar(Long id, RefeicaoRequest req) {
        Refeicao refeicao = refeicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refeição não encontrada: " + id));

        Usuario usuario = getUsuarioAutenticado();
        refeicao.setTipoRefeicao(req.tipo);
        refeicao.setMedicaoAntesHorario(req.medicaoAntesHora);
        refeicao.setValorAntes(req.valorAntes);
        refeicao.setHorarioInicio(req.inicio);
        refeicao.setHorarioFim(req.fim);
        refeicao.setObservacao(req.observacao);

        refeicao = refeicaoRepository.save(refeicao);

        // Atualizar pendência
        pendencia2HRepository.findByRefeicaoId(id).ifPresent(p -> {
            p.setHorarioPrevisto(req.fim.plusHours(2));
            pendencia2HRepository.save(p);
        });

        registrarLog(usuario, refeicao, "EDITAR_REFEICAO",
                String.format("Refeição ID %d atualizada.", id));

        excelService.exportarTodosParaExcel();
        return toResponse(refeicao);
    }

    @Transactional
    public void deletar(Long id) {
        Refeicao refeicao = refeicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refeição não encontrada: " + id));

        Usuario usuario = getUsuarioAutenticado();
        registrarLog(usuario, null, "DELETAR_REFEICAO",
                String.format("Refeição ID %d (%s) deletada.", id, refeicao.getTipoRefeicao()));

        refeicaoRepository.delete(refeicao);
        excelService.exportarTodosParaExcel();
    }

    @Transactional(readOnly = true)
    public List<RefeicaoResponse> listar() {
        Usuario usuario = getUsuarioAutenticado();
        boolean isAdmin = usuario.getRole().equals("ROLE_ADMIN");

        List<Refeicao> lista = isAdmin
                ? refeicaoRepository.findAllOrderByHorarioInicioDesc()
                : refeicaoRepository.findByUsuarioIdOrderByHorarioInicioDesc(usuario.getId());

        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ==================== HELPERS ====================

    public RefeicaoResponse toResponse(Refeicao r) {
        Pendencia2H pendencia = pendencia2HRepository.findByRefeicaoId(r.getId()).orElse(null);
        List<Medicao2HResponse> medicoes = r.getMedicoes() != null
                ? r.getMedicoes().stream().map(m -> Medicao2HResponse.builder()
                        .id(m.getId())
                        .refeicaoId(r.getId())
                        .horarioMedicao(m.getHorarioMedicao())
                        .valorGlicemia(m.getValorGlicemia())
                        .observacao(m.getObservacao())
                        .statusGlicemia(calcStatusGlicemia(m.getValorGlicemia()))
                        .build()).collect(Collectors.toList())
                : List.of();

        return RefeicaoResponse.builder()
                .id(r.getId())
                .tipoRefeicao(r.getTipoRefeicao().name())
                .tipoDescricao(r.getTipoRefeicao().getDescricao())
                .medicaoAntesHorario(r.getMedicaoAntesHorario())
                .valorAntes(r.getValorAntes())
                .horarioInicio(r.getHorarioInicio())
                .horarioFim(r.getHorarioFim())
                .observacao(r.getObservacao())
                .usuarioUsername(r.getUsuario().getUsername())
                .temPendencia(pendencia != null && pendencia.getStatus() == Pendencia2H.StatusPendencia.PENDENTE)
                .statusPendencia(pendencia != null ? pendencia.getStatus().name() : "SEM_PENDENCIA")
                .medicoes(medicoes)
                .build();
    }

    public static String calcStatusGlicemia(Integer valor) {
        if (valor == null) return "SEM_DADO";
        if (valor < 50) return "PERIGO_BAIXO";
        if (valor > 150) return "PERIGO_ALTO";
        return "NORMAL";
    }

    private Usuario getUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private void registrarLog(Usuario usuario, Refeicao refeicao, String acao, String detalhes) {
        LogAlteracao log = LogAlteracao.builder()
                .timestamp(LocalDateTime.now())
                .usuario(usuario)
                .refeicao(refeicao)
                .acao(acao)
                .detalhes(detalhes)
                .build();
        logRepository.save(log);
    }
}
