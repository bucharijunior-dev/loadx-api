package br.fatec.loadx.loadxapi;

import br.fatec.loadx.loadxapi.dominio.Caminhao;
import br.fatec.loadx.loadxapi.dominio.Carreta;
import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.persistencia.VeiculoMapeador;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRegistro;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// ============================================================
// DemoVeiculo.java  (NOVO — TEMPORÁRIO)
// ------------------------------------------------------------
// Serve só para você VER a gravação e a leitura funcionando.
// Será removida quando as telas (Thymeleaf) assumirem esse papel.
//
// COMO FUNCIONA:
//   - @Component: o Spring cria um objeto desta classe sozinho ao
//     iniciar a aplicação.
//   - CommandLineRunner: o método run() é executado automaticamente
//     logo depois que a aplicação sobe.
//   - O VeiculoRepository chega pelo construtor: é a "injeção de
//     dependência". Em vez de criarmos o repositório com "new", o
//     Spring cria e entrega pronto. É uma das ideias centrais do
//     Spring.
// ============================================================
@Component
public class DemoVeiculo implements CommandLineRunner {

    private final VeiculoRepository repositorio;

    public DemoVeiculo(VeiculoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {

        // Grava os dois veículos de exemplo só se a tabela estiver
        // vazia, para não duplicar a cada vez que a aplicação sobe.
        if (repositorio.count() == 0) {
            Veiculo truck = new Caminhao("TRUCK 3 EIXOS", 23000, 3, 8000, 15000);
            Veiculo carreta = new Carreta("CARRETA 5 EIXOS", 45000, 5, 9500, 7000);

            repositorio.save(VeiculoMapeador.paraRegistro(truck));
            repositorio.save(VeiculoMapeador.paraRegistro(carreta));
            System.out.println(">>> Veículos de exemplo gravados no banco.");
        }

        // Lê tudo do banco e converte de volta para objetos de domínio.
        System.out.println(">>> Veículos no banco:");
        for (VeiculoRegistro registro : repositorio.findAll()) {
            Veiculo v = VeiculoMapeador.paraDominio(registro);
            System.out.println("    #" + v.getId() + " " + v);
            System.out.println("       Capacidade líquida: "
                    + v.calcularCapacidadeLiquida() + " kg");
        }
    }
}