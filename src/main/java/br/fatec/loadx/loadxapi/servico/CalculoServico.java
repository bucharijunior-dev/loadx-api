package br.fatec.loadx.loadxapi.servico;

import br.fatec.loadx.loadxapi.dominio.CalculoCarga;
import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.Veiculo;
import org.springframework.stereotype.Service;

// ============================================================
// CalculoServico.java  (NOVO)
// ------------------------------------------------------------
// Camada de SERVIÇO do cálculo: busca o veículo e o produto escolhidos
// na tela e monta o CalculoCarga (o motor de cálculo do domínio).
// Não grava nada: o cálculo é uma SIMULAÇÃO, como a tela de cálculo
// do LoadX em VBA antes de gravar a OT.
// ============================================================
@Service
public class CalculoServico {

    private final VeiculoServico veiculoServico;
    private final ProdutoServico produtoServico;

    public CalculoServico(VeiculoServico veiculoServico, ProdutoServico produtoServico) {
        this.veiculoServico = veiculoServico;
        this.produtoServico = produtoServico;
    }

    public CalculoCarga calcular(Long veiculoId, Long produtoId, double tara,
                                 double capacidadeTanqueLitros, double pedidoKg,
                                 double toleranciaPedidoPercentual, double minimoLitros,
                                 boolean dnit, boolean quebraOndas) {
        Veiculo veiculo = veiculoServico.buscar(veiculoId);
        Produto produto = produtoServico.buscar(produtoId);
        return new CalculoCarga(veiculo, produto, tara, capacidadeTanqueLitros,
                pedidoKg, toleranciaPedidoPercentual, minimoLitros, dnit, quebraOndas);
    }
}
