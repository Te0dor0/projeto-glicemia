package com.glicemia.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    public String token;
    public String role;
    public String username;
}
