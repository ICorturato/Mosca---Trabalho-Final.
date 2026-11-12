package br.com.fatec.farmacia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EntradaEstoqueDTO {

    @NotNull(message = "ID do fornecedor é obrigatório")
    @Schema(description = "Identificador do fornecedor", example = "1")
    private Long idFornecedor;

    @NotNull(message = "ID do medicamento é obrigatório")
    @Schema(description = "Identificador do medicamento a abastecer", example = "1")
    private Long idMedicamento;

    @NotNull(message = "Quantidade de entrada é obrigatória")
    @Min(value = 1, message = "Quantidade mínima para abastecimento é 1")
    @Schema(description = "Quantidade de unidades recebidas da distribuidora", example = "100")
    private Integer quantidade;

    @NotNull(message = "Custo unitário de compra é obrigatório")
    @DecimalMin(value = "0.01", message = "Custo deve ser maior que zero")
    @Schema(description = "Preço de custo unitário pago pela farmácia", example = "8.50")
    private BigDecimal custoUnitario;
}
