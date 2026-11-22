package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// ResultadoValidacao.java  (NOVO)
// ------------------------------------------------------------
// Resultado de UMA regra de validação do cálculo: o texto da
// justificativa e se a regra foi aprovada. É o equivalente aos pares
// msgs()/aprovados() do LoadX em VBA, agora reunidos em um objeto.
// ============================================================
public class ResultadoValidacao {

    private final String mensagem;
    private final boolean aprovado;

    public ResultadoValidacao(String mensagem, boolean aprovado) {
        this.mensagem = mensagem;
        this.aprovado = aprovado;
    }

    public String getMensagem() { return mensagem; }

    public boolean isAprovado() { return aprovado; }
}
