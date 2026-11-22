-- ============================================================
-- schema.sql
-- ------------------------------------------------------------
-- Cria as três tabelas do projeto no PostgreSQL.
-- Executado pelo Spring a cada inicialização (spring.sql.init.mode=always).
-- "IF NOT EXISTS" evita erro quando a tabela já existe.
-- Todos os dados do projeto são FICTÍCIOS.
-- ============================================================

-- ------------------------------------------------------------
-- VEICULO
-- O Spring Data JDBC não mapeia herança. Por isso Caminhao e
-- Carreta compartilham UMA tabela, distinguidas pela coluna "tipo".
-- As colunas específicas de cada tipo ficam vazias (NULL) no outro.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS veiculo (
    id                BIGSERIAL        PRIMARY KEY,
    tipo              VARCHAR(20)      NOT NULL CHECK (tipo IN ('CAMINHAO', 'CARRETA')),
    descricao         VARCHAR(100)     NOT NULL,
    pbtc              DOUBLE PRECISION NOT NULL CHECK (pbtc > 0),
    numero_eixos      INTEGER          NOT NULL CHECK (numero_eixos >= 2),
    tara              DOUBLE PRECISION NOT NULL CHECK (tara >= 0),
    capacidade_litros DOUBLE PRECISION CHECK (capacidade_litros >= 0),   -- só CAMINHAO (0 = não informada)
    tara_semirreboque DOUBLE PRECISION CHECK (tara_semirreboque >= 0)    -- só CARRETA
);

-- ------------------------------------------------------------
-- PRODUTO
-- Mesma ideia: ProdutoComum e ProdutoComExpansao numa tabela só.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS produto (
    id                         BIGSERIAL        PRIMARY KEY,
    tipo                       VARCHAR(20)      NOT NULL CHECK (tipo IN ('COMUM', 'COM_EXPANSAO')),
    nome                       VARCHAR(100)     NOT NULL UNIQUE,
    densidade                  DOUBLE PRECISION NOT NULL CHECK (densidade > 0),
    tolerancia_percentual      DOUBLE PRECISION NOT NULL CHECK (tolerancia_percentual BETWEEN 0 AND 100),
    margem_expansao_percentual DOUBLE PRECISION CHECK (margem_expansao_percentual >= 0
                                                       AND margem_expansao_percentual < 100)  -- só COM_EXPANSAO
);

-- ------------------------------------------------------------
-- PROGRAMACAO
-- Liga um veículo e um produto (chaves estrangeiras) e guarda o
-- status como texto, limitado aos valores do enum StatusProgramacao.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS programacao (
    id                       BIGSERIAL        PRIMARY KEY,
    ordem_transporte         VARCHAR(30)      NOT NULL UNIQUE,
    veiculo_id               BIGINT           NOT NULL REFERENCES veiculo (id),
    produto_id               BIGINT           NOT NULL REFERENCES produto (id),
    capacidade_tanque_litros DOUBLE PRECISION NOT NULL CHECK (capacidade_tanque_litros > 0),
    data_hora_prevista       TIMESTAMP        NOT NULL,
    status                   VARCHAR(20)      NOT NULL
        CHECK (status IN ('AGENDADA', 'EM_CARREGAMENTO', 'CONCLUIDA', 'CANCELADA'))
);

-- ------------------------------------------------------------
-- AJUSTE (VERSÃO 2): a capacidade do caminhão passou a aceitar ZERO.
-- Para bancos criados antes desta mudança, troca a regra antiga
-- (> 0) por (>= 0). É seguro rodar a cada inicialização:
-- DROP IF EXISTS + ADD recria a regra sempre do mesmo jeito.
-- ------------------------------------------------------------
ALTER TABLE veiculo DROP CONSTRAINT IF EXISTS veiculo_capacidade_litros_check;
ALTER TABLE veiculo ADD CONSTRAINT veiculo_capacidade_litros_check CHECK (capacidade_litros >= 0);
