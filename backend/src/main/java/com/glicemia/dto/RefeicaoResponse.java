package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
