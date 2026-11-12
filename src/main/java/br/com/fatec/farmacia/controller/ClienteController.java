package br.com.fatec.farmacia.controller;

import br.com.fatec.farmacia.model.Cliente;
import br.com.fatec.farmacia.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Clientes",
        description = "Gerenciamento de Clientes da Farmácia"
)
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(
            summary = "Cadastrar Cliente",
            description = "Permite cadastrar um novo cliente no sistema com validação de CPF e E-mail."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Cliente.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para cadastro", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@Valid @RequestBody Cliente cliente) {
        Cliente novoCliente = clienteService.cadastrarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @Operation(
            summary = "Listar Clientes",
            description = "Retorna a lista completa de clientes cadastrados na farmácia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    @Operation(
            summary = "Buscar Cliente por ID",
            description = "Consulta os dados de um cliente a partir de seu identificador numérico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<Cliente> pesquisarClientePorId(
            @Parameter(description = "ID do cliente a ser consultado", example = "1", required = true)
            @PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.pesquisarClientePorId(id);
        return cliente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Buscar Cliente por CPF",
            description = "Busca um cliente específico utilizando o CPF cadastrado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o CPF informado")
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> pesquisarClientePorCpf(
            @Parameter(description = "CPF do cliente", example = "123.456.789-00", required = true)
            @PathVariable String cpf) {
        Optional<Cliente> cliente = clienteService.pesquisarClientePorCpf(cpf);
        return cliente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Buscar Clientes por Parte do Nome",
            description = "Filtra clientes que contenham o termo pesquisado em seu nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes filtrados com sucesso")
    })
    @GetMapping("/partenome/{nome}")
    public List<Cliente> pesquisarClientePorParteDoNome(
            @Parameter(description = "Trecho do nome do cliente", example = "Carlos", required = true)
            @PathVariable String nome) {
        return clienteService.pesquisarClientePorParteDoNome(nome);
    }

    @Operation(
            summary = "Atualizar Cliente",
            description = "Atualiza todas as informações cadastrais de um cliente existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não localizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(
            @Parameter(description = "ID do cliente", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {
        Cliente atualizado = clienteService.atualizarCliente(id, cliente);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Deletar Cliente",
            description = "Remove o registro de um cliente do banco de dados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para exclusão")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(
            @Parameter(description = "ID do cliente", example = "1", required = true)
            @PathVariable Long id) {
        boolean deletado = clienteService.deletarCliente(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
