# API de Gestão de Farmácia

Projeto Final desenvolvido para a disciplina de **Arquitetura Orientada a Serviços** do 6º Semestre do curso de **Tecnologia em Sistemas para Internet** da **Fatec Jales - Prof. José Camargo**.

---

## 📌 Contexto e Problema a Solucionar

No dia a dia de farmácias e drogarias, a integração entre controle sanitário, movimentação de estoque e saúde financeira apresenta gargalos críticos:
1. **Controle Sanitário Rigoroso e Prevenção de Fraudes em Receitas**: Portarias da ANVISA e resoluções do CFM exigem que medicamentos de controle especial e antimicrobianos tenham receitas arquivadas e que uma **receita digital não seja reutilizada em múltiplas compras**.
2. **Substituição e Sugestão de Genéricos**: No balcão, é comum o cliente procurar um produto de marca caro; a API precisa sugerir alternativas equivalentes (mesmo princípio ativo e dosagem) de menor custo.
3. **Gestão de Estoque e Vendas com Múltiplos Itens**: Vendas com múltiplos medicamentos no mesmo cupom, controle de estoque mínimo e estorno automático em caso de cancelamento.
4. **Fluxo Financeiro em Tempo Real**: Registro transparente de entradas (vendas), saídas (estornos e compras de distribuidores) e cálculo automático do saldo em caixa.

### A Solução
Uma **API RESTful orientada a serviços** robusta, flexível e preparada para alimentar aplicações móveis, sistemas de PDV (ponto de venda) e portais de autoatendimento farmacêutico.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.4**
- **Spring Data JPA** & **Hibernate**
- **PostgreSQL**
- **Bean Validation & Hibernate Validator** (validação com `@CPF`, `@CNPJ`, `@Email`, etc.)
- **Lombok**
- **Springdoc OpenAPI / Swagger UI 3** (Documentação interativa em `/documentacao`)
- **Git**

---

## 🏗️ Arquitetura e Estrutura de Camadas

```
src/main/java/br/com/fatec/farmacia/
├── FarmaciaApplication.java
├── config/
│   └── SwaggerConfig.java
├── controller/
│   ├── ClienteController.java
│   ├── FornecedorController.java
│   ├── MedicamentoController.java
│   ├── ReceitaDigitalController.java
│   └── VendaController.java
├── dto/
│   ├── EntradaEstoqueDTO.java
│   ├── ItemVendaDTO.java
│   ├── SaldoFinanceiroDTO.java
│   └── VendaDTO.java
├── exception/
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── model/
│   ├── Cliente.java
│   ├── Fornecedor.java
│   ├── ItemVenda.java
│   ├── Medicamento.java
│   ├── MovimentacaoFinanceira.java
│   ├── ReceitaDigital.java
│   └── Venda.java
├── repository/
│   ├── ClienteRepository.java
│   ├── FornecedorRepository.java
│   ├── MedicamentoRepository.java
│   ├── MovimentacaoFinanceiraRepository.java
│   ├── ReceitaDigitalRepository.java
│   └── VendaRepository.java
└── service/
    ├── ClienteService.java
    ├── FornecedorService.java
    ├── MedicamentoService.java
    ├── ReceitaDigitalService.java
    └── VendaService.java
```

---

## ⚙️ Regras de Negócio Implementadas

1. **Validação e Baixa Automática de Receita Digital (Combate a Fraude e Reuso)**:
   - Para remédios com `controlado = true`, o envio de uma receita digital válida é obrigatório.
   - Assim que a venda é processada, a receita muda para o status **`DISPENSADA`**. Se alguém tentar reutilizar a mesma receita, a API rejeita com erro `409 Conflict`.
   - Bloqueio automático de receitas cuja data de validade tenha expirado.
2. **Motor de Sugestão de Genéricos e Alternativas Mais Baratas**:
   - Endpoint `GET /medicamentos/alternativas/{id}`: localiza opções com o mesmo princípio ativo e dosagem, com preço inferior ao de referência.
3. **Múltiplos Itens por Venda com Controle Atômico de Estoque**:
   - Valida estoque de cada produto; realiza a dedução atômica.
