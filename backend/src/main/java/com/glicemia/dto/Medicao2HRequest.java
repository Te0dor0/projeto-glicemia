package com.glicemia.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class Medicao2HRequest {
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm") public LocalDateTime horario;
    @NotNull @Min(0) @Max(600) public Integer valor;
    public String observacao;
}
