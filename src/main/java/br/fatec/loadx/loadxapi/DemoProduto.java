package br.fatec.loadx.loadxapi;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.ProdutoComExpansao;
import br.fatec.loadx.loadxapi.dominio.ProdutoComum;
import br.fatec.loadx.loadxapi.persistencia.ProdutoMapeador;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRegistro;
import br.fatec.loadx.loadxapi.persistencia.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// ============================================================
// DemoProduto.java  (NOVO — TEMPORÁRIO)
// ------------------------------------------------------------
// Igual ao DemoVeiculo: serve só para você VER a gravação e a
// leitura de produtos funcionando. Sai quando as telas existirem.
//
// Aproveitamos para exibir o polimorfismo vindo do banco: cada
// produto lido calcula o volume máximo do seu jeito.
// ============================================================
@Component
public class DemoProduto implements CommandLineRunner {

    private final ProdutoRepository repositorio;

    public DemoProduto(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {

        // Grava os exemplos só se a tabela estiver vazia.
        if (repositorio.count() == 0) {
            Produto alfa = new ProdutoComum("Produto Alfa", 0.80, 10);
            Produto beta = new ProdutoComExpansao("Produto Beta", 1.30, 5, 10);

            repositorio.save(ProdutoMapeador.paraRegistro(alfa));
            repositorio.save(ProdutoMapeador.paraRegistro(beta));
            System.out.println(">>> Produtos de exemplo gravados no banco.");
        }

        // Lê tudo do banco, converte para o domínio e usa o polimorfismo.
        double tanque = 15000;
        System.out.println(">>> Produtos no banco (tanque de " + tanque + " L):");
        for (ProdutoRegistro registro : repositorio.findAll()) {
            Produto p = ProdutoMapeador.paraDominio(registro);
            System.out.println("    #" + p.getId() + " " + p);
            System.out.println("       Volume máximo: " + p.calcularVolumeMaximo(tanque) + " L");
        }

        // Exemplo da consulta derivada findByNome (SQL gerado pelo Spring).
        repositorio.findByNome("PRODUTO ALFA").ifPresent(r ->
                System.out.println(">>> findByNome encontrou: #" + r.getId() + " " + r.getNome()));
    }
}