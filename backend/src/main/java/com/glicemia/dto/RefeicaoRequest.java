package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.glicemia.entity.Refeicao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor
public class RefeicaoRequest {
    @NotNull public Refeicao.TipoRefeicao tipo;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime medicaoAntesHora;
    @NotNull @Min(0) @Max(600) public Integer valorAntes;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime inicio;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime fim;
    public String observacao;
}

