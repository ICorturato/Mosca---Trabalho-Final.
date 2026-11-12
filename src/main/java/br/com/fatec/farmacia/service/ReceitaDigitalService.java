package br.com.fatec.farmacia.service;

import br.com.fatec.farmacia.model.ReceitaDigital;
import br.com.fatec.farmacia.repository.ReceitaDigitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReceitaDigitalService {

    @Autowired
    private ReceitaDigitalRepository receitaDigitalRepository;

    public ReceitaDigital cadastrarReceita(ReceitaDigital receita) {
        if (receita.getStatusReceita() == null || receita.getStatusReceita().isBlank()) {
            receita.setStatusReceita("ATIVA");
        }
        return receitaDigitalRepository.save(receita);
    }

    public List<ReceitaDigital> listarReceitas() {
        return receitaDigitalRepository.findAll();
    }

    public Optional<ReceitaDigital> pesquisarPorId(Long id) {
        return receitaDigitalRepository.findById(id);
    }

    public Optional<ReceitaDigital> pesquisarPorCodigo(String codigoReceita) {
        return receitaDigitalRepository.findByCodigoReceita(codigoReceita);
    }

    public List<ReceitaDigital> listarPorPaciente(String cpfPaciente) {
        return receitaDigitalRepository.findByCpfPaciente(cpfPaciente);
    }

    /**
     * Valida e dá baixa (dispensação) na receita digital, impedindo fraudes ou compras duplicadas.
     */
    public ReceitaDigital dispensarReceita(String codigoReceita) {
        ReceitaDigital receita = receitaDigitalRepository.findByCodigoReceita(codigoReceita)
                .orElseThrow(() -> new IllegalArgumentException("Receita digital com código '" + codigoReceita + "' não encontrada."));

        if ("DISPENSADA".equalsIgnoreCase(receita.getStatusReceita())) {
            throw new IllegalStateException("FRAUDE / REUSO: A receita digital '" + codigoReceita + 
                    "' já foi utilizada e dispensada anteriormente em " + receita.getDataDispensacao() + ".");
        }

        if (receita.getDataValidade().isBefore(LocalDate.now())) {
            throw new IllegalStateException("RECEITA EXPIRADA: A receita digital '" + codigoReceita + 
                    "' venceu em " + receita.getDataValidade() + " e não pode ser dispensada.");
        }

        receita.setStatusReceita("DISPENSADA");
        receita.setDataDispensacao(LocalDateTime.now());
        return receitaDigitalRepository.save(receita);
    }
}
