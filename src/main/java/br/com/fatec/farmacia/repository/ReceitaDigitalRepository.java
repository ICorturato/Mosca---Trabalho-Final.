package br.com.fatec.farmacia.repository;

import br.com.fatec.farmacia.model.ReceitaDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceitaDigitalRepository extends JpaRepository<ReceitaDigital, Long> {
    Optional<ReceitaDigital> findByCodigoReceita(String codigoReceita);
    List<ReceitaDigital> findByCpfPaciente(String cpfPaciente);
    List<ReceitaDigital> findByStatusReceita(String statusReceita);
}
