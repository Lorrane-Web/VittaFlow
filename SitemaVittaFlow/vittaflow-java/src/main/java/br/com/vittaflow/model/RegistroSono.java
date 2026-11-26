package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "registros_sono")
public class RegistroSono implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private java.time.LocalTime horarioDormir;
    @Column(nullable = false)
    private java.time.LocalTime horarioAcordar;
    @Column(nullable = false)
    private double horasDormidas;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QualidadeSono qualidade;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate v) {
        data = v;
    }

    public java.time.LocalTime getHorarioDormir() {
        return horarioDormir;
    }

    public void setHorarioDormir(java.time.LocalTime v) {
        horarioDormir = v;
    }

    public java.time.LocalTime getHorarioAcordar() {
        return horarioAcordar;
    }

    public void setHorarioAcordar(java.time.LocalTime v) {
        horarioAcordar = v;
    }

    public double getHorasDormidas() {
        return horasDormidas;
    }

    public void setHorasDormidas(double v) {
        horasDormidas = v;
    }

    public QualidadeSono getQualidade() {
        return qualidade;
    }

    public void setQualidade(QualidadeSono v) {
        qualidade = v;
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