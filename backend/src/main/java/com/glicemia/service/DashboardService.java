package com.glicemia.service;

import com.glicemia.dto.*;
import com.glicemia.entity.*;
import com.glicemia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final Medicao2HRepository medicao2HRepository;
    private final Pendencia2HRepository pendencia2HRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final EstrelRepository estrelRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean isAdmin = usuario.getRole().equals("ROLE_ADMIN");

        // Última glicemia
        Optional<Medicao2H> ultimaMedicao = isAdmin
                ? medicao2HRepository.findUltimaMedicao()
                : medicao2HRepository.findUltimaMedicaoByUsuarioId(usuario.getId());

        Integer ultimaGlicemia = ultimaMedicao.map(Medicao2H::getValorGlicemia).orElse(null);
        String statusGlicemia = RefeicaoService.calcStatusGlicemia(ultimaGlicemia);

        // Pendências
        List<Pendencia2H> pendencias = isAdmin
                ? pendencia2HRepository.findAllByStatus(Pendencia2H.StatusPendencia.PENDENTE)
                : pendencia2HRepository.findByUsuarioIdAndStatus(usuario.getId(), Pendencia2H.StatusPendencia.PENDENTE);

        List<PendenciaResponse> pendenciasResp = pendencias.stream()
                .map(p -> PendenciaResponse.builder()
                        .id(p.getId())
                        .refeicaoId(p.getRefeicao().getId())
                        .tipoRefeicao(p.getRefeicao().getTipoRefeicao().getDescricao())
                        .horarioPrevisto(p.getHorarioPrevisto())
                        .status(p.getStatus().name())
                        .atrasada(p.getHorarioPrevisto().isBefore(LocalDateTime.now()))
                        .build())
                .collect(Collectors.toList());

        // Alertas
        List<String> alertas = new ArrayList<>();
        if (ultimaGlicemia != null) {
            if (ultimaGlicemia < 50) {
                alertas.add("⚠️ ALERTA CRÍTICO: Glicemia muito baixa! Valor atual: " + ultimaGlicemia + " mg/dL");
            } else if (ultimaGlicemia > 150) {
                alertas.add("⚠️ ALERTA: Glicemia elevada! Valor atual: " + ultimaGlicemia + " mg/dL");
            }
        }
        if (!pendencias.isEmpty()) {
            alertas.add("📋 Você tem " + pendencias.size() + " medição(ões) pendente(s)");
        }

        // Taxa de medições (%)
        long concluidas = isAdmin
                ? pendencia2HRepository.count() - pendencia2HRepository.findAllByStatus(Pendencia2H.StatusPendencia.PENDENTE).size()
                : pendencia2HRepository.countConcluidasByUsuarioId(usuario.getId());
        long total = isAdmin
                ? pendencia2HRepository.count()
                : pendencia2HRepository.countTotalByUsuarioId(usuario.getId());
        int taxa = total == 0 ? 100 : (int) ((concluidas * 100) / total);

        // Últimas refeições
        List<Refeicao> ultimasRefeicoes = isAdmin
                ? refeicaoRepository.findTop10OrderByHorarioInicioDesc()
                : refeicaoRepository.findTop5ByUsuarioIdOrderByHorarioInicioDesc(usuario.getId());

        // Estrelas de Lui
        Integer estrelas = estrelRepository.findByUsuarioUsername("Lui")
                .map(Estrela::getQuantidade).orElse(0);

        return DashboardResponse.builder()
                .ultimaGlicemia(ultimaGlicemia)
                .statusUltimaGlicemia(statusGlicemia)
                .pendencias(pendenciasResp)
                .alertas(alertas)
                .taxaMedicoes(taxa)
                .ultimasRefeicoes(ultimasRefeicoes.stream()
                        .map(r -> RefeicaoResponse.builder()
                                .id(r.getId())
                                .tipoRefeicao(r.getTipoRefeicao().name())
                                .tipoDescricao(r.getTipoRefeicao().getDescricao())
                                .valorAntes(r.getValorAntes())
                                .horarioInicio(r.getHorarioInicio())
                                .horarioFim(r.getHorarioFim())
                                .usuarioUsername(r.getUsuario().getUsername())
                                .build())
                        .collect(Collectors.toList()))
                .totalEstrelas(estrelas)
                .build();
    }
}
