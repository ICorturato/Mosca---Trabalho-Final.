package br.com.fatec.farmacia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

@Data
@Entity
@Table(name = "fornecedor")
@Valid
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do fornecedor", example = "1")
    private Long idFornecedor;

    @Column(name = "razao_social", nullable = false)
    @NotBlank(message = "Razão social é obrigatória")
    @Schema(description = "Razão social ou nome fantasia da distribuidora", example = "Distribuidora Farmacêutica Santa Cruz S/A")
    private String razaoSocial;

    @Column(name = "cnpj", nullable = false, unique = true)
    @NotBlank(message = "CNPJ é obrigatório")
    @CNPJ(message = "CNPJ deve ser válido")
    @Schema(description = "CNPJ da distribuidora", example = "12.345.678/0001-90")
    private String cnpj;

    @Column(name = "telefone", nullable = false)
    @NotBlank(message = "Telefone é obrigatório")
    @Schema(description = "Telefone comercial", example = "(11) 3322-1100")
    private String telefone;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Schema(description = "E-mail de pedidos ou contato", example = "pedidos@santacruz.com.br")
    private String email;

    @Column(name = "cidade")
    @Schema(description = "Cidade sede", example = "São Paulo")
    private String cidade;
}
