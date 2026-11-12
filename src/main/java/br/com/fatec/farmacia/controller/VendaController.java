package br.com.fatec.farmacia.controller;

import br.com.fatec.farmacia.dto.EntradaEstoqueDTO;
import br.com.fatec.farmacia.dto.SaldoFinanceiroDTO;
import br.com.fatec.farmacia.dto.VendaDTO;
import br.com.fatec.farmacia.model.Medicamento;
import br.com.fatec.farmacia.model.MovimentacaoFinanceira;
import br.com.fatec.farmacia.model.Venda;
import br.com.fatec.farmacia.service.VendaService;
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
        name = "Vendas e Financeiro",
        description = "Processamento de Vendas, Estornos, Abastecimento de Fornecedores e Fluxo de Caixa"
)
@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @Operation(
            summary = "Realizar Venda (Carrinho com Múltiplos Itens)",
            description = "Processa a venda com múltiplos itens. Executa regras de negócio: validação de estoque de cada item, " +
                    "baixa e bloqueio de reuso de receita digital para remédios controlados, cálculo de total com desconto por forma de pagamento (ex: 5% no PIX) " +
                    "e lança automaticamente a entrada no fluxo financeiro da farmácia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venda realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = Venda.class))),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada (estoque insuficiente ou receita ausente/inválida)"),
            @ApiResponse(responseCode = "409", description = "Conflito: Receita digital já foi dispensada anteriormente")
    })
    @PostMapping
    public ResponseEntity<Venda> realizarVenda(@Valid @RequestBody VendaDTO dto) {
        Venda novaVenda = vendaService.realizarVenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaVenda);
    }

    @Operation(
            summary = "Cancelar Venda com Estorno de Estoque",
            description = "Regra de Negócio: Altera o status da venda para CANCELADA, devolve as quantidades vendidas de volta para o estoque e registra a saída correspondente no fluxo financeiro."
    )
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Venda> cancelarVenda(
            @Parameter(description = "ID da venda a ser cancelada", example = "1", required = true)
            @PathVariable Long id) {
        Venda cancelada = vendaService.cancelarVenda(id);
        return ResponseEntity.ok(cancelada);
    }

    @Operation(
            summary = "Registrar Entrada de Estoque (Compra de Fornecedor)",
            description = "Recebe mercadorias da distribuidora parceira, aumenta o saldo em estoque e lança saída no módulo financeiro."
    )
    @PostMapping("/abastecimento")
    public ResponseEntity<Medicamento> registrarEntradaEstoque(@Valid @RequestBody EntradaEstoqueDTO dto) {
        Medicamento med = vendaService.registrarEntradaEstoque(dto);
        return ResponseEntity.ok(med);
    }

    @Operation(
            summary = "Consultar Saldo do Fluxo de Caixa",
            description = "Regra de Negócio: Calcula automaticamente em tempo real o total de entradas, total de saídas e o saldo financeiro em caixa."
    )
    @GetMapping("/financeiro/saldo")
    public ResponseEntity<SaldoFinanceiroDTO> obterSaldo() {
        return ResponseEntity.ok(vendaService.obterSaldoFinanceiro());
    }

    @Operation(
            summary = "Listar Histórico de Movimentações Financeiras",
            description = "Extrato detalhado de todas as entradas por venda e saídas por estorno ou compra de estoque."
    )
    @GetMapping("/financeiro/extrato")
    public List<MovimentacaoFinanceira> listarMovimentacoes() {
        return vendaService.listarMovimentacoesFinanceiras();
    }

    @Operation(
            summary = "Listar Todas as Vendas",
            description = "Retorna o histórico geral de vendas."
    )
    @GetMapping
    public List<Venda> listarVendas() {
        return vendaService.listarVendas();
    }

    @Operation(
            summary = "Buscar Venda por ID",
            description = "Recupera detalhes de uma venda pelo ID."
    )
    @GetMapping("/id/{id}")
    public ResponseEntity<Venda> pesquisarVendaPorId(@PathVariable Long id) {
        Optional<Venda> venda = vendaService.pesquisarVendaPorId(id);
        return venda.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Listar Vendas por Cliente",
            description = "Retorna todas as compras já efetuadas pelo cliente."
    )
    @GetMapping("/cliente/{idCliente}")
    public List<Venda> listarVendasPorCliente(@PathVariable Long idCliente) {
        return vendaService.listarVendasPorCliente(idCliente);
    }
}
