package br.com.vittaflow.model;
import jakarta.persistence.*; import java.io.Serializable; import java.time.LocalDate;
@Entity @Table(name="registros_passos") public class RegistroPassos implements Serializable {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@ManyToOne(fetch=FetchType.LAZY,optional=false) private Usuario usuario;
@Column(nullable=false) private LocalDate data; @Column(nullable=false) private int quantidadePassos; @Column(nullable=false) private int metaPassos;
public LocalDate getData(){return data;} public void setData(LocalDate v){data=v;} public int getQuantidadePassos(){return quantidadePassos;} public void setQuantidadePassos(int v){quantidadePassos=v;}
public int getMetaPassos(){return metaPassos;} public void setMetaPassos(int v){metaPassos=v;}
public Long getId(){return id;} public Usuario getUsuario(){return usuario;} public void setUsuario(Usuario v){usuario=v;}
}