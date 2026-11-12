package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "item_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do item na venda", example = "1")
    private Long idItemVenda;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_medicamento", nullable = false)
    @NotNull(message = "Medicamento é obrigatório")
    private Medicamento medicamento;

    @Column(name = "quantidade", nullable = false)
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Schema(description = "Quantidade de unidades compradas deste item", example = "2")
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Preço unitário praticado na venda", example = "14.50")
    private BigDecimal precoUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Subtotal (quantidade * precoUnitario)", example = "29.00")
    private BigDecimal subtotal;
}
