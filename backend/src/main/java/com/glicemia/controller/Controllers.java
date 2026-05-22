package com.glicemia.controller;

import com.glicemia.dto.*;
import com.glicemia.security.JwtUtil;
import com.glicemia.service.*;
import com.glicemia.entity.LogAlteracao;
import com.glicemia.repository.LogAlteracaoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// ============================================================
// AUTH CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username, req.password));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Credenciais inválidas\"}");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.username);
        String token = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .role(role)
                .username(req.username)
                .build());
    }
}

// ============================================================
// DASHBOARD CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}

// ============================================================
// REFEICAO CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/refeicoes")
@RequiredArgsConstructor
class RefeicaoController {

    private final RefeicaoService refeicaoService;

    @GetMapping
    public ResponseEntity<List<RefeicaoResponse>> listar() {
        return ResponseEntity.ok(refeicaoService.listar());
    }

    @PostMapping
    public ResponseEntity<RefeicaoResponse> criar(@RequestBody @Valid RefeicaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refeicaoService.criar(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefeicaoResponse> atualizar(@PathVariable Long id,
                                                       @RequestBody @Valid RefeicaoRequest req) {
        return ResponseEntity.ok(refeicaoService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        refeicaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

// ============================================================
// MEDICAO 2H CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/refeicoes/{id}/medicoes2h")
@RequiredArgsConstructor
class Medicao2HController {

    private final Medicao2HService medicao2HService;

    @PostMapping
    public ResponseEntity<Medicao2HResponse> registrar(@PathVariable Long id,
                                                        @RequestBody @Valid Medicao2HRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicao2HService.registrar(id, req));
    }
}

// ============================================================
// ESTRELAS CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/estrelas")
@RequiredArgsConstructor
class EstrelasController {

    private final EstrelasService estrelasService;

    @GetMapping
    public ResponseEntity<EstrelasResponse> getEstrelas() {
        return ResponseEntity.ok(estrelasService.getEstrelas());
    }

    @PostMapping("/add")
    public ResponseEntity<EstrelasResponse> adicionar(@RequestBody EstrelasRequest req) {
        return ResponseEntity.ok(estrelasService.adicionar(req.quantidade));
    }

    @PostMapping("/remove")
    public ResponseEntity<EstrelasResponse> remover(@RequestBody EstrelasRequest req) {
        return ResponseEntity.ok(estrelasService.remover(req.quantidade));
    }
}

// ============================================================
// LOGS CONTROLLER (Admin only)
// ============================================================
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
class LogsController {

    private final LogAlteracaoRepository logRepository;

    @GetMapping
    public ResponseEntity<List<LogResponse>> getLogs() {
        List<LogAlteracao> logs = logRepository.findTop50ByOrderByTimestampDesc();
        List<LogResponse> resp = logs.stream().map(l -> LogResponse.builder()
                .id(l.getId())
                .timestamp(l.getTimestamp())
                .usuario(l.getUsuario() != null ? l.getUsuario().getUsername() : "SISTEMA")
                .acao(l.getAcao())
                .detalhes(l.getDetalhes())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }
}

// ============================================================
// PENDENCIAS CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/pendencias")
@RequiredArgsConstructor
class PendenciasController {

    private final com.glicemia.repository.Pendencia2HRepository pendencia2HRepository;
    private final com.glicemia.repository.UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<PendenciaResponse>> getPendencias() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        var usuario = usuarioRepository.findByUsername(username).orElseThrow();
        boolean isAdmin = usuario.getRole().equals("ROLE_ADMIN");

        var pendencias = isAdmin
                ? pendencia2HRepository.findAllByStatus(com.glicemia.entity.Pendencia2H.StatusPendencia.PENDENTE)
                : pendencia2HRepository.findByUsuarioIdAndStatus(usuario.getId(), com.glicemia.entity.Pendencia2H.StatusPendencia.PENDENTE);

        var resp = pendencias.stream().map(p -> PendenciaResponse.builder()
                .id(p.getId())
                .refeicaoId(p.getRefeicao().getId())
                .tipoRefeicao(p.getRefeicao().getTipoRefeicao().getDescricao())
                .horarioPrevisto(p.getHorarioPrevisto())
                .status(p.getStatus().name())
                .atrasada(p.getHorarioPrevisto().isBefore(java.time.LocalDateTime.now()))
                .build()).collect(Collectors.toList());

        return ResponseEntity.ok(resp);
    }
}
