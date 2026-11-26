package br.com.vittaflow.model;

public enum QualidadeSono {
    RUIM("Ruim"), REGULAR("Regular"), BOA("Boa");

    private final String descricao;

    QualidadeSono(String d) {
        descricao = d;
    }

    public String getDescricao() {
        return descricao;
    }
}
