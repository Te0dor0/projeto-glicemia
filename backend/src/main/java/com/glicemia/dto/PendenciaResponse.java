package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.glicemia.entity.Refeicao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PendenciaResponse {
    public Long id;
    public Long refeicaoId;
    public String tipoRefeicao;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horarioPrevisto;
    public String status;
    public Boolean atrasada;
}

