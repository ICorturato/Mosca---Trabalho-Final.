package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "venda")
@Valid
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da venda", example = "1")
    private Long idVenda;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "Cliente que realizou a compra")
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_venda")
    @Schema(description = "Lista dos itens comercializados nesta venda")
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(name = "subtotal_itens", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Soma dos subtotais dos itens", example = "58.00")
    private BigDecimal subtotalItens;

    @Column(name = "valor_desconto", precision = 10, scale = 2)
    @Schema(description = "Desconto concedido (promoção, convênio ou pagamento à vista)", example = "5.00")
    private BigDecimal valorDesconto;

    @Column(name = "valor_acrescimo_taxa", precision = 10, scale = 2)
    @Schema(description = "Acréscimo de taxa (ex: parcelamento em cartão)", example = "2.00")
    private BigDecimal valorAcrescimoTaxa;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Valor total final calculado (subtotal - desconto + acrescimo)", example = "55.00")
    private BigDecimal valorTotal;

    @Column(name = "forma_pagamento", nullable = false)
    @Schema(description = "Forma de pagamento (DINHEIRO, PIX, CARTAO_DEBITO, CARTAO_CREDITO)", example = "PIX")
    private String formaPagamento;

    @ManyToOne
    @JoinColumn(name = "id_receita")
    @Schema(description = "Receita digital vinculada (obrigatória caso haja medicamentos controlados)")
    private ReceitaDigital receitaDigital;

    @Column(name = "status_venda", nullable = false)
    @Schema(description = "Status da venda: CONCLUIDA ou CANCELADA", example = "CONCLUIDA")
    private String statusVenda;

    @Column(name = "data_hora", nullable = false)
    @Schema(description = "Data e hora do registro da venda", example = "2026-11-12T10:30:00")
    private LocalDateTime dataHora;
}
