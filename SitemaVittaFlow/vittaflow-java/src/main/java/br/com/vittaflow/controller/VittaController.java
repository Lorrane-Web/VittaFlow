package br.com.vittaflow.controller;

import br.com.vittaflow.model.*;
import br.com.vittaflow.service.VittaService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Component("vittaController")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class VittaController implements Serializable {
    private final VittaService service;
    private Usuario usuarioLogado;
    private String emailLogin, senhaLogin, nomeCadastro, emailCadastro, senhaCadastro, confirmacaoSenha,
            emailRecuperacao, novaSenha, buscaProfissional;
    private boolean consentimentoLGPD;
    private Profissional profissionalEdicao;
    private RegistroRotina rotina = new RegistroRotina();
    private RegistroSono sono = sonoNovo();
    private RegistroAgua agua = aguaNovo();
    private RegistroPassos passos = passosNovo();
    private RegistroTreino treino = treinoNovo();
    private RegistroAlimentacao alimentacao = alimentoNovo();
    private List<RegistroRotina> rotinas = new ArrayList<>();
    private List<RegistroSono> sonos = new ArrayList<>();
    private List<RegistroAgua> aguas = new ArrayList<>();
    private List<RegistroPassos> passosHistorico = new ArrayList<>();
    private List<RegistroTreino> treinos = new ArrayList<>();
    private List<RegistroAlimentacao> alimentacoes = new ArrayList<>();
    private List<Profissional> profissionais = new ArrayList<>();
    private List<VinculoProfissional> vinculos = new ArrayList<>(), solicitacoes = new ArrayList<>(),
            vinculados = new ArrayList<>();
    private List<ConsultaOnline> consultasOnline = new ArrayList<>();
    private List<Feedback> feedbacks = new ArrayList<>(), feedbacksUsuarioSelecionado = new ArrayList<>();
    private Usuario usuarioSelecionado;
    private Long usuarioSelecionadoId;
    private String textoFeedback;
    private ConsultaOnline consultaSelecionada;
    private String graficoAguaJson, graficoPassosJson, graficoSonoJson;

    public VittaController(VittaService s) {
        service = s;
    }

    @PostConstruct
    public void init() {
        carregar();
    }

    public void carregar() {
        if (usuarioLogado == null)
            return;
        usuarioLogado = service.porId(usuarioLogado.getId());
        profissionais = service.profissionais(buscaProfissional);
        vinculos = service.vinculosUsuario(usuarioLogado);
        feedbacks = service.feedbacksDoUsuario(usuarioLogado);
        consultasOnline = service.consultasUsuario(usuarioLogado);
        if (profissional()) {
            profissionalEdicao = service.profissional(usuarioLogado);
            solicitacoes = service.solicitacoes(profissionalEdicao);
            vinculados = service.aceitos(profissionalEdicao);
            consultasOnline = service.consultasProfissional(profissionalEdicao);
            feedbacks = service.feedbacksDoProfissional(profissionalEdicao);
        } else {
            rotinas = service.rotinas(usuarioLogado);
            sonos = service.sonos(usuarioLogado);
            aguas = service.aguas(usuarioLogado);
            passosHistorico = service.passos(usuarioLogado);
            treinos = service.treinos(usuarioLogado);
            alimentacoes = service.alimentos(usuarioLogado);
            graficoAguaJson = chart("Água (ml)", aguas, true);
            graficoPassosJson = chart("Passos", passosHistorico, false);
        }
        if (usuarioSelecionado != null && profissional()) {
            usuarioSelecionado = service.porId(usuarioSelecionado.getId());
            if (!service.vinculoAceito(profissionalEdicao, usuarioSelecionado)) {
                usuarioSelecionado = null;
                feedbacksUsuarioSelecionado = new ArrayList<>();
            } else {
                rotinas = service.rotinas(usuarioSelecionado);
                sonos = service.sonos(usuarioSelecionado);
                aguas = service.aguas(usuarioSelecionado);
                passosHistorico = service.passos(usuarioSelecionado);
                treinos = service.treinos(usuarioSelecionado);
                alimentacoes = service.alimentos(usuarioSelecionado);
                feedbacksUsuarioSelecionado = service.feedbacksDoUsuario(usuarioSelecionado).stream()
                        .filter(f -> f.getProfissional().getId().equals(profissionalEdicao.getId())).toList();
                graficoAguaJson = chart("Água (ml)", service.aguas(usuarioSelecionado), true);
                graficoPassosJson = chart("Passos", service.passos(usuarioSelecionado), false);
                graficoSonoJson = chartSono(service.sonos(usuarioSelecionado));
            }
        }
    }

    private String chart(String label, List<?> registros, boolean aguaChart) {
        List<String> labels = new ArrayList<>();
        List<Integer> vals = new ArrayList<>();
        registros.stream().limit(7).sorted(Comparator.comparing(x -> dataRegistro(x))).forEach(x -> {
            labels.add("'" + dataRegistro(x).toString().substring(5).replace('-', '/') + "'");
            vals.add(aguaChart ? ((RegistroAgua) x).getQuantidadeMl() : ((RegistroPassos) x).getQuantidadePassos());
        });
        return "{type:'line',data:{labels:[" + String.join(",", labels) + "],datasets:[{label:'" + label + "',data:"
                + vals + ",tension:.35}]},options:{responsive:true,maintainAspectRatio:false}}";
    }

    private LocalDate dataRegistro(Object o) {
        if (o instanceof RegistroAgua x)
            return x.getData();
        if (o instanceof RegistroPassos x)
            return x.getData();
        if (o instanceof RegistroSono x)
            return x.getData();
        return LocalDate.now();
    }

    private String chartSono(List<RegistroSono> registros) {
        List<String> labels = new ArrayList<>();
        List<Double> vals = new ArrayList<>();
        registros.stream().limit(7).sorted(Comparator.comparing(RegistroSono::getData)).forEach(x -> {
            labels.add("'" + x.getData().toString().substring(5).replace('-', '/') + "'");
            vals.add(x.getHorasDormidas());
        });
        return "{type:'line',data:{labels:[" + String.join(",", labels) + "],datasets:[{label:'Horas de sono',data:"
                + vals + ",tension:.35}]},options:{responsive:true,maintainAspectRatio:false}}";
    }

    public String login() {
        Usuario u = service.porEmail(emailLogin);
        if (u == null || !u.getSenha().equals(senhaLogin)) {
            msg(FacesMessage.SEVERITY_ERROR, "Login", "E-mail ou senha inválidos.");
            return null;
        }
        usuarioLogado = u;
        usuarioSelecionado = null;
        carregar();
        return "/dashboard.xhtml?faces-redirect=true";
    }

    public String cadastrar() {
        try {
            if (!Objects.equals(senhaCadastro, confirmacaoSenha))
                throw new IllegalArgumentException("As senhas não coincidem.");
            service.cadastrar(nomeCadastro, emailCadastro, senhaCadastro, consentimentoLGPD);
            msg(FacesMessage.SEVERITY_INFO, "Cadastro", "Conta criada. Faça o login.");
            limparCadastro();
            return "/login.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            msg(FacesMessage.SEVERITY_ERROR, "Cadastro", e.getMessage());
            return null;
        }
    }

    public void recuperarSenha() {
        try {
            service.novaSenha(emailRecuperacao, novaSenha);
            msg(FacesMessage.SEVERITY_INFO, "Senha", "Senha alterada no fluxo demonstrativo.");
        } catch (Exception e) {
            msg(FacesMessage.SEVERITY_ERROR, "Recuperação", e.getMessage());
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public void salvarRotina() {
        rotina.setUsuario(usuarioLogado);
        if (rotina.getData() == null)
            rotina.setData(LocalDate.now());
        service.salvar(rotina);
        rotina = new RegistroRotina();
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Rotina", "Registro salvo.");
    }

    public void salvarSono() {
        sono.setUsuario(usuarioLogado);
        if (sono.getData() == null)
            sono.setData(LocalDate.now());
        service.salvar(sono);
        sono = sonoNovo();
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Sono", "Registro salvo e horas calculadas.");
    }

    public void salvarAgua() {
        agua.setUsuario(usuarioLogado);
        if (agua.getData() == null)
            agua.setData(LocalDate.now());
        service.salvar(agua);
        agua = aguaNovo();
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Água", "Registro salvo.");
    }

    public void salvarPassos() {
        passos.setUsuario(usuarioLogado);
        if (passos.getData() == null)
            passos.setData(LocalDate.now());
        service.salvar(passos);
        passos = passosNovo();
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Passos", "Registro salvo.");
    }

    public void salvarTreino() {
        treino.setUsuario(usuarioLogado);
        if (treino.getData() == null)
            treino.setData(LocalDate.now());
        service.salvar(treino);
        treino = treinoNovo();
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Treino", "Registro salvo.");
    }

    public void fuiHoje() {
        RegistroTreino t = treinoNovo();
        t.setUsuario(usuarioLogado);
        t.setTipoExercicio("Check-in academia");
        t.setDuracao(1);
        t.setCheckInAcademia(true);
        t.setObservacoes("Presença registrada pelo botão Fui hoje.");
        service.salvar(t);
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Academia", "Check-in registrado.");
    }

    public void salvarAlimentacao() {
        alimentacao.setUsuario(usuarioLogado);
        if (alimentacao.getData() == null)
            alimentacao.setData(LocalDate.now());
        service.salvar(alimentacao);
        alimentacao = alimentoNovo();
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Alimentação", "Refeição registrada.");
    }

    public void excluir(Object o) {
        service.excluir(o);
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Registro", "Registro excluído.");
    }

    public void pesquisar() {
        profissionais = service.profissionais(buscaProfissional);
    }

    public void solicitar(Profissional p) {
        try {
            service.solicitar(usuarioLogado, p);
            msg(FacesMessage.SEVERITY_INFO, "Vínculo", "Solicitação enviada. Aguarde o aceite do profissional.");
            carregar();
        } catch (Exception e) {
            msg(FacesMessage.SEVERITY_WARN, "Vínculo", e.getMessage());
        }
    }

    public void solicitarConsulta(Profissional p) {
        try {
            service.solicitarConsulta(usuarioLogado, p);
            msg(FacesMessage.SEVERITY_INFO, "Consulta online", "Solicitação de consulta enviada ao profissional.");
            carregar();
        } catch (Exception e) {
            msg(FacesMessage.SEVERITY_WARN, "Consulta online", e.getMessage());
        }
    }

    public void aceitar(VinculoProfissional v) {
        service.status(v, StatusVinculo.ACEITO);
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Vínculo", "Usuário aceito no acompanhamento.");
    }

    public void recusar(VinculoProfissional v) {
        service.status(v, StatusVinculo.RECUSADO);
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Vínculo", "Solicitação recusada.");
    }

    public void selecionarUsuario(VinculoProfissional v) {
        if (!profissional())
            return;
        usuarioSelecionado = v.getUsuario();
        usuarioSelecionadoId = usuarioSelecionado.getId();
        carregar();
    }

    public String selecionarUsuarioPorConsulta(ConsultaOnline c) {
        if (!profissional())
            return null;
        usuarioSelecionado = c.getUsuario();
        usuarioSelecionadoId = usuarioSelecionado.getId();
        carregar();
        return "/acompanhamento.xhtml?faces-redirect=true";
    }

    public void limparUsuarioSelecionado() {
        usuarioSelecionado = null;
        feedbacksUsuarioSelecionado = new ArrayList<>();
    }

    public void salvarFeedback() {
        try {
            if (usuarioSelecionadoId != null)
                usuarioSelecionado = service.porId(usuarioSelecionadoId);
            service.registrarFeedback(profissionalEdicao, usuarioSelecionado, textoFeedback);
            textoFeedback = "";
            carregar();
            msg(FacesMessage.SEVERITY_INFO, "Feedback", "Feedback registrado para o usuário.");
        } catch (Exception e) {
            msg(FacesMessage.SEVERITY_WARN, "Feedback", e.getMessage());
        }
    }

    public void prepararFeedback(Usuario u) {
        usuarioSelecionado = u;
        usuarioSelecionadoId = u == null ? null : u.getId();
        carregar();
    }

    public void agendarConsulta(ConsultaOnline c) {
        service.agendarConsulta(c);
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Consulta", "Consulta marcada como agendada.");
    }

    public void salvarPerfil() {
        service.atualizar(usuarioLogado);
        if (profissional() && profissionalEdicao != null) {
            profissionalEdicao.setNome(usuarioLogado.getNome());
            service.atualizar(profissionalEdicao);
        }
        carregar();
        msg(FacesMessage.SEVERITY_INFO, "Perfil", "Perfil atualizado.");
    }

    public String excluirConta() {
        service.excluirUsuario(usuarioLogado);
        usuarioLogado = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public int progressoAgua() {
        RegistroAgua a = service.aguaHoje(usuarioLogado);
        return a == null || a.getMetaDiaria() <= 0 ? 0
                : Math.min(100, Math.max(0, a.getQuantidadeMl() * 100 / a.getMetaDiaria()));
    }

    public int progressoPassos() {
        RegistroPassos p = service.passosHoje(usuarioLogado);
        return p == null || p.getMetaPassos() <= 0 ? 0
                : Math.min(100, Math.max(0, p.getQuantidadePassos() * 100 / p.getMetaPassos()));
    }

    public String aguaHoje() {
        RegistroAgua a = service.aguaHoje(usuarioLogado);
        return a == null ? "0 ml / 0 ml" : a.getQuantidadeMl() + " ml / " + a.getMetaDiaria() + " ml";
    }

    public String passosHoje() {
        RegistroPassos p = service.passosHoje(usuarioLogado);
        return p == null ? "0 / 0" : p.getQuantidadePassos() + " / " + p.getMetaPassos();
    }

    public String sonoHoje() {
        RegistroSono s = service.sonoHoje(usuarioLogado);
        return s == null ? "Sem registro" : String.format("%.1f h", s.getHorasDormidas());
    }

    public String treinoHoje() {
        RegistroTreino t = service.treinoHoje(usuarioLogado);
        return t == null ? "Sem treino" : t.getDuracao() + " min";
    }

    public long academiaSemana() {
        LocalDate h = LocalDate.now();
        return service.academia(usuarioLogado, h.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), h);
    }

    public long academiaMes() {
        LocalDate h = LocalDate.now();
        return service.academia(usuarioLogado, h.withDayOfMonth(1), h);
    }

    public boolean isAutenticado() {
        return usuarioLogado != null;
    }

    public boolean profissional() {
        return usuarioLogado != null && usuarioLogado.getTipoUsuario() == TipoUsuario.PROFISSIONAL;
    }

    public boolean isProfissional() {
        return profissional();
    }

    public boolean getProfissional() {
        return profissional();
    }

    public int usuariosAcompanhamento() {
        return vinculados.size();
    }

    public int consultasPendentes() {
        return (int) consultasOnline.stream().filter(c -> c.getStatus() == StatusConsulta.SOLICITADA).count();
    }

    public int feedbacksEnviados() {
        return feedbacks.size();
    }

    public String progressoAguaUsuarioSelecionado() {
        return usuarioSelecionado == null ? "0%"
                : percent(
                        service.aguaHoje(usuarioSelecionado) == null ? 0
                                : service.aguaHoje(usuarioSelecionado).getQuantidadeMl(),
                        service.aguaHoje(usuarioSelecionado) == null ? 0
                                : service.aguaHoje(usuarioSelecionado).getMetaDiaria());
    }

    public String progressoPassosUsuarioSelecionado() {
        return usuarioSelecionado == null ? "0%"
                : percent(
                        service.passosHoje(usuarioSelecionado) == null ? 0
                                : service.passosHoje(usuarioSelecionado).getQuantidadePassos(),
                        service.passosHoje(usuarioSelecionado) == null ? 0
                                : service.passosHoje(usuarioSelecionado).getMetaPassos());
    }

    private String percent(int a, int b) {
        return b <= 0 ? "0%" : Math.min(100, Math.max(0, a * 100 / b)) + "%";
    }

    public String aguaUsuarioSelecionado() {
        if (usuarioSelecionado == null)
            return "0 ml / 0 ml";
        RegistroAgua a = service.aguaHoje(usuarioSelecionado);
        return a == null ? "0 ml / 0 ml" : a.getQuantidadeMl() + " ml / " + a.getMetaDiaria() + " ml";
    }

    public String passosUsuarioSelecionado() {
        if (usuarioSelecionado == null)
            return "0 / 0";
        RegistroPassos p = service.passosHoje(usuarioSelecionado);
        return p == null ? "0 / 0" : p.getQuantidadePassos() + " / " + p.getMetaPassos();
    }

    public String sonoUsuarioSelecionado() {
        if (usuarioSelecionado == null)
            return "Sem registro";
        RegistroSono s = service.sonoHoje(usuarioSelecionado);
        return s == null ? "Sem registro" : String.format("%.1f h", s.getHorasDormidas());
    }

    public String treinoUsuarioSelecionado() {
        if (usuarioSelecionado == null)
            return "Sem treino";
        RegistroTreino t = service.treinoHoje(usuarioSelecionado);
        return t == null ? "Sem treino" : t.getDuracao() + " min";
    }

    public long academiaSemanaUsuarioSelecionado() {
        if (usuarioSelecionado == null)
            return 0;
        LocalDate h = LocalDate.now();
        return service.academia(usuarioSelecionado, h.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), h);
    }

    private void msg(FacesMessage.Severity s, String a, String b) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, a, b));
    }

    private void limparCadastro() {
        nomeCadastro = emailCadastro = senhaCadastro = confirmacaoSenha = "";
        consentimentoLGPD = false;
    }

    private static RegistroSono sonoNovo() {
        RegistroSono x = new RegistroSono();
        x.setData(LocalDate.now());
        x.setHorarioDormir(LocalTime.of(23, 0));
        x.setHorarioAcordar(LocalTime.of(7, 0));
        x.setQualidade(QualidadeSono.BOA);
        return x;
    }

    private static RegistroAgua aguaNovo() {
        RegistroAgua x = new RegistroAgua();
        x.setData(LocalDate.now());
        x.setMetaDiaria(2000);
        return x;
    }

    private static RegistroPassos passosNovo() {
        RegistroPassos x = new RegistroPassos();
        x.setData(LocalDate.now());
        x.setMetaPassos(10000);
        return x;
    }

    private static RegistroTreino treinoNovo() {
        RegistroTreino x = new RegistroTreino();
        x.setData(LocalDate.now());
        return x;
    }

    private static RegistroAlimentacao alimentoNovo() {
        RegistroAlimentacao x = new RegistroAlimentacao();
        x.setData(LocalDate.now());
        x.setTipoRefeicao(TipoRefeicao.CAFE_DA_MANHA);
        return x;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public String getEmailLogin() {
        return emailLogin;
    }

    public void setEmailLogin(String v) {
        emailLogin = v;
    }

    public String getSenhaLogin() {
        return senhaLogin;
    }

    public void setSenhaLogin(String v) {
        senhaLogin = v;
    }

    public String getNomeCadastro() {
        return nomeCadastro;
    }

    public void setNomeCadastro(String v) {
        nomeCadastro = v;
    }

    public String getEmailCadastro() {
        return emailCadastro;
    }

    public void setEmailCadastro(String v) {
        emailCadastro = v;
    }

    public String getSenhaCadastro() {
        return senhaCadastro;
    }

    public void setSenhaCadastro(String v) {
        senhaCadastro = v;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String v) {
        confirmacaoSenha = v;
    }

    public boolean isConsentimentoLGPD() {
        return consentimentoLGPD;
    }

    public void setConsentimentoLGPD(boolean v) {
        consentimentoLGPD = v;
    }

    public String getEmailRecuperacao() {
        return emailRecuperacao;
    }

    public void setEmailRecuperacao(String v) {
        emailRecuperacao = v;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String v) {
        novaSenha = v;
    }

    public String getBuscaProfissional() {
        return buscaProfissional;
    }

    public void setBuscaProfissional(String v) {
        buscaProfissional = v;
    }

    public Profissional getProfissionalEdicao() {
        return profissionalEdicao;
    }

    public RegistroRotina getRotina() {
        return rotina;
    }

    public RegistroSono getSono() {
        return sono;
    }

    public RegistroAgua getAgua() {
        return agua;
    }

    public RegistroPassos getPassos() {
        return passos;
    }

    public RegistroTreino getTreino() {
        return treino;
    }

    public RegistroAlimentacao getAlimentacao() {
        return alimentacao;
    }

    public List<RegistroRotina> getRotinas() {
        return rotinas;
    }

    public List<RegistroSono> getSonos() {
        return sonos;
    }

    public List<RegistroAgua> getAguas() {
        return aguas;
    }

    public List<RegistroPassos> getPassosHistorico() {
        return passosHistorico;
    }

    public List<RegistroTreino> getTreinos() {
        return treinos;
    }

    public List<RegistroAlimentacao> getAlimentacoes() {
        return alimentacoes;
    }

    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    public List<VinculoProfissional> getVinculos() {
        return vinculos;
    }

    public List<VinculoProfissional> getSolicitacoes() {
        return solicitacoes;
    }

    public List<VinculoProfissional> getVinculados() {
        return vinculados;
    }

    public List<ConsultaOnline> getConsultasOnline() {
        return consultasOnline;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public List<Feedback> getFeedbacksUsuarioSelecionado() {
        return feedbacksUsuarioSelecionado;
    }

    public Usuario getUsuarioSelecionado() {
        return usuarioSelecionado;
    }

    public Long getUsuarioSelecionadoId() {
        return usuarioSelecionadoId;
    }

    public void setUsuarioSelecionadoId(Long v) {
        usuarioSelecionadoId = v;
    }

    public String getTextoFeedback() {
        return textoFeedback;
    }

    public void setTextoFeedback(String v) {
        textoFeedback = v;
    }

    public ConsultaOnline getConsultaSelecionada() {
        return consultaSelecionada;
    }

    public void setConsultaSelecionada(ConsultaOnline v) {
        consultaSelecionada = v;
    }

    public String getGraficoAguaJson() {
        return graficoAguaJson;
    }

    public String getGraficoPassosJson() {
        return graficoPassosJson;
    }

    public String getGraficoSonoJson() {
        return graficoSonoJson;
    }

    public TipoRefeicao[] getTiposRefeicao() {
        return TipoRefeicao.values();
    }

    public QualidadeSono[] getQualidadesSono() {
        return QualidadeSono.values();
    }

    public Especialidade[] getEspecialidades() {
        return Especialidade.values();
    }
}
