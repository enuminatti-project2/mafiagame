package org.academiadecodigo.enuminatti.mafiagame.server.persistence;


import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Samuel Laço on 15/11/17.
 */
public class ConnectionManager {
    private static Connection connection = null;

    public static Connection getConnection() {

        try {
            if (connection == null) {
                String dbUrl = "jdbc:mysql://"+ Constants.SQL_ADDRESS +"/Mafia?useSSL=false";
                String user = Constants.SQL_USERNAME;
                String pwd = Constants.SQL_PASSWORD;
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