package br.com.fatec.farmacia.repository;

import br.com.fatec.farmacia.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    List<Fornecedor> findByRazaoSocialContainingIgnoreCase(String razaoSocial);
}
