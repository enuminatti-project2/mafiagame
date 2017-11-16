package org.academiadecodigo.enuminatti.mafiagame.server.persistence;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.academiadecodigo.enuminatti.mafiagame.utils.Security;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by Samuel Laço on 15/11/17.
 */
public class JdbcUserService {

    private Connection dbConnection;

    public JdbcUserService(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public boolean userExists(String username){
        return authenticate(username, null);
    }

    public boolean authenticate(String username, String password) {
        String query = "";
        if (password != null) {
            query = "SELECT * FROM Players WHERE username ='" + username + "' AND pwd = '" + password + "'";
        } else {
            query = "SELECT * FROM Players WHERE username ='" + username + "'";
        }

        Statement statement = null;
        ResultSet resultSet;

        try {
            statement = dbConnection.createStatement();
            return statement.executeQuery(query).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(String user, String pwd) {
        String query = "INSERT INTO Players(username,pwd) VALUES ('" + user + "' , '" + pwd + "')";

        Statement statement = null;
        try {
            statement = dbConnection.createStatement();
            statement.executeUpdate(query);

        } catch ( MySQLIntegrityConstraintViolationException e) {
            System.out.println("User allready exists");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authenticate(user, pwd);
    }
}