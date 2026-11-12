package br.com.fatec.farmacia.controller;

import br.com.fatec.farmacia.model.Fornecedor;
import br.com.fatec.farmacia.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Fornecedores",
        description = "Gerenciamento de Distribuidoras Farmacêuticas e Parceiros de Abastecimento"
)
@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @Operation(summary = "Cadastrar Fornecedor", description = "Cadastra uma nova distribuidora com validação de CNPJ.")
    @PostMapping
    public ResponseEntity<Fornecedor> cadastrar(@Valid @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.cadastrarFornecedor(fornecedor));
    }

    @Operation(summary = "Listar Fornecedores", description = "Lista todas as distribuidoras cadastradas.")
    @GetMapping
    public List<Fornecedor> listar() {
        return fornecedorService.listarFornecedores();
    }

    @Operation(summary = "Buscar Fornecedor por ID", description = "Busca distribuidora pelo ID.")
    @GetMapping("/id/{id}")
    public ResponseEntity<Fornecedor> pesquisarPorId(@PathVariable Long id) {
        Optional<Fornecedor> f = fornecedorService.pesquisarPorId(id);
        return f.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar Fornecedor", description = "Atualiza dados de uma distribuidora.")
    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> atualizar(@PathVariable Long id, @Valid @RequestBody Fornecedor fornecedor) {
        Fornecedor atualizado = fornecedorService.atualizarFornecedor(id, fornecedor);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Deletar Fornecedor", description = "Remove distribuidora do sistema.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (fornecedorService.deletarFornecedor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
