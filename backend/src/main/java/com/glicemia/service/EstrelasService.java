package com.glicemia.service;

import com.glicemia.dto.EstrelasResponse;
import com.glicemia.entity.Estrela;
import com.glicemia.entity.LogAlteracao;
import com.glicemia.entity.Usuario;
import com.glicemia.repository.EstrelRepository;
import com.glicemia.repository.LogAlteracaoRepository;
import com.glicemia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EstrelasService {

    private final EstrelRepository estrelRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogAlteracaoRepository logRepository;

    @Transactional(readOnly = true)
    public EstrelasResponse getEstrelas() {
        int qtd = estrelRepository.findByUsuarioUsername("Lui")
                .map(Estrela::getQuantidade).orElse(0);
        return new EstrelasResponse(qtd);
    }

    @Transactional
    public EstrelasResponse adicionar(int quantidade) {
        return modificar(quantidade, "ADICIONAR_ESTRELA");
    }

    @Transactional
    public EstrelasResponse remover(int quantidade) {
        return modificar(-quantidade, "REMOVER_ESTRELA");
    }

    private EstrelasResponse modificar(int delta, String acao) {
        Usuario lui = usuarioRepository.findByUsername("Lui")
                .orElseThrow(() -> new RuntimeException("Usuário Lui não encontrado"));

        Estrela estrela = estrelRepository.findByUsuarioId(lui.getId())
                .orElseGet(() -> Estrela.builder().usuario(lui).quantidade(0).build());

        int novaQtd = Math.max(0, estrela.getQuantidade() + delta);
        estrela.setQuantidade(novaQtd);
        estrelRepository.save(estrela);

        // Log
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioRepository.findByUsername(adminUsername).orElse(null);
        LogAlteracao log = LogAlteracao.builder()
                .timestamp(LocalDateTime.now())
                .usuario(admin)
                .acao(acao)
                .detalhes(String.format("Estrelas de Lui: %s%d → Total: %d",
                        delta > 0 ? "+" : "", delta, novaQtd))
                .build();
        logRepository.save(log);

        return new EstrelasResponse(novaQtd);
    }
}
