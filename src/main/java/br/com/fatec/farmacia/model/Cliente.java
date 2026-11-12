package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "cliente")
@Valid
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do cliente", example = "1")
    private Long idCliente;

    @Column(name = "nome_cliente", nullable = false)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome completo do cliente", example = "Carlos Eduardo Ferreira")
    private String nome;

    @Column(name = "cpf_cliente", nullable = false, unique = true)
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF deve ser válido no formato xxx.xxx.xxx-xx ou apenas números")
    @Schema(description = "CPF válido do cliente", example = "123.456.789-00")
    private String cpf;

    @Column(name = "email_cliente", nullable = false, unique = true)
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido (exemplo: usuario@dominio.com)")
    @Schema(description = "Endereço de e-mail do cliente", example = "carlos.silva@email.com")
    private String email;

    @Column(name = "telefone_cliente", nullable = false)
    @NotBlank(message = "Telefone é obrigatório")
    @Schema(description = "Telefone de contato do cliente", example = "(17) 99876-5432")
    private String telefone;

    @Column(name = "data_nascimento")
    @Schema(description = "Data de nascimento do cliente", example = "1985-05-20")
    private LocalDate dataNascimento;

    @Column(name = "numero_convenio")
    @Schema(description = "Número da carteirinha de convênio ou PBM (opcional)", example = "CONV-998877")
    private String numeroConvenio;

    @Column(name = "observacoes_saude", length = 500)
    @Schema(description = "Observações de saúde ou alergias relatadas (opcional)", example = "Alérgico a Dipirona e Penicilina")
    private String observacoesSaude;
}
