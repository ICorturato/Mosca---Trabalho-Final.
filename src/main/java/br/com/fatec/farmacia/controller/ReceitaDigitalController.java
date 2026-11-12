package br.com.fatec.farmacia.controller;

import br.com.fatec.farmacia.model.ReceitaDigital;
import br.com.fatec.farmacia.service.ReceitaDigitalService;
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
        name = "Receitas Digitais",
        description = "Controle de Prescrições Eletrônicas, Validação de Autenticidade e Prevenção de Fraudes/Reuso"
)
@RestController
@RequestMapping("/receitas")
public class ReceitaDigitalController {

    @Autowired
    private ReceitaDigitalService receitaService;

    @Operation(summary = "Registrar Receita Digital", description = "Recebe e registra a prescrição assinada digitalmente com dados do médico e paciente.")
    @PostMapping
    public ResponseEntity<ReceitaDigital> cadastrar(@Valid @RequestBody ReceitaDigital receita) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receitaService.cadastrarReceita(receita));
    }

    @Operation(summary = "Listar Receitas", description = "Lista todas as receitas cadastradas na base.")
    @GetMapping
    public List<ReceitaDigital> listar() {
        return receitaService.listarReceitas();
    }

    @Operation(summary = "Consultar Receita por Código", description = "Verifica validade, autenticidade e status da receita digital.")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ReceitaDigital> pesquisarPorCodigo(@PathVariable String codigo) {
        Optional<ReceitaDigital> rec = receitaService.pesquisarPorCodigo(codigo);
        return rec.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar Receitas por CPF do Paciente", description = "Histórico de prescrições de um paciente.")
    @GetMapping("/paciente/{cpf}")
    public List<ReceitaDigital> listarPorPaciente(@PathVariable String cpf) {
        return receitaService.listarPorPaciente(cpf);
    }
}
