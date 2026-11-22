package br.fatec.loadx.loadxapi;

import br.fatec.loadx.loadxapi.dominio.Programacao;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRegistro;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRepository;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRegistro;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRepository;
import br.fatec.loadx.loadxapi.servico.ProgramacaoServico;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// ============================================================
// DemoProgramacao.java  (NOVO — TEMPORÁRIO)
// ------------------------------------------------------------
// Como as outras demos, serve só para você VER o fluxo completo
// funcionando no console. Sai quando as telas existirem.
//
// Demonstra o ciclo de vida de uma programação passando pelo
// SERVIÇO: criar -> iniciar -> conferir o peso da balança ->
// concluir -> listar. Cada mudança é gravada no PostgreSQL.
//
// PRÉ-REQUISITO: as tabelas de veículo e produto já devem ter
// dados (as demos DemoVeiculo e DemoProduto os gravam na primeira
// execução). Se estiverem vazias, a demo avisa e não faz nada.
// ============================================================
@Component
public class DemoProgramacao implements CommandLineRunner {

    private final ProgramacaoServico servico;
    private final VeiculoRepository veiculoRepositorio;
    private final ProdutoRepository produtoRepositorio;

    public DemoProgramacao(ProgramacaoServico servico,
                           VeiculoRepository veiculoRepositorio,
                           ProdutoRepository produtoRepositorio) {
        this.servico = servico;
        this.veiculoRepositorio = veiculoRepositorio;
        this.produtoRepositorio = produtoRepositorio;
    }

    @Override
    public void run(String... args) {

        List<VeiculoRegistro> veiculos = veiculoRepositorio.findAll();
        List<ProdutoRegistro> produtos = produtoRepositorio.findAll();

        if (veiculos.isEmpty() || produtos.isEmpty()) {
            System.out.println(">>> DemoProgramacao: faltam veículos ou produtos no banco."
                    + " Rode a aplicação novamente.");
            return;
        }

        // Cria e percorre o ciclo de vida só se ainda não há programações.
        if (servico.listar().isEmpty()) {
            Long veiculoId = veiculos.get(0).getId();
            Long produtoId = produtos.get(0).getId();

            Programacao p = servico.criar("OT-0001", veiculoId, produtoId,
                    15000, LocalDateTime.of(2026, 11, 20, 8, 30));
            System.out.println(">>> Criada: " + p);

            p = servico.iniciar(p.getId());
            System.out.println(">>> Após iniciar: " + p.getStatus().getDescricao());

            System.out.println(">>> Peso previsto: " + p.calcularPesoPrevisto() + " kg");
            System.out.println(">>> Balança 11.500 kg confere? " + p.pesoConfereComPrevisto(11500));

            p = servico.concluir(p.getId());
            System.out.println(">>> Após concluir: " + p.getStatus().getDescricao());

            // Regra de negócio: não se cancela o que já foi concluído.
            try {
                servico.cancelar(p.getId());
            } catch (IllegalStateException e) {
                System.out.println(">>> Operação recusada: " + e.getMessage());
            }
        }

        System.out.println(">>> Programações no banco:");
        for (Programacao p : servico.listar()) {
            System.out.println("    #" + p.getId() + " " + p);
        }
    }
}