package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

public class ContaService {

    private ConnectionFactory connFactory;

    public ContaService() {
        this.connFactory = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection conn = connFactory.getConnection();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connFactory.getConnection();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        if (!conta.getAtiva()) {
            throw new RegraDeNegocioException("Conta inativa!");
        }

        Connection conn = connFactory.getConnection();
        new ContaDAO(conn).alterarSaldo(conta.getNumero(), conta.getSaldo().subtract(valor));
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        if (!conta.getAtiva()) {
            throw new RegraDeNegocioException("Conta inativa!");
        }

        Connection conn = connFactory.getConnection();
        new ContaDAO(conn).alterarSaldo(conta.getNumero(), conta.getSaldo().add(valor));
    }

    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroDaContaOrigem, valor);
        this.realizarDeposito(numeroDaContaDestino, valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conn = connFactory.getConnection();
        new ContaDAO(conn).remover(conta.getNumero());
    }

    public void encerrarLogico(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (!conta.getAtiva()) {
            throw new RegraDeNegocioException("Conta já está desativada!");
        }

        Connection conn = connFactory.getConnection();
        new ContaDAO(conn).desativar(conta.getNumero());
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connFactory.getConnection();
        Conta conta = new ContaDAO(conn).procurarPorNumero(numero);

        if (conta == null) {
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
        }

        return conta;
    }
}
