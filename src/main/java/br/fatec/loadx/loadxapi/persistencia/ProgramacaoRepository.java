package br.fatec.loadx.loadxapi.persistencia;

import org.springframework.data.repository.ListCrudRepository;

// ============================================================
// ProgramacaoRepository.java  (NOVO)
// ------------------------------------------------------------
// Repositório da tabela "programacao": interface vazia, com a
// implementação gerada pelo Spring Data JDBC (mesmo padrão do
// VeiculoRepository e do ProdutoRepository).
// Ganhamos prontos: save, findAll, findById, deleteById e count.
// ============================================================
public interface ProgramacaoRepository extends ListCrudRepository<ProgramacaoRegistro, Long> {
}