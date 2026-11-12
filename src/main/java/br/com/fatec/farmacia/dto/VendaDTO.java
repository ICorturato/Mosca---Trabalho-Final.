package br.com.fatec.farmacia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VendaDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "Identificador do cliente cadastrado", example = "1")
    private Long idCliente;

    @NotEmpty(message = "A venda deve conter pelo menos um item")
    @Valid
    @Schema(description = "Lista de produtos/itens adicionados ao carrinho")
    private List<ItemVendaDTO> itens;

    @NotBlank(message = "Forma de pagamento é obrigatória (DINHEIRO, PIX, CARTAO_DEBITO, CARTAO_CREDITO)")
    @Schema(description = "Forma de pagamento escolhida", example = "PIX")
    private String formaPagamento;

    @Schema(description = "Desconto manual ou promocional opcional (BRL)", example = "3.00")
    private BigDecimal valorDesconto;

    @Schema(description = "Acréscimo ou taxa de maquininha opcional (BRL)", example = "0.00")
    private BigDecimal valorAcrescimoTaxa;

    @Schema(description = "Código da Receita Digital (obrigatório se algum item da venda for controlado)", example = "REC-2026-998811-SP")
    private String codigoReceitaDigital;
}
