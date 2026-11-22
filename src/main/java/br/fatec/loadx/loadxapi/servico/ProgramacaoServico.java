package br.fatec.loadx.loadxapi.servico;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.Programacao;
import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.persistencia.ProdutoMapeador;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRepository;
import br.fatec.loadx.loadxapi.persistencia.ProgramacaoMapeador;
import br.fatec.loadx.loadxapi.persistencia.ProgramacaoRegistro;
import br.fatec.loadx.loadxapi.persistencia.ProgramacaoRepository;
import br.fatec.loadx.loadxapi.persistencia.VeiculoMapeador;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

// ============================================================
// ProgramacaoServico.java  (NOVO)
// ------------------------------------------------------------
// É a camada de SERVIÇO: o "meio de campo" entre as telas (que virão
// depois) e o banco. Em VBA, seria o papel dos módulos que orquestram
// as classes (como o modSAP), chamando o modBD quando preciso.
//
// RESPONSABILIDADES:
//   - Juntar as peças: buscar Veiculo e Produto nos repositórios e
//     montar a Programacao completa.
//   - Aplicar as regras de negócio (o status muda pelos métodos do
//     próprio domínio) e gravar o resultado.
//   - Esconder do resto do sistema os detalhes de registro e mapeador.
//
// COMO FUNCIONA O SPRING AQUI:
//   - @Service: o Spring cria um objeto desta classe e o mantém
//     disponível para quem precisar.
//   - Os três repositórios chegam pelo construtor (injeção de
//     dependência), sem usar "new".
// ============================================================
@Service
public class ProgramacaoServico {

    private final ProgramacaoRepository programacaoRepositorio;
    private final VeiculoRepository veiculoRepositorio;
    private final ProdutoRepository produtoRepositorio;

    public ProgramacaoServico(ProgramacaoRepository programacaoRepositorio,
                              VeiculoRepository veiculoRepositorio,
                              ProdutoRepository produtoRepositorio) {
        this.programacaoRepositorio = programacaoRepositorio;
        this.veiculoRepositorio = veiculoRepositorio;
        this.produtoRepositorio = produtoRepositorio;
    }

    // --------------------------------------------------------
    // CRIAR: monta a Programacao (que já valida os dados e nasce
    // como AGENDADA) e grava no banco.
    // --------------------------------------------------------
    public Programacao criar(String ordemTransporte, Long veiculoId, Long produtoId,
                             double capacidadeTanqueLitros, LocalDateTime dataHoraPrevista) {
        Veiculo veiculo = buscarVeiculo(veiculoId);
        Produto produto = buscarProduto(produtoId);

        Programacao programacao = new Programacao(
                ordemTransporte, veiculo, produto, capacidadeTanqueLitros, dataHoraPrevista);

        ProgramacaoRegistro salvo = programacaoRepositorio.save(
                ProgramacaoMapeador.paraRegistro(programacao));
        programacao.setId(salvo.getId());
        return programacao;
    }

    // --------------------------------------------------------
    // LISTAR e BUSCAR
    // --------------------------------------------------------
    public List<Programacao> listar() {
        List<Programacao> resultado = new ArrayList<>();
        for (ProgramacaoRegistro registro : programacaoRepositorio.findAll()) {
            resultado.add(montar(registro));
        }
        return resultado;
    }

    public Programacao buscar(Long id) {
        ProgramacaoRegistro registro = programacaoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Programação não encontrada: " + id));
        return montar(registro);
    }

    // --------------------------------------------------------
    // MUDANÇAS DE STATUS
    // O serviço NÃO decide as regras: chama o método do domínio
    // (que lança exceção se a transição for inválida) e só então grava.
    // --------------------------------------------------------
    public Programacao iniciar(Long id) {
        Programacao p = buscar(id);
        p.iniciarCarregamento();
        salvar(p);
        return p;
    }

    public Programacao concluir(Long id) {
        Programacao p = buscar(id);
        p.concluir();
        salvar(p);
        return p;
    }

    public Programacao cancelar(Long id) {
        Programacao p = buscar(id);
        p.cancelar();
        salvar(p);
        return p;
    }

    // --------------------------------------------------------
    // EXCLUIR
    // --------------------------------------------------------
    public void excluir(Long id) {
        programacaoRepositorio.deleteById(id);
    }

    // --------------------------------------------------------
    // MÉTODOS AUXILIARES (privados: só esta classe usa)
    // --------------------------------------------------------
    private void salvar(Programacao programacao) {
        programacaoRepositorio.save(ProgramacaoMapeador.paraRegistro(programacao));
    }

    // Converte um registro (só com ids) em Programacao completa,
    // buscando o veículo e o produto correspondentes.
    private Programacao montar(ProgramacaoRegistro registro) {
        Veiculo veiculo = buscarVeiculo(registro.getVeiculoId());
        Produto produto = buscarProduto(registro.getProdutoId());
        return ProgramacaoMapeador.paraDominio(registro, veiculo, produto);
    }

    private Veiculo buscarVeiculo(Long id) {
        return veiculoRepositorio.findById(id)
                .map(VeiculoMapeador::paraDominio)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
    }

    private Produto buscarProduto(Long id) {
        return produtoRepositorio.findById(id)
                .map(ProdutoMapeador::paraDominio)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }
}