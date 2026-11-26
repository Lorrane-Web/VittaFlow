package br.com.vittaflow.model;

public enum StatusConsulta {
    SOLICITADA("Solicitada"), AGENDADA("Agendada"), REALIZADA("Realizada");

    private final String descricao;
    StatusConsulta(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
