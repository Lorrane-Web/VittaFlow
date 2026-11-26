package br.com.vittaflow.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.USUARIO;
    @Column(nullable = false)
    private boolean consentimentoLGPD;
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profissional profissional;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroRotina> registrosRotina = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroSono> registrosSono = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroAgua> registrosAgua = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroPassos> registrosPassos = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroTreino> registrosTreino = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroAlimentacao> registrosAlimentacao = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VinculoProfissional> vinculos = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultaOnline> consultasOnline = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String v) {
        nome = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String v) {
        senha = v;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario v) {
        tipoUsuario = v;
    }

    public boolean isConsentimentoLGPD() {
        return consentimentoLGPD;
    }

    public void setConsentimentoLGPD(boolean v) {
        consentimentoLGPD = v;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional v) {
        profissional = v;
    }

    public List<RegistroAgua> getRegistrosAgua() {
        return registrosAgua;
    }

    public List<RegistroSono> getRegistrosSono() {
        return registrosSono;
    }

    public List<RegistroTreino> getRegistrosTreino() {
        return registrosTreino;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public List<ConsultaOnline> getConsultasOnline() {
        return consultasOnline;
    }
}
