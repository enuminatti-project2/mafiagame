package org.academiadecodigo.enuminatti.mafiagame.server.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by codecadet on 16/11/17.
 */
public class JdbcScore {

    private Connection dbConnection;

    public JdbcScore(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public String getPoints(String username){

        StringBuilder stringBuilder = new StringBuilder(username + " ");
        String query = "SELECT * FROM points WHERE username = ? ;";

        try {

            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1,username);
            ResultSet resultplayerpoints =  preparedStatement.executeQuery();

            if (resultplayerpoints.next()){
                stringBuilder.append(String.valueOf(resultplayerpoints.getInt("gamesWon"))).append(" ");
                stringBuilder.append(String.valueOf(resultplayerpoints.getInt("gamesLost"))).append(" ");
                stringBuilder.append(String.valueOf(resultplayerpoints.getInt("turnSurvived"))).append(" ");
                stringBuilder.append(String.valueOf(resultplayerpoints.getInt("totalPoints"))).append(" ");
            }

            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public void updatePoints(String username,int gameWon , int turnsSurvived ){

        String query = "UPDATE points SET gamesWon = gamesWon + ? , gamesLost = gamesLost " +
                "+ ? , turnSurvived = turnSurvived + ? , totalPoints = totalPoints + ? WHERE username = ? ;";


        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = dbConnection.prepareStatement(query);

            preparedStatement.setInt(3,turnsSurvived);
            preparedStatement.setInt(4,turnsSurvived*100 + gameWon * 50);
            preparedStatement.setString(5,username);

            if(gameWon == 1){
                preparedStatement.setInt(1,1);
                preparedStatement.setInt(2,0);
                preparedStatement.executeUpdate();
                return;
            }

            preparedStatement.setInt(1,0 );
            preparedStatement.setInt(2,1);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
