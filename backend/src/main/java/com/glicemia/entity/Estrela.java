package com.glicemia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ESTRELA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Estrela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantidade;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
