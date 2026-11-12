package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movimentacao_financeira")
public class MovimentacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador da movimentação financeira", example = "1")
    private Long idMovimentacao;

    @Column(name = "tipo_movimentacao", nullable = false)
    @NotBlank(message = "Tipo de movimentação é obrigatório (ENTRADA ou SAIDA)")
    @Schema(description = "Tipo: ENTRADA (venda realizada) ou SAIDA (estorno de venda, compra de fornecedor)", example = "ENTRADA")
    private String tipo;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Schema(description = "Valor financeiro movimentado", example = "55.00")
    private BigDecimal valor;

    @Column(name = "descricao", nullable = false)
    @NotBlank(message = "Descrição é obrigatória")
    @Schema(description = "Descrição da operação financeira", example = "Recebimento de Venda #1 via PIX")
    private String descricao;

    @Column(name = "id_referencia")
    @Schema(description = "ID da Venda ou Pedido associado a esta movimentação", example = "1")
    private Long idReferencia;

    @Column(name = "data_hora", nullable = false)
    @Schema(description = "Momento exato da transação financeira")
    private LocalDateTime dataHora;
}
