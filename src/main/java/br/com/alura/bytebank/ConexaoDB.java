package br.com.alura.bytebank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    private static final String URL = "jdbc:mysql://localhost:3306/byte_bank?user=alura&password=alura";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Connected!");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
