package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Profissional profissional;

    @Column(nullable = false, length = 1500)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime dataRegistro;

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario v) { usuario = v; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional v) { profissional = v; }
    public String getTexto() { return texto; }
    public void setTexto(String v) { texto = v; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime v) { dataRegistro = v; }
}
