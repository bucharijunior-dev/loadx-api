package br.fatec.loadx.loadxapi.persistencia;

import org.springframework.data.repository.ListCrudRepository;

// ============================================================
// VeiculoRepository.java  (NOVO)
// ------------------------------------------------------------
// O REPOSITÓRIO é a camada que conversa com o banco. Em VBA, o
// equivalente seria o modBD (ObterConexao, INSERT, SELECT...).
//
// O DETALHE IMPORTANTE: isto é uma INTERFACE VAZIA. Não há nenhum
// código SQL escrito. Ao subir, o Spring Data JDBC GERA a
// implementação sozinho, com base nos dois tipos entre os sinais
// < > : a classe da tabela (VeiculoRegistro) e o tipo da chave
// primária (Long).
//
// Ao estender ListCrudRepository, já ganhamos prontos:
//   save(obj)        -> INSERT (se id for null) ou UPDATE
//   findAll()        -> SELECT * (devolve uma lista)
//   findById(id)     -> SELECT por chave primária
//   deleteById(id)   -> DELETE
//   count()          -> quantidade de linhas
//
// Para o CRUD mínimo exigido (criar, ler, atualizar, excluir),
// estes métodos já bastam.
// ============================================================
public interface VeiculoRepository extends ListCrudRepository<VeiculoRegistro, Long> {
}