package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "registros_treino")
public class RegistroTreino implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private String tipoExercicio;
    @Column(nullable = false)
    private int duracao;
    @Column(length = 500)
    private String observacoes;
    @Column(nullable = false)
    private boolean checkInAcademia;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate v) {
        data = v;
    }

    public String getTipoExercicio() {
        return tipoExercicio;
    }

    public void setTipoExercicio(String v) {
        tipoExercicio = v;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int v) {
        duracao = v;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String v) {
        observacoes = v;
    }

    public boolean isCheckInAcademia() {
        return checkInAcademia;
    }

    public void setCheckInAcademia(boolean v) {
        checkInAcademia = v;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario v) {
        usuario = v;
    }
}