package br.com.vittaflow.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "consultas_online")
public class ConsultaOnline implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Profissional profissional;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsulta status;

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario v) { usuario = v; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional v) { profissional = v; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate v) { data = v; }
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime v) { horario = v; }
    public StatusConsulta getStatus() { return status; }
    public void setStatus(StatusConsulta v) { status = v; }
}
