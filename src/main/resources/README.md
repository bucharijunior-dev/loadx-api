# LoadX (Java)

Sistema de cálculo e programação de carregamentos de veículos com produtos químicos,
migrado de um projeto em Excel VBA para Java. Projeto da disciplina de Programação
Orientada a Objetos (Fatec, ADS). **Todos os dados são fictícios.**

## Tecnologias
- Java 21, Spring Boot, Spring Data JDBC
- Thymeleaf (telas), PostgreSQL
- Configuração em `application.yaml`

## Funcionalidades
- **Veículos** e **Produtos**: cadastro completo (criar, listar, editar, excluir).
- **Programações**: agendar carregamento, calcular litros máximos e controlar o status
  (Agendada, Em carregamento, Concluída, Cancelada); conferência de peso pela tolerância.
- **Cálculo**: simulação com veículo, produto, tara, pedido, tolerância, DNIT (+5% PBTC)
  e quebra-ondas, com as 5 regras de validação.

## Arquitetura
Navegador (Thymeleaf) → Controller → Serviço → Repository (Spring Data JDBC) → PostgreSQL.
Pacotes: `dominio` (regras de negócio), `persistencia`, `servico`, `web`.

## Os quatro pilares da POO
- **Abstração:** `Veiculo` e `Produto` são classes abstratas.
- **Encapsulamento:** atributos privados com validação nos setters; o status da programação
  só muda por métodos (`iniciarCarregamento`, `concluir`, `cancelar`).
- **Herança:** `Caminhao` e `Carreta` herdam de `Veiculo`; `ProdutoComum` e
  `ProdutoComExpansao` herdam de `Produto`.
- **Polimorfismo:** `calcularCapacidadeLiquida()` e `calcularVolumeMaximo()` se comportam
  de modo diferente em cada subclasse.

## Como executar
1. Crie um banco PostgreSQL chamado `loadx_java`.
2. Copie `src/main/resources/application-local.yaml.example` para
   `src/main/resources/application-local.yaml` e coloque a senha do seu PostgreSQL.
3. Execute `mvnw.cmd spring-boot:run` (Windows) ou `./mvnw spring-boot:run`.
4. Abra http://localhost:8080 (as tabelas são criadas automaticamente pelo `schema.sql`).
