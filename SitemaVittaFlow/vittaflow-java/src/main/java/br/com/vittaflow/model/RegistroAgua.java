package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "registros_agua")
public class RegistroAgua implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private int quantidadeMl;
    @Column(nullable = false)
    private int metaDiaria;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate v) {
        data = v;
    }

    public int getQuantidadeMl() {
        return quantidadeMl;
    }

    public void setQuantidadeMl(int v) {
        quantidadeMl = v;
    }

    public int getMetaDiaria() {
        return metaDiaria;
    }

    public void setMetaDiaria(int v) {
        metaDiaria = v;
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