package br.fatec.loadx.loadxapi.servico;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.persistencia.ProdutoMapeador;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRegistro;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

// ============================================================
// ProdutoServico.java  (NOVO)
// ------------------------------------------------------------
// Camada de SERVIÇO do produto. Mesmo papel do VeiculoServico, com
// uma regra de negócio a mais: o NOME do produto não pode se repetir.
// (O banco também garante isso com UNIQUE, mas conferir antes nos dá
// uma mensagem clara para o usuário.)
// ============================================================
@Service
public class ProdutoServico {

    private final ProdutoRepository repositorio;

    public ProdutoServico(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Produto> listar() {
        List<Produto> resultado = new ArrayList<>();
        for (ProdutoRegistro registro : repositorio.findAll()) {
            resultado.add(ProdutoMapeador.paraDominio(registro));
        }
        return resultado;
    }

    public Produto buscar(Long id) {
        return repositorio.findById(id)
                .map(ProdutoMapeador::paraDominio)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    // SALVAR com checagem de nome duplicado.
    // Usa findByNome (consulta derivada do ProdutoRepository). Se já
    // existe um produto com esse nome E não é o mesmo que estamos
    // editando (id diferente), recusa.
    public Produto salvar(Produto produto) {
        repositorio.findByNome(produto.getNome()).ifPresent(existente -> {
            if (!existente.getId().equals(produto.getId())) {
                throw new IllegalArgumentException(
                        "Já existe um produto com o nome " + produto.getNome() + ".");
            }
        });

        ProdutoRegistro salvo = repositorio.save(ProdutoMapeador.paraRegistro(produto));
        produto.setId(salvo.getId());
        return produto;
    }

    public void excluir(Long id) {
        repositorio.deleteById(id);
    }
}
