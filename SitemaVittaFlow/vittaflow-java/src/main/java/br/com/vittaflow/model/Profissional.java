package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "profissionais")
public class Profissional implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    @Column(nullable = false)
    private String nome;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidade especialidade;
    @Column(length = 1000)
    private String bio;
    @Column(nullable = false)
    private String registroProfissional;
    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VinculoProfissional> vinculos = new ArrayList<>();
    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();
    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultaOnline> consultasOnline = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario v) {
        usuario = v;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String v) {
        nome = v;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade v) {
        especialidade = v;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String v) {
        bio = v;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String v) {
        registroProfissional = v;
    }

    public List<VinculoProfissional> getVinculos() {
        return vinculos;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public List<ConsultaOnline> getConsultasOnline() {
        return consultasOnline;
    }
}
