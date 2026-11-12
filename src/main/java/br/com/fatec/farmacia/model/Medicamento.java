package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "medicamento")
@Valid
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do produto", example = "1")
    private Long idMedicamento;

    @Column(name = "nome_medicamento", nullable = false)
    @NotBlank(message = "Nome do medicamento/produto é obrigatório")
    @Schema(description = "Nome comercial", example = "Novalgina 500mg Comprimidos")
    private String nome;

    @Column(name = "principio_ativo")
    @Schema(description = "Princípio ativo do medicamento (usado para sugerir genéricos/similares)", example = "Dipirona Monoidratada")
    private String principioAtivo;

    @Column(name = "dosagem")
    @Schema(description = "Dosagem ou concentração do produto", example = "500mg")
    private String dosagem;

    @Column(name = "forma_farmaceutica")
    @Schema(description = "Forma farmacêutica (ex: Comprimido, Xarope, Gotas, Pomada, Cápsula)", example = "Comprimido")
    private String formaFarmaceutica;

    @Column(name = "tipo_produto", nullable = false)
    @NotBlank(message = "Tipo de produto é obrigatório")
    @Schema(description = "Tipo do produto no catálogo (MEDICAMENTO, COSMETICO, SUPLEMENTO, HIGIENE)", example = "MEDICAMENTO")
    private String tipoProduto;

    @Column(name = "classe_terapeutica")
    @Schema(description = "Classe terapêutica (ex: ANALGESICO, ANTI_INFLAMATORIO, ANSIOLITICO, ANTIBIOTICO)", example = "ANALGESICO")
    private String classeTerapeutica;

    @Column(name = "is_generico", nullable = false)
    @NotNull(message = "Informe se o medicamento é genérico (true/false)")
    @Schema(description = "Indica se o produto é classificado como genérico", example = "false")
    private Boolean isGenerico;

    @Column(name = "codigo_barras", unique = true)
    @Schema(description = "Código de barras padrão EAN-13 para leitor ótico", example = "7891058001234")
    private String codigoBarras;

    @Column(name = "laboratorio", nullable = false)
    @NotBlank(message = "Laboratório fabricante é obrigatório")
    @Schema(description = "Laboratório produtor", example = "Sanofi Medley")
    private String laboratorio;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Schema(description = "Preço de venda praticado (BRL)", example = "28.90")
    private BigDecimal preco;

    @Column(name = "desconto_laboratorio", precision = 10, scale = 2)
    @Schema(description = "Valor ou percentual de desconto de laboratório/PBM (opcional)", example = "5.00")
    private BigDecimal descontoLaboratorio;

    @Column(name = "quantidade_estoque", nullable = false)
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    @Schema(description = "Quantidade de unidades disponíveis no estoque físico", example = "50")
    private Integer quantidadeEstoque;

    @Column(name = "estoque_minimo")
    @Schema(description = "Estoque mínimo para alerta/trava de compras da farmácia (opcional)", example = "10")
    private Integer estoqueMinimo;

    @Column(name = "controlado", nullable = false)
    @NotNull(message = "Indicação de controle é obrigatória")
    @Schema(description = "Indica se o medicamento exige receita médica controlada (true/false)", example = "false")
    private Boolean controlado;
}
