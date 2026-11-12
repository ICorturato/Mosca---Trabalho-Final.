package br.com.fatec.farmacia.service;

import br.com.fatec.farmacia.model.Medicamento;
import br.com.fatec.farmacia.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public Medicamento cadastrarMedicamento(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    public Optional<Medicamento> pesquisarMedicamentoPorId(Long id) {
        return medicamentoRepository.findById(id);
    }

    public Optional<Medicamento> pesquisarMedicamentoPorCodigoBarras(String codigoBarras) {
        return medicamentoRepository.findByCodigoBarras(codigoBarras);
    }

    public List<Medicamento> pesquisarMedicamentoPorParteDoNome(String nome) {
        return medicamentoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Medicamento> pesquisarMedicamentoPorControlado(Boolean controlado) {
        return medicamentoRepository.findByControlado(controlado);
    }

    public List<Medicamento> pesquisarMedicamentoPorTipoProduto(String tipo) {
        return medicamentoRepository.findByTipoProduto(tipo);
    }

    /**
     * Regra de Negócio: Sugere alternativas mais baratas (como genéricos e similares)
     * com o mesmo princípio ativo e dosagem.
     */
    public List<Medicamento> sugerirAlternativasMaisBaratas(Long idReferencia) {
        Optional<Medicamento> opt = medicamentoRepository.findById(idReferencia);
        if (opt.isEmpty()) {
            return Collections.emptyList();
        }
        Medicamento base = opt.get();
        if (base.getPrincipioAtivo() == null || base.getDosagem() == null) {
            return Collections.emptyList();
        }

        List<Medicamento> equivalentes = medicamentoRepository.findByPrincipioAtivoIgnoreCaseAndDosagemIgnoreCase(
                base.getPrincipioAtivo(), base.getDosagem()
        );

        return equivalentes.stream()
                .filter(m -> !m.getIdMedicamento().equals(base.getIdMedicamento()))
                .filter(m -> m.getPreco().compareTo(base.getPreco()) < 0)
                .sorted(Comparator.comparing(Medicamento::getPreco))
                .collect(Collectors.toList());
    }

    /**
     * Relatório de Ponto de Reposição: Retorna medicamentos com estoque abaixo ou igual ao mínimo.
     */
    public List<Medicamento> listarMedicamentosComEstoqueBaixo() {
        return medicamentoRepository.findAll().stream()
                .filter(m -> m.getEstoqueMinimo() != null && m.getQuantidadeEstoque() <= m.getEstoqueMinimo())
                .collect(Collectors.toList());
    }

    public Medicamento atualizarMedicamento(Long id, Medicamento medicamentoAtualizado) {
        Optional<Medicamento> medicamentoExistente = medicamentoRepository.findById(id);

        if (medicamentoExistente.isPresent()) {
            Medicamento medicamento = medicamentoExistente.get();
            medicamento.setNome(medicamentoAtualizado.getNome());
            medicamento.setPrincipioAtivo(medicamentoAtualizado.getPrincipioAtivo());
            medicamento.setDosagem(medicamentoAtualizado.getDosagem());
            medicamento.setFormaFarmaceutica(medicamentoAtualizado.getFormaFarmaceutica());
            medicamento.setTipoProduto(medicamentoAtualizado.getTipoProduto());
            medicamento.setClasseTerapeutica(medicamentoAtualizado.getClasseTerapeutica());
            medicamento.setIsGenerico(medicamentoAtualizado.getIsGenerico());
            medicamento.setCodigoBarras(medicamentoAtualizado.getCodigoBarras());
            medicamento.setLaboratorio(medicamentoAtualizado.getLaboratorio());
            medicamento.setPreco(medicamentoAtualizado.getPreco());
            medicamento.setDescontoLaboratorio(medicamentoAtualizado.getDescontoLaboratorio());
            medicamento.setQuantidadeEstoque(medicamentoAtualizado.getQuantidadeEstoque());
            medicamento.setEstoqueMinimo(medicamentoAtualizado.getEstoqueMinimo());
            medicamento.setControlado(medicamentoAtualizado.getControlado());
            return medicamentoRepository.save(medicamento);
        }
        return null;
    }

    public boolean deletarMedicamento(Long id) {
        if (medicamentoRepository.existsById(id)) {
            medicamentoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
