package br.com.fatec.farmacia.repository;

import br.com.fatec.farmacia.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    List<Medicamento> findByNomeContainingIgnoreCase(String nome);
    List<Medicamento> findByLaboratorioContainingIgnoreCase(String laboratorio);
    List<Medicamento> findByControlado(Boolean controlado);
    List<Medicamento> findByTipoProduto(String tipoProduto);
    Optional<Medicamento> findByCodigoBarras(String codigoBarras);
    List<Medicamento> findByPrincipioAtivoIgnoreCaseAndDosagemIgnoreCase(String principioAtivo, String dosagem);
}
