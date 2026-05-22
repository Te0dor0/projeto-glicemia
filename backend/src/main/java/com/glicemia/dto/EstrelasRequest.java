package com.glicemia.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class EstrelasRequest {
    @NotNull @Min(1) public Integer quantidade;
}
