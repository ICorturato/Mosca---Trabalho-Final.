package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "receita_digital")
@Valid
public class ReceitaDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da receita", example = "1")
    private Long idReceita;

    @Column(name = "codigo_receita", nullable = false, unique = true)
    @NotBlank(message = "Código da receita é obrigatório")
    @Schema(description = "Código de identificação ou hash ICP-Brasil/CFM da receita", example = "REC-2026-998811-SP")
    private String codigoReceita;

    @Column(name = "cpf_paciente", nullable = false)
    @NotBlank(message = "CPF do paciente é obrigatório")
    @Schema(description = "CPF do paciente beneficiário da receita", example = "123.456.789-00")
    private String cpfPaciente;

    @Column(name = "nome_paciente", nullable = false)
    @NotBlank(message = "Nome do paciente é obrigatório")
    @Schema(description = "Nome do paciente", example = "Carlos Eduardo Ferreira")
    private String nomePaciente;

    @Column(name = "crm_medico", nullable = false)
    @NotBlank(message = "CRM do médico é obrigatório")
    @Schema(description = "CRM do médico prescritor", example = "123456")
    private String crmMedico;

    @Column(name = "uf_crm", nullable = false)
    @NotBlank(message = "UF do CRM é obrigatória")
    @Schema(description = "Estado do CRM médico", example = "SP")
    private String ufCrm;

    @Column(name = "nome_medico", nullable = false)
    @NotBlank(message = "Nome do médico é obrigatório")
    @Schema(description = "Nome do médico prescritor", example = "Dr. Roberto Albuquerque")
    private String nomeMedico;

    @Column(name = "medicamento_prescrito", nullable = false)
    @NotBlank(message = "Medicamento prescrito é obrigatório")
    @Schema(description = "Nome e dosagem do medicamento prescrito", example = "Clonazepam 2mg")
    private String medicamentoPrescrito;

    @Column(name = "quantidade_prescrita", nullable = false)
    @NotNull(message = "Quantidade prescrita é obrigatória")
    @Schema(description = "Quantidade máxima autorizada na receita", example = "2")
    private Integer quantidadePrescrita;

    @Column(name = "data_emissao", nullable = false)
    @NotNull(message = "Data de emissão é obrigatória")
    @Schema(description = "Data em que o médico emitiu a receita", example = "2026-11-01")
    private LocalDate dataEmissao;

    @Column(name = "data_validade", nullable = false)
    @NotNull(message = "Data de validade da receita é obrigatória")
    @Schema(description = "Validade máxima da receita perante a portaria da Anvisa (ex: 30 dias)", example = "2026-12-01")
    private LocalDate dataValidade;

    @Column(name = "status_receita", nullable = false)
    @Schema(description = "Status da receita no ecossistema: ATIVA, DISPENSADA ou CANCELADA", example = "ATIVA")
    private String statusReceita;

    @Column(name = "data_dispensacao")
    @Schema(description = "Momento exato da baixa/dispensação no balcão da farmácia")
    private LocalDateTime dataDispensacao;
}
