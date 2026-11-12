package br.com.fatec.farmacia.service;

import br.com.fatec.farmacia.dto.EntradaEstoqueDTO;
import br.com.fatec.farmacia.dto.ItemVendaDTO;
import br.com.fatec.farmacia.dto.SaldoFinanceiroDTO;
import br.com.fatec.farmacia.dto.VendaDTO;
import br.com.fatec.farmacia.model.*;
import br.com.fatec.farmacia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private ReceitaDigitalRepository receitaDigitalRepository;

    @Autowired
    private ReceitaDigitalService receitaDigitalService;

    @Autowired
    private MovimentacaoFinanceiraRepository financeiroRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Transactional
    public Venda realizarVenda(VendaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente com ID " + dto.getIdCliente() + " não encontrado."));

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter ao menos um item.");
        }

        List<ItemVenda> itensVenda = new ArrayList<>();
        BigDecimal subtotalGeral = BigDecimal.ZERO;
        boolean contemControlado = false;

        for (ItemVendaDTO itemDTO : dto.getItens()) {
            Medicamento med = medicamentoRepository.findById(itemDTO.getIdMedicamento())
                    .orElseThrow(() -> new IllegalArgumentException("Medicamento com ID " + itemDTO.getIdMedicamento() + " não encontrado."));

            // Regra 1: Validação de Estoque
            if (med.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para '" + med.getNome() + 
                        "'. Disponível: " + med.getQuantidadeEstoque() + ", Solicitado: " + itemDTO.getQuantidade());
            }

            if (Boolean.TRUE.equals(med.getControlado())) {
                contemControlado = true;
            }

            // Baixa no estoque
            med.setQuantidadeEstoque(med.getQuantidadeEstoque() - itemDTO.getQuantidade());
            medicamentoRepository.save(med);

            // Monta o item
            BigDecimal precoItem = med.getPreco();
            BigDecimal subtotalItem = precoItem.multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));
            subtotalGeral = subtotalGeral.add(subtotalItem);

            ItemVenda iv = new ItemVenda();
            iv.setMedicamento(med);
            iv.setQuantidade(itemDTO.getQuantidade());
            iv.setPrecoUnitario(precoItem);
            iv.setSubtotal(subtotalItem);
            itensVenda.add(iv);
        }

        // Regra 2: Controle de Receita Digital para controlados
        ReceitaDigital receitaVinculada = null;
        if (contemControlado) {
            if (dto.getCodigoReceitaDigital() == null || dto.getCodigoReceitaDigital().trim().isEmpty()) {
                throw new IllegalArgumentException("A venda contém medicamentos de controle especial e exige o 'codigoReceitaDigital'.");
            }
            receitaVinculada = receitaDigitalService.dispensarReceita(dto.getCodigoReceitaDigital());
        }

        // Regra 3: Cálculo financeiro (desconto, acréscimo e desconto automático por forma de pagamento)
        BigDecimal desconto = dto.getValorDesconto() != null ? dto.getValorDesconto() : BigDecimal.ZERO;
        BigDecimal acrescimo = dto.getValorAcrescimoTaxa() != null ? dto.getValorAcrescimoTaxa() : BigDecimal.ZERO;

        // Se pagamento via PIX ou DINHEIRO e não foi informado desconto manual, aplica desconto promocional de 5%
        if (("PIX".equalsIgnoreCase(dto.getFormaPagamento()) || "DINHEIRO".equalsIgnoreCase(dto.getFormaPagamento()))
                && desconto.compareTo(BigDecimal.ZERO) == 0) {
            desconto = subtotalGeral.multiply(BigDecimal.valueOf(0.05)); // 5% de desconto automático à vista
        }

        BigDecimal valorTotal = subtotalGeral.subtract(desconto).add(acrescimo);
        if (valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            valorTotal = BigDecimal.ZERO;
        }

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setItens(itensVenda);
        venda.setSubtotalItens(subtotalGeral);
        venda.setValorDesconto(desconto);
        venda.setValorAcrescimoTaxa(acrescimo);
        venda.setValorTotal(valorTotal);
        venda.setFormaPagamento(dto.getFormaPagamento().toUpperCase());
        venda.setReceitaDigital(receitaVinculada);
        venda.setStatusVenda("CONCLUIDA");
        venda.setDataHora(LocalDateTime.now());

        Venda salva = vendaRepository.save(venda);

        // Registro de ENTRADA no Fluxo Financeiro
        MovimentacaoFinanceira mov = new MovimentacaoFinanceira();
        mov.setTipo("ENTRADA");
        mov.setValor(salva.getValorTotal());
        mov.setDescricao("Venda #" + salva.getIdVenda() + " (" + salva.getFormaPagamento() + ") - Cliente: " + cliente.getNome());
        mov.setIdReferencia(salva.getIdVenda());
        mov.setDataHora(LocalDateTime.now());
        financeiroRepository.save(mov);

        return salva;
    }

    /**
     * Regra 4: Cancelamento de Venda com Estorno total de estoque e financeiro
     */
    @Transactional
    public Venda cancelarVenda(Long idVenda) {
        Venda venda = vendaRepository.findById(idVenda)
                .orElseThrow(() -> new IllegalArgumentException("Venda com ID " + idVenda + " não encontrada."));

        if ("CANCELADA".equalsIgnoreCase(venda.getStatusVenda())) {
            throw new IllegalStateException("A venda #" + idVenda + " já se encontra cancelada.");
        }

        // Estorno das mercadorias para o estoque
        for (ItemVenda item : venda.getItens()) {
            Medicamento med = item.getMedicamento();
            med.setQuantidadeEstoque(med.getQuantidadeEstoque() + item.getQuantidade());
            medicamentoRepository.save(med);
        }

        venda.setStatusVenda("CANCELADA");
        Venda vendaAtualizada = vendaRepository.save(venda);

        // Registro de SAÍDA no fluxo financeiro (estorno ao cliente)
        MovimentacaoFinanceira movEstorno = new MovimentacaoFinanceira();
        movEstorno.setTipo("SAIDA");
        movEstorno.setValor(venda.getValorTotal());
        movEstorno.setDescricao("Estorno por Cancelamento da Venda #" + venda.getIdVenda());
        movEstorno.setIdReferencia(venda.getIdVenda());
        movEstorno.setDataHora(LocalDateTime.now());
        financeiroRepository.save(movEstorno);

        return vendaAtualizada;
    }

    /**
     * Entrada de Estoque de Fornecedores (Distribuidora) -> Alimenta estoque e gera SAÍDA financeira
     */
    @Transactional
    public Medicamento registrarEntradaEstoque(EntradaEstoqueDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(dto.getIdFornecedor())
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor com ID " + dto.getIdFornecedor() + " não encontrado."));

        Medicamento med = medicamentoRepository.findById(dto.getIdMedicamento())
                .orElseThrow(() -> new IllegalArgumentException("Medicamento com ID " + dto.getIdMedicamento() + " não encontrado."));

        med.setQuantidadeEstoque(med.getQuantidadeEstoque() + dto.getQuantidade());
        medicamentoRepository.save(med);

        BigDecimal custoTotal = dto.getCustoUnitario().multiply(BigDecimal.valueOf(dto.getQuantidade()));

        MovimentacaoFinanceira movCompra = new MovimentacaoFinanceira();
        movCompra.setTipo("SAIDA");
        movCompra.setValor(custoTotal);
        movCompra.setDescricao("Compra de " + dto.getQuantidade() + " un de '" + med.getNome() + "' junto a " + fornecedor.getRazaoSocial());
        movCompra.setIdReferencia(med.getIdMedicamento());
        movCompra.setDataHora(LocalDateTime.now());
        financeiroRepository.save(movCompra);

        return med;
    }

    public SaldoFinanceiroDTO obterSaldoFinanceiro() {
        List<MovimentacaoFinanceira> todas = financeiroRepository.findAll();
        BigDecimal totalEntradas = todas.stream()
                .filter(m -> "ENTRADA".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = todas.stream()
                .filter(m -> "SAIDA".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoLiquido = totalEntradas.subtract(totalSaidas);
        return new SaldoFinanceiroDTO(totalEntradas, totalSaidas, saldoLiquido);
    }

    public List<MovimentacaoFinanceira> listarMovimentacoesFinanceiras() {
        return financeiroRepository.findAll();
    }

    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> pesquisarVendaPorId(Long id) {
        return vendaRepository.findById(id);
    }

    public List<Venda> listarVendasPorCliente(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente com ID " + idCliente + " não encontrado."));
        return vendaRepository.findByCliente(cliente);
    }
}
