package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.glicemia.entity.Refeicao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EstrelasResponse {
    public Integer quantidade;
}

