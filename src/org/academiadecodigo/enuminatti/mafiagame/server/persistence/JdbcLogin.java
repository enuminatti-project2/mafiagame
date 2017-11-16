package org.academiadecodigo.enuminatti.mafiagame.server.persistence;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.academiadecodigo.enuminatti.mafiagame.utils.Security;

import java.sql.*;


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
        if (!userExists(username)){
            return false;
        }

        String query = "";
        if (password != null) {
            query = "SELECT * FROM "+table+" WHERE username = ? AND pwd = ?";
        } else {
            query = "SELECT * FROM "+table+" WHERE username = ?";
        }

        PreparedStatement pStatement = null;

        try {
            pStatement = dbConnection.prepareStatement(query);
            pStatement.setString(1, username);

            if (password!= null) {
                pStatement.setString(2, password);
            }

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
        String query = "INSERT INTO "+table+"(username,pwd) VALUES (?,?)";

        PreparedStatement pStatement = null;
        try {


            pStatement = dbConnection.prepareStatement(query);
            pStatement.setString(1, user);
            pStatement.setString(2, pwd);
            pStatement.executeUpdate();

        } catch ( MySQLIntegrityConstraintViolationException e) {
            System.out.println("User allready exists");
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
        return authenticate(user, pwd);
    }

    public boolean userExists(String username) {

        // create a query
        String query = "SELECT * FROM "+table+" WHERE username = ?";

        PreparedStatement pStatement = null;

        ResultSet resultSet = null;
        try {

            pStatement = dbConnection.prepareStatement(query);
            pStatement.setString(1, username);
            resultSet = pStatement.executeQuery();

            return resultSet.next();

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

}