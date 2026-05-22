package com.glicemia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEDICAO_2H")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicao2H {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refeicao_id", nullable = false)
    private Refeicao refeicao;

    @Column(name = "horario_medicao", nullable = false)
    private LocalDateTime horarioMedicao;

    @Column(name = "valor_glicemia", nullable = false)
    private Integer valorGlicemia;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
