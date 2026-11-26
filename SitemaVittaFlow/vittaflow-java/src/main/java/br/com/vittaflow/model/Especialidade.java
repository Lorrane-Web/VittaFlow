package br.com.vittaflow.model;

public enum Especialidade {
    PSICOLOGIA("Psicologia"), NUTRICAO("Nutrição"), EDUCACAO_FISICA("Educação Física");

    private final String descricao;

    Especialidade(String d) {
        descricao = d;
    }

    public String getDescricao() {
        return descricao;
    }
}
