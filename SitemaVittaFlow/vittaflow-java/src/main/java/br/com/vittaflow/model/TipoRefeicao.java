package br.com.vittaflow.model;

public enum TipoRefeicao {
    CAFE_DA_MANHA("Café da manhã"), ALMOCO("Almoço"), JANTAR("Jantar"), LANCHE("Lanche");

    private final String descricao;

    TipoRefeicao(String d) {
        descricao = d;
    }

    public String getDescricao() {
        return descricao;
    }
}