4. **Desconto Automático por Forma de Pagamento e Acréscimo**:
   - Vendas em `PIX` ou `DINHEIRO` recebem automaticamente **5% de desconto** à vista quando não informado desconto promocional manual.
5. **Cancelamento de Venda com Estorno Completo**:
   - Ao cancelar uma venda (`PUT /vendas/{id}/cancelar`), os itens retornam automaticamente para o estoque físico e uma movimentação de saída (estorno) é lançada no financeiro.
6. **Módulo Financeiro e Ponto de Reposição**:
   - `GET /vendas/financeiro/saldo` calcula entradas - saídas em tempo real.
   - Abastecimento por fornecedores (`POST /vendas/abastecimento`) incrementa o estoque e lança saída de custo.

---

## 📋 Catálogo de Endpoints Principais

Acesse a documentação interativa:
> **Swagger UI**: `http://localhost:8080/documentacao`

### 🧑‍💼 Clientes (`/clientes`)
- `POST /clientes` - Cadastra cliente (valida CPF, E-mail, telefone, convênio)
- `GET /clientes` - Lista clientes
- `GET /clientes/id/{id}` - Consulta por ID
- `GET /clientes/cpf/{cpf}` - Consulta por CPF
- `GET /clientes/partenome/{nome}` - Filtra por nome
- `PUT /clientes/{id}` - Atualiza cadastro
- `DELETE /clientes/{id}` - Remove cliente

### 💊 Medicamentos e Produtos (`/medicamentos`)
- `POST /medicamentos` - Cadastra produto (princípio ativo, dosagem, EAN, tipo, laboratório, preço, estoque)
- `GET /medicamentos` - Lista catálogo
- `GET /medicamentos/id/{id}` - Consulta por ID
- `GET /medicamentos/codigobarras/{codigoBarras}` - Busca por leitor de código de barras
- `GET /medicamentos/alternativas/{id}` - **Sugestão de genéricos mais baratos**
- `GET /medicamentos/estoque-baixo` - **Alerta de ponto de reposição**
- `GET /medicamentos/controlados/{controlado}` - Filtra controlados
- `GET /medicamentos/tipo/{tipo}` - Filtra por categoria
- `PUT /medicamentos/{id}` - Atualiza medicamento
- `DELETE /medicamentos/{id}` - Exclui medicamento

### 📜 Receitas Digitais (`/receitas`)
- `POST /receitas` - Registra receita digital
- `GET /receitas` - Lista receitas cadastradas
- `GET /receitas/codigo/{codigo}` - Consulta receita por código/hash
- `GET /receitas/paciente/{cpf}` - Consulta histórico do paciente

### 🏭 Fornecedores (`/fornecedores`)
- `POST /fornecedores` - Cadastra distribuidora (validação de CNPJ)
- `GET /fornecedores` - Lista distribuidoras
- `GET /fornecedores/id/{id}` - Consulta por ID
- `PUT /fornecedores/{id}` - Atualiza distribuidora
- `DELETE /fornecedores/{id}` - Exclui distribuidora

### 🛒 Vendas e Fluxo Financeiro (`/vendas`)
- `POST /vendas` - Processa venda com múltiplos itens, baixa de receita e desconto
- `PUT /vendas/{id}/cancelar` - **Cancela venda com estorno de estoque e financeiro**
- `POST /vendas/abastecimento` - Entrada de estoque de distribuidora
- `GET /vendas/financeiro/saldo` - **Saldo de caixa em tempo real**
- `GET /vendas/financeiro/extrato` - Extrato de todas as movimentações
- `GET /vendas` - Histórico de vendas
- `GET /vendas/id/{id}` - Consulta por ID
- `GET /vendas/cliente/{idCliente}` - Compras por cliente

---

## 🚀 Como Executar

### Pré-requisitos
- **Java 21**
- **PostgreSQL** ativo com a base `farmacia`:
  ```sql
  CREATE DATABASE farmacia;
  ```

### Execução
```bash
./mvnw clean spring-boot:run
```
Documentação Swagger: `http://localhost:8080/documentacao`
