package br.fatec.loadx.loadxapi.persistencia;

import org.springframework.data.repository.ListCrudRepository;

// ============================================================
// ProdutoRepository.java  (NOVO)
// ------------------------------------------------------------
// Repositório da tabela "produto". Mesma ideia do VeiculoRepository:
// uma INTERFACE VAZIA; o Spring Data JDBC gera a implementação.
// Ao estender ListCrudRepository<ProdutoRegistro, Long> ganhamos
// prontos: save, findAll, findById, deleteById e count.
//
// Um exemplo de algo que o Spring faz a partir só do NOME do
// método (sem escrever SQL): a linha abaixo vira
//   SELECT ... FROM produto WHERE nome = ?
// ============================================================
public interface ProdutoRepository extends ListCrudRepository<ProdutoRegistro, Long> {

    // Consulta derivada do nome: "findBy" + nome do campo.
    java.util.Optional<ProdutoRegistro> findByNome(String nome);
}