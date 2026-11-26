package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "vinculos_profissionais")
public class VinculoProfissional implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Profissional profissional;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVinculo status;
    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario v) {
        usuario = v;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional v) {
        profissional = v;
    }

    public StatusVinculo getStatus() {
        return status;
    }

    public void setStatus(StatusVinculo v) {
        status = v;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime v) {
        dataSolicitacao = v;
    }
}
