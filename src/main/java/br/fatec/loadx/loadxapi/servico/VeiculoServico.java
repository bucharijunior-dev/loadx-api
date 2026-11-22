package br.fatec.loadx.loadxapi.servico;

import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.persistencia.VeiculoMapeador;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRegistro;
import br.fatec.loadx.loadxapi.persistencia.VeiculoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

// ============================================================
// VeiculoServico.java  (NOVO)
// ------------------------------------------------------------
// Camada de SERVIÇO do veículo: é por aqui que as telas (controllers)
// leem e gravam veículos. Esconde do resto do sistema o repositório,
// o registro e o mapeador.
//
// - @Service: o Spring cria um objeto desta classe e o entrega a
//   quem precisar (injeção de dependência pelo construtor).
// - As regras de validação ficam no DOMÍNIO (setters de Veiculo);
//   o serviço só orquestra.
// ============================================================
@Service
public class VeiculoServico {

    private final VeiculoRepository repositorio;

    public VeiculoServico(VeiculoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // LISTAR: lê os registros e converte cada um para o domínio.
    public List<Veiculo> listar() {
        List<Veiculo> resultado = new ArrayList<>();
        for (VeiculoRegistro registro : repositorio.findAll()) {
            resultado.add(VeiculoMapeador.paraDominio(registro));
        }
        return resultado;
    }

    // BUSCAR por id (erro claro se não existir).
    public Veiculo buscar(Long id) {
        return repositorio.findById(id)
                .map(VeiculoMapeador::paraDominio)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
    }

    // SALVAR: se o veículo não tem id, o Spring faz INSERT; se tem, UPDATE.
    public Veiculo salvar(Veiculo veiculo) {
        VeiculoRegistro salvo = repositorio.save(VeiculoMapeador.paraRegistro(veiculo));
        veiculo.setId(salvo.getId());
        return veiculo;
    }

    // EXCLUIR por id.
    public void excluir(Long id) {
        repositorio.deleteById(id);
    }
}
