package com.glicemia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PENDENCIA_2H")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pendencia2H {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refeicao_id", nullable = false)
    private Refeicao refeicao;

    @Column(name = "horario_previsto", nullable = false)
    private LocalDateTime horarioPrevisto;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusPendencia status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicao_id")
    private Medicao2H medicao;

    public enum StatusPendencia {
        PENDENTE, CONCLUIDO
    }
}
