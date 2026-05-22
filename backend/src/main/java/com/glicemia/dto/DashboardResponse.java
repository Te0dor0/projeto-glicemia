package com.glicemia.dto;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {
    public Integer ultimaGlicemia;
    public String statusUltimaGlicemia;
    public List<PendenciaResponse> pendencias;
    public List<String> alertas;
    public Integer taxaMedicoes;
    public List<RefeicaoResponse> ultimasRefeicoes;
    public Integer totalEstrelas;
}
