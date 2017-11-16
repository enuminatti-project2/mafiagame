package org.academiadecodigo.enuminatti.mafiagame.server.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Samuel Laço on 15/11/17.
 */
public class JdbcLogin {

    private Connection dbConnection;

    private String table;

    public JdbcLogin(Connection dbConnection) {
        this.dbConnection = dbConnection;
        this.table = "Players";
    }

    public boolean authenticate(String username, String password) {
        if (!userExists(username)) {
            return false;
        }

        String query = "SELECT * FROM " + table + " WHERE username = ? AND pwd = ?";

        PreparedStatement pStatement = null;

        try {
            pStatement = dbConnection.prepareStatement(query);
            pStatement.setString(1, username);

            pStatement.setString(2, password);

            return pStatement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert pStatement != null;
                pStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean addUser(String user, String pwd) {
        if (userExists(user)) {
            return false;
        }

        String query = "INSERT INTO " + table + " (username, pwd) VALUES (?, ?)";

        try {
            PreparedStatement pStatement = dbConnection.prepareStatement(query);
            pStatement.setString(1, user);
            pStatement.setString(2, pwd);
            pStatement.executeUpdate();

            pStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authenticate(user, pwd);
    }

    public boolean userExists(String username) {

        // create a query
        String query = "SELECT * FROM " + table + " WHERE username = ?";

        try {

            PreparedStatement pStatement = dbConnection.prepareStatement(query);
            pStatement.setString(1, username);
            ResultSet resultSet = pStatement.executeQuery();

            boolean userExists = resultSet.next();

            pStatement.close();

            return userExists;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}