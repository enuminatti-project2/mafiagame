package org.academiadecodigo.enuminatti.mafiagame.server.persistence;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Samuel Laço on 15/11/17.
 */
public class ConnectionManager {
    private Connection connection = null;

    public Connection getConnection() {

        try {
            if (connection == null) {
                String dbUrl = "jdbc:mysql://192.168.1.18/Mafia";
                String user = "mafiagame";
                String pwd = "cenas";
                connection = DriverManager.getConnection(dbUrl, user, pwd);
            }
        } catch (SQLException ex) {
            System.out.println("Failure to connect to database : " + ex.getMessage());
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("Failure to close database connections: " + ex.getMessage());
        }
    }
}