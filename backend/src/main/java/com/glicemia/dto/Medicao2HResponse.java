package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.glicemia.entity.Refeicao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicao2HResponse {
    public Long id;
    public Long refeicaoId;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horarioMedicao;
    public Integer valorGlicemia;
    public String observacao;
    public String statusGlicemia;
}

