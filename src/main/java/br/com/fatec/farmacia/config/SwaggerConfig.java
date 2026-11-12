package br.com.fatec.farmacia.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Gestão de Farmácia",
                version = "1.0",
                description = "API desenvolvida em Spring Boot para gerenciamento de clientes, medicamentos e vendas em uma farmácia. " +
                        "Atende aos requisitos da disciplina de Arquitetura Orientada a Serviços da Fatec Jales, " +
                        "incluindo controle de estoque, validação de receitas para remédios controlados e emissão de pedidos de venda.",
                contact = @Contact(
                        name = "Equipe de Desenvolvimento - Fatec Jales",
                        email = "devfatecjales@cps.sp.gov.br"
                )
        )
)
public class SwaggerConfig {
}
