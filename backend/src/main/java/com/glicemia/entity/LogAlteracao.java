package com.glicemia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOG_ALTERACAO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LogAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refeicao_id")
    private Refeicao refeicao;

    @Column(length = 100)
    private String acao;

    @Column(columnDefinition = "TEXT")
    private String detalhes;
}
