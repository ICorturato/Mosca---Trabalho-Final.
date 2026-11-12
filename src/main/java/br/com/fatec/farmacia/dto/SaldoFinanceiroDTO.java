package br.com.fatec.farmacia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoFinanceiroDTO {

    @Schema(description = "Total de entradas financeiras registradas", example = "1500.00")
    private BigDecimal totalEntradas;

    @Schema(description = "Total de saídas financeiras registradas", example = "600.00")
    private BigDecimal totalSaidas;

    @Schema(description = "Saldo líquido em caixa (entradas - saídas)", example = "900.00")
    private BigDecimal saldoLiquido;
}
