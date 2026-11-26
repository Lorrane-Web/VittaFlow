package br.com.vittaflow.service;

import br.com.vittaflow.model.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class VittaService {
    @PersistenceContext
    private EntityManager em;

    public Usuario porEmail(String email) {
        try {
            return em.createQuery("select u from Usuario u where lower(u.email)=lower(:e)", Usuario.class)
                    .setParameter("e", email.trim()).getSingleResult();
        } catch (NoResultException e) { return null; }
    }

    public Usuario porId(Long id) { return em.find(Usuario.class, id); }

    public List<Profissional> profissionais(String termo) {
        String t = termo == null ? "" : termo.trim().toLowerCase();
        return em.createQuery("select p from Profissional p where lower(p.nome) like :t or lower(str(p.especialidade)) like :t order by p.nome", Profissional.class)
                .setParameter("t", "%" + t + "%").getResultList();
    }

    public Profissional profissional(Usuario u) {
        return em.createQuery("select p from Profissional p where p.usuario.id=:id", Profissional.class)
                .setParameter("id", u.getId()).getResultStream().findFirst().orElse(null);
    }

    public List<VinculoProfissional> vinculosUsuario(Usuario u) {
        return em.createQuery("select v from VinculoProfissional v join fetch v.profissional where v.usuario.id=:id order by v.dataSolicitacao desc", VinculoProfissional.class)
                .setParameter("id", u.getId()).getResultList();
    }

    public List<VinculoProfissional> solicitacoes(Profissional p) {
        return em.createQuery("select v from VinculoProfissional v join fetch v.usuario where v.profissional.id=:id and v.status=:s order by v.dataSolicitacao desc", VinculoProfissional.class)
                .setParameter("id", p.getId()).setParameter("s", StatusVinculo.PENDENTE).getResultList();
    }

    public List<VinculoProfissional> aceitos(Profissional p) {
        return em.createQuery("select v from VinculoProfissional v join fetch v.usuario where v.profissional.id=:id and v.status=:s order by v.usuario.nome", VinculoProfissional.class)
                .setParameter("id", p.getId()).setParameter("s", StatusVinculo.ACEITO).getResultList();
    }

    public boolean vinculoAceito(Profissional p, Usuario u) {
        Long n = em.createQuery("select count(v) from VinculoProfissional v where v.profissional.id=:p and v.usuario.id=:u and v.status=:s", Long.class)
                .setParameter("p", p.getId()).setParameter("u", u.getId()).setParameter("s", StatusVinculo.ACEITO).getSingleResult();
        return n > 0;
    }

    @Transactional
    public Usuario cadastrar(String nome, String email, String senha, boolean lgpd) {
        if (porEmail(email) != null) throw new IllegalArgumentException("E-mail já cadastrado.");
        if (!lgpd) throw new IllegalArgumentException("O consentimento LGPD é obrigatório.");
        if (senha == null || senha.length() < 4) throw new IllegalArgumentException("A senha deve possuir pelo menos 4 caracteres.");
        Usuario u = new Usuario(); u.setNome(nome.trim()); u.setEmail(email.trim().toLowerCase()); u.setSenha(senha); u.setConsentimentoLGPD(true); em.persist(u); return u;
    }

    @Transactional
    public void salvar(Object o) {
        if (o instanceof RegistroSono s) s.setHorasDormidas(horas(s.getHorarioDormir(), s.getHorarioAcordar()));
        if (em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(o) == null) em.persist(o); else em.merge(o);
    }

    public double horas(LocalTime dormir, LocalTime acordar) {
        long m = Duration.between(dormir, acordar).toMinutes(); if (m <= 0) m += 1440; return m / 60.0;
    }

    @Transactional public void excluir(Object o) { em.remove(em.contains(o) ? o : em.merge(o)); }
    @Transactional public void atualizar(Usuario u) { em.merge(u); }
    @Transactional public void atualizar(Profissional p) { em.merge(p); }

    @Transactional
    public void novaSenha(String email, String senha) {
        Usuario u = porEmail(email); if (u == null) throw new IllegalArgumentException("E-mail não encontrado.");
        if (senha == null || senha.length() < 4) throw new IllegalArgumentException("A nova senha deve possuir pelo menos 4 caracteres.");
        u.setSenha(senha); em.merge(u);
    }

    public List<RegistroRotina> rotinas(Usuario u) { return em.createQuery("select r from RegistroRotina r where r.usuario.id=:id order by r.data desc,r.id desc", RegistroRotina.class).setParameter("id",u.getId()).getResultList(); }
    public List<RegistroSono> sonos(Usuario u) { return em.createQuery("select r from RegistroSono r where r.usuario.id=:id order by r.data desc,r.id desc", RegistroSono.class).setParameter("id",u.getId()).getResultList(); }
    public List<RegistroAgua> aguas(Usuario u) { return em.createQuery("select r from RegistroAgua r where r.usuario.id=:id order by r.data desc,r.id desc", RegistroAgua.class).setParameter("id",u.getId()).getResultList(); }
    public List<RegistroPassos> passos(Usuario u) { return em.createQuery("select r from RegistroPassos r where r.usuario.id=:id order by r.data desc,r.id desc", RegistroPassos.class).setParameter("id",u.getId()).getResultList(); }
    public List<RegistroTreino> treinos(Usuario u) { return em.createQuery("select r from RegistroTreino r where r.usuario.id=:id order by r.data desc,r.id desc", RegistroTreino.class).setParameter("id",u.getId()).getResultList(); }
    public List<RegistroAlimentacao> alimentos(Usuario u) { return em.createQuery("select r from RegistroAlimentacao r where r.usuario.id=:id order by r.data desc,r.id desc", RegistroAlimentacao.class).setParameter("id",u.getId()).getResultList(); }

    public RegistroAgua aguaHoje(Usuario u) { return em.createQuery("select r from RegistroAgua r where r.usuario.id=:id and r.data=:d order by r.id desc", RegistroAgua.class).setParameter("id",u.getId()).setParameter("d",LocalDate.now()).setMaxResults(1).getResultStream().findFirst().orElse(null); }
    public RegistroSono sonoHoje(Usuario u) { return em.createQuery("select r from RegistroSono r where r.usuario.id=:id and r.data=:d order by r.id desc", RegistroSono.class).setParameter("id",u.getId()).setParameter("d",LocalDate.now()).setMaxResults(1).getResultStream().findFirst().orElse(null); }
    public RegistroPassos passosHoje(Usuario u) { return em.createQuery("select r from RegistroPassos r where r.usuario.id=:id and r.data=:d order by r.id desc", RegistroPassos.class).setParameter("id",u.getId()).setParameter("d",LocalDate.now()).setMaxResults(1).getResultStream().findFirst().orElse(null); }
    public RegistroTreino treinoHoje(Usuario u) { return em.createQuery("select r from RegistroTreino r where r.usuario.id=:id and r.data=:d order by r.id desc", RegistroTreino.class).setParameter("id",u.getId()).setParameter("d",LocalDate.now()).setMaxResults(1).getResultStream().findFirst().orElse(null); }
    public long academia(Usuario u,LocalDate inicio,LocalDate fim){return em.createQuery("select count(r) from RegistroTreino r where r.usuario.id=:id and r.checkInAcademia=true and r.data between :i and :f",Long.class).setParameter("id",u.getId()).setParameter("i",inicio).setParameter("f",fim).getSingleResult();}

    @Transactional
    public void solicitar(Usuario u, Profissional p) {
        Long n = em.createQuery("select count(v) from VinculoProfissional v where v.usuario.id=:u and v.profissional.id=:p and v.status in (:a,:b)",Long.class)
                .setParameter("u",u.getId()).setParameter("p",p.getId()).setParameter("a",StatusVinculo.PENDENTE).setParameter("b",StatusVinculo.ACEITO).getSingleResult();
        if(n>0) throw new IllegalArgumentException("Já existe vínculo pendente ou aceito.");
        VinculoProfissional v=new VinculoProfissional();v.setUsuario(u);v.setProfissional(p);v.setStatus(StatusVinculo.PENDENTE);v.setDataSolicitacao(LocalDateTime.now());em.persist(v);
    }

    @Transactional
    public void status(VinculoProfissional v, StatusVinculo s) { VinculoProfissional x=em.find(VinculoProfissional.class,v.getId()); if(x!=null)x.setStatus(s); }

    @Transactional
    public void solicitarConsulta(Usuario u, Profissional p) {
        if (!vinculoAceito(p, u)) throw new IllegalArgumentException("Aceite o vínculo com o profissional antes de solicitar uma consulta.");
        Long n=em.createQuery("select count(c) from ConsultaOnline c where c.usuario.id=:u and c.profissional.id=:p and c.status in (:s1,:s2)",Long.class)
                .setParameter("u",u.getId()).setParameter("p",p.getId()).setParameter("s1",StatusConsulta.SOLICITADA).setParameter("s2",StatusConsulta.AGENDADA).getSingleResult();
        if(n>0) throw new IllegalArgumentException("Já existe uma consulta online solicitada ou agendada.");
        ConsultaOnline c=new ConsultaOnline();c.setUsuario(u);c.setProfissional(p);c.setData(LocalDate.now());c.setHorario(LocalTime.of(19,0));c.setStatus(StatusConsulta.SOLICITADA);em.persist(c);
    }

    public List<ConsultaOnline> consultasProfissional(Profissional p){return em.createQuery("select c from ConsultaOnline c join fetch c.usuario where c.profissional.id=:id and c.status in (:s1,:s2) order by c.data,c.horario",ConsultaOnline.class).setParameter("id",p.getId()).setParameter("s1",StatusConsulta.SOLICITADA).setParameter("s2",StatusConsulta.AGENDADA).getResultList();}
    public List<ConsultaOnline> consultasUsuario(Usuario u){return em.createQuery("select c from ConsultaOnline c join fetch c.profissional where c.usuario.id=:id order by c.data desc,c.horario desc",ConsultaOnline.class).setParameter("id",u.getId()).getResultList();}

    @Transactional
    public void agendarConsulta(ConsultaOnline consulta) { ConsultaOnline x=em.find(ConsultaOnline.class,consulta.getId()); if(x!=null)x.setStatus(StatusConsulta.AGENDADA); }

    public List<Feedback> feedbacksDoProfissional(Profissional p){return em.createQuery("select f from Feedback f join fetch f.usuario where f.profissional.id=:id order by f.dataRegistro desc",Feedback.class).setParameter("id",p.getId()).getResultList();}
    public List<Feedback> feedbacksDoUsuario(Usuario u){return em.createQuery("select f from Feedback f join fetch f.profissional where f.usuario.id=:id order by f.dataRegistro desc",Feedback.class).setParameter("id",u.getId()).getResultList();}

    @Transactional
    public void registrarFeedback(Profissional p, Usuario u, String texto) {
        if (!vinculoAceito(p,u)) throw new IllegalArgumentException("O usuário não possui vínculo aceito com este profissional.");
        if(texto==null || texto.trim().length()<3) throw new IllegalArgumentException("Digite um feedback antes de enviar.");
        Feedback f=new Feedback();f.setProfissional(p);f.setUsuario(u);f.setTexto(texto.trim());f.setDataRegistro(LocalDateTime.now());em.persist(f);
    }

    @Transactional public void excluirUsuario(Usuario u){Usuario x=em.find(Usuario.class,u.getId());if(x!=null)em.remove(x);}

}
