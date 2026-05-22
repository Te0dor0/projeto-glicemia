package com.glicemia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "REFEICAO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Refeicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_refeicao", nullable = false)
    private TipoRefeicao tipoRefeicao;

    @Column(name = "medicao_antes_horario", nullable = false)
    private LocalDateTime medicaoAntesHorario;

    @Column(name = "valor_antes", nullable = false)
    private Integer valorAntes;

    @Column(name = "horario_inicio", nullable = false)
    private LocalDateTime horarioInicio;

    @Column(name = "horario_fim", nullable = false)
    private LocalDateTime horarioFim;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "refeicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medicao2H> medicoes;

    @OneToOne(mappedBy = "refeicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pendencia2H pendencia;

    @OneToMany(mappedBy = "refeicao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogAlteracao> logs;

    public enum TipoRefeicao {
        CAFE("Café da Manhã"),
        LANCHE("Lanche"),
        ALMOCO("Almoço"),
        TARDE("Lanche da Tarde"),
        JANTA("Janta"),
        CEIA("Ceia");

        private final String descricao;
        TipoRefeicao(String descricao) { this.descricao = descricao; }
        public String getDescricao() { return descricao; }
    }
}
