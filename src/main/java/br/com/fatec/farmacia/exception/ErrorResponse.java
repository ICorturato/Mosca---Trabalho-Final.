package br.com.fatec.farmacia.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Momento em que o erro ocorreu", example = "2026-11-12T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "400")
    private int status;

    @Schema(description = "Mensagem ou título do erro", example = "Regra de negócio violada")
    private String mensagem;

    @Schema(description = "Detalhes específicos ou lista de campos com falha de validação")
    private List<String> detalhes;
}
