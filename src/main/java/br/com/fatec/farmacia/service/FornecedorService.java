package br.com.fatec.farmacia.service;

import br.com.fatec.farmacia.model.Fornecedor;
import br.com.fatec.farmacia.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Fornecedor cadastrarFornecedor(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorRepository.findAll();
    }

    public Optional<Fornecedor> pesquisarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    public Optional<Fornecedor> pesquisarPorCnpj(String cnpj) {
        return fornecedorRepository.findByCnpj(cnpj);
    }

    public List<Fornecedor> pesquisarPorRazaoSocial(String razao) {
        return fornecedorRepository.findByRazaoSocialContainingIgnoreCase(razao);
    }

    public Fornecedor atualizarFornecedor(Long id, Fornecedor atualizado) {
        Optional<Fornecedor> opt = fornecedorRepository.findById(id);
        if (opt.isPresent()) {
            Fornecedor f = opt.get();
            f.setRazaoSocial(atualizado.getRazaoSocial());
            f.setCnpj(atualizado.getCnpj());
            f.setTelefone(atualizado.getTelefone());
            f.setEmail(atualizado.getEmail());
            f.setCidade(atualizado.getCidade());
            return fornecedorRepository.save(f);
        }
        return null;
    }

    public boolean deletarFornecedor(Long id) {
        if (fornecedorRepository.existsById(id)) {
            fornecedorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
