package br.com.fatec.farmacia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemVendaDTO {

    @NotNull(message = "ID do medicamento é obrigatório")
    @Schema(description = "Identificador único do medicamento", example = "1")
    private Long idMedicamento;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima por item é 1")
    @Schema(description = "Quantidade de unidades a comprar", example = "2")
    private Integer quantidade;
}
