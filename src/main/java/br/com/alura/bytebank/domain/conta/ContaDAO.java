package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection conn) {
        this.conn = conn;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);

        PreparedStatement pst;
        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            pst = conn.prepareStatement(sql);

            pst.setInt(1, conta.getNumero());
            pst.setBigDecimal(2, BigDecimal.ZERO);
            pst.setString(3, dadosDaConta.dadosCliente().nome());
            pst.setString(4, dadosDaConta.dadosCliente().cpf());
            pst.setString(5, dadosDaConta.dadosCliente().email());

            pst.executeUpdate();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta";

        try {
            PreparedStatement pst = conn.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Integer numero = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);
                Conta conta = new Conta(numero, saldo, cliente);

                contas.add(conta);
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contas;
    }

    public void remover(Integer numero) {
        String sql = "DELETE FROM conta WHERE numero = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, numero);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Conta procurarPorNumero(Integer numero) {
        Conta conta = null;

        String sql = "SELECT * FROM conta WHERE numero = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, numero);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Integer num = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);
                conta = new Conta(num, saldo, cliente);
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return conta;
    }

}
