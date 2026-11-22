package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// StatusProgramacao.java  (NOVO)
// ------------------------------------------------------------
// Um ENUM é um tipo com um conjunto FECHADO de valores possíveis.
// Em vez de guardar o status como texto livre ("Agendada",
// "agendada ", "AGENDADO"...), só existem os valores abaixo — um
// erro de digitação vira erro de compilação, não bug em produção.
//
// Em VBA o equivalente é "Enum" (Public Enum ... End Enum), mas
// o enum do Java é mais poderoso: cada valor pode ter campos e
// métodos, como a descrição abaixo.
// ============================================================
public enum StatusProgramacao {

    // Cada valor chama o construtor passando a descrição legível.
    AGENDADA("Agendada"),
    EM_CARREGAMENTO("Em carregamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusProgramacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}