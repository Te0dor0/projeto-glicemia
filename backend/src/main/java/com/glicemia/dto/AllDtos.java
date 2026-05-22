package com.glicemia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.glicemia.entity.Refeicao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== REQUEST ==========

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank public String username;
    @NotBlank public String password;
}

@Data @NoArgsConstructor @AllArgsConstructor
public class RefeicaoRequest {
    @NotNull public Refeicao.TipoRefeicao tipo;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime medicaoAntesHora;
    @NotNull @Min(0) @Max(600) public Integer valorAntes;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime inicio;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime fim;
    public String observacao;
}

@Data @NoArgsConstructor @AllArgsConstructor
public class Medicao2HRequest {
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horario;
    @NotNull @Min(0) @Max(600) public Integer valor;
    public String observacao;
}

@Data @NoArgsConstructor @AllArgsConstructor
public class EstrelasRequest {
    @NotNull @Min(1) public Integer quantidade;
}

// ========== RESPONSE ==========

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    public String token;
    public String role;
    public String username;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RefeicaoResponse {
    public Long id;
    public String tipoRefeicao;
    public String tipoDescricao;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime medicaoAntesHorario;
    public Integer valorAntes;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horarioInicio;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horarioFim;
    public String observacao;
    public String usuarioUsername;
    public Boolean temPendencia;
    public String statusPendencia;
    public List<Medicao2HResponse> medicoes;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicao2HResponse {
    public Long id;
    public Long refeicaoId;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horarioMedicao;
    public Integer valorGlicemia;
    public String observacao;
    public String statusGlicemia;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PendenciaResponse {
    public Long id;
    public Long refeicaoId;
    public String tipoRefeicao;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horarioPrevisto;
    public String status;
    public Boolean atrasada;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {
    public Integer ultimaGlicemia;
    public String statusUltimaGlicemia;
    public List<PendenciaResponse> pendencias;
    public List<String> alertas;
    public Integer taxaMedicoes;
    public List<RefeicaoResponse> ultimasRefeicoes;
    public Integer totalEstrelas;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EstrelasResponse {
    public Integer quantidade;
}

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LogResponse {
    public Long id;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime timestamp;
    public String usuario;
    public String acao;
    public String detalhes;
}
