package com.glicemia.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank public String username;
    @NotBlank public String password;
}
