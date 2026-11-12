package br.com.fatec.farmacia.controller;

import br.com.fatec.farmacia.model.Medicamento;
import br.com.fatec.farmacia.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Medicamentos",
        description = "Gerenciamento do Catálogo, Produtos Farmacêuticos e Sugestão de Genéricos"
)
@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Operation(
            summary = "Cadastrar Medicamento ou Produto",
            description = "Cadastra um novo produto no inventário com dados como princípio ativo, dosagem, código de barras, controle especial e laboratório."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Medicamento cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Medicamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados do produto inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Medicamento> cadastrarMedicamento(@Valid @RequestBody Medicamento medicamento) {
        Medicamento novo = medicamentoService.cadastrarMedicamento(medicamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @Operation(
            summary = "Listar Produtos / Medicamentos",
            description = "Retorna todos os medicamentos e produtos cadastrados no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public List<Medicamento> listarMedicamentos() {
        return medicamentoService.listarMedicamentos();
    }

    @Operation(
            summary = "Buscar Medicamento por ID",
            description = "Consulta detalhes do produto pelo identificador numérico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<Medicamento> pesquisarMedicamentoPorId(
            @Parameter(description = "ID do medicamento", example = "1", required = true)
            @PathVariable Long id) {
        Optional<Medicamento> medicamento = medicamentoService.pesquisarMedicamentoPorId(id);
        return medicamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Buscar Medicamento por Código de Barras (EAN)",
            description = "Permite a leitura direta via leitor de código de barras no ponto de venda (PDV)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Código de barras não localizado")
    })
    @GetMapping("/codigobarras/{codigoBarras}")
    public ResponseEntity<Medicamento> pesquisarPorCodigoBarras(
            @Parameter(description = "Código de barras EAN", example = "7891058001234", required = true)
            @PathVariable String codigoBarras) {
        Optional<Medicamento> med = medicamentoService.pesquisarMedicamentoPorCodigoBarras(codigoBarras);
        return med.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Sugerir Genéricos e Alternativas Mais Baratas",
            description = "Regra de Negócio: Identifica e lista alternativas (genéricos ou similares) com o mesmo princípio ativo e dosagem que possuam menor preço para o cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de alternativas sugeridas com menor valor")
    })
    @GetMapping("/alternativas/{id}")
    public List<Medicamento> sugerirAlternativasMaisBaratas(
            @Parameter(description = "ID do medicamento de referência consultado", example = "1", required = true)
            @PathVariable Long id) {
        return medicamentoService.sugerirAlternativasMaisBaratas(id);
    }

    @Operation(
            summary = "Relatório de Alerta de Estoque Baixo (Ponto de Reposição)",
            description = "Regra de Negócio: Retorna medicamentos que atingiram o limite mínimo de segurança no estoque físico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de medicamentos com necessidade de reposição")
    })
    @GetMapping("/estoque-baixo")
    public List<Medicamento> listarMedicamentosComEstoqueBaixo() {
        return medicamentoService.listarMedicamentosComEstoqueBaixo();
    }

    @Operation(
            summary = "Buscar Medicamentos por Parte do Nome",
            description = "Localiza produtos por trecho do nome comercial."
    )
    @GetMapping("/partenome/{nome}")
    public List<Medicamento> pesquisarMedicamentoPorParteDoNome(@PathVariable String nome) {
        return medicamentoService.pesquisarMedicamentoPorParteDoNome(nome);
    }

    @Operation(
            summary = "Listar por Tipo de Controle",
            description = "Filtra medicamentos sujeitos a controle especial (true) ou venda livre (false)."
    )
    @GetMapping("/controlados/{controlado}")
    public List<Medicamento> pesquisarMedicamentoPorControlado(@PathVariable Boolean controlado) {
        return medicamentoService.pesquisarMedicamentoPorControlado(controlado);
    }

    @Operation(
            summary = "Listar por Tipo de Produto",
            description = "Filtra catálogo por tipo (MEDICAMENTO, COSMETICO, SUPLEMENTO, HIGIENE)."
    )
    @GetMapping("/tipo/{tipo}")
    public List<Medicamento> pesquisarMedicamentoPorTipo(@PathVariable String tipo) {
        return medicamentoService.pesquisarMedicamentoPorTipoProduto(tipo);
    }

    @Operation(
            summary = "Atualizar Medicamento",
            description = "Atualiza cadastro e propriedades de um medicamento."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> atualizarMedicamento(
            @PathVariable Long id,
            @Valid @RequestBody Medicamento medicamento) {
        Medicamento atualizado = medicamentoService.atualizarMedicamento(id, medicamento);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Deletar Medicamento",
            description = "Exclui um medicamento do catálogo."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedicamento(@PathVariable Long id) {
        boolean deletado = medicamentoService.deletarMedicamento(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
