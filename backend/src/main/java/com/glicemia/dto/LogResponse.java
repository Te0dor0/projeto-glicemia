package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.glicemia.entity.Refeicao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LogResponse {
    public Long id;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime timestamp;
    public String usuario;
    public String acao;
    public String detalhes;
}
