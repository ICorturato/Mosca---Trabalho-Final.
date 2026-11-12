package br.com.fatec.farmacia.repository;

import br.com.fatec.farmacia.model.Cliente;
import br.com.fatec.farmacia.model.Medicamento;
import br.com.fatec.farmacia.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByCliente(Cliente cliente);
    List<Venda> findByMedicamento(Medicamento medicamento);
}
