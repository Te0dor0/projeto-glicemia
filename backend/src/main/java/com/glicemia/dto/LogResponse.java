package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LogResponse {
    public Long id;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime timestamp;
    public String usuario;
    public String acao;
    public String detalhes;
}
