package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.persistence.ConnectionManager;
import org.academiadecodigo.enuminatti.mafiagame.server.persistence.JdbcScore;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Baeta on 14/11/17.
 */
public class ScoreCalculator {

    private static int roundNumber = 0;
    private static int points = 0;
    private static JdbcScore jdbcScore = new JdbcScore(ConnectionManager.getConnection());

    static void calculate(String nickname, Player player) {

        points = roundNumber * 100;
        jdbcScore.updatePoints(nickname,0,roundNumber);
        Broadcaster.broadcastToPlayer(player, EncodeDecode.SCORE, points + " " + roundNumber);

        //Get the list of players in a file, check if the player's there and increment rounds killed.
    }

    public static void resetCalculator(){
        roundNumber = 0;
        points = 0;
    }

    public static void nextRound() {
        roundNumber++;
    }

    public static void pointsUpdateWin(List<String> winners){

        for (String s:winners) {
            jdbcScore.updatePoints(s,1,roundNumber);
        }

    }

    public static void pointsUpdateLose(List<String> loser){

        for (String s:loser) {
            jdbcScore.updatePoints(s,0,roundNumber);
        }

    }

    static void calculateEnd(Map<String, Player> listOfPlayers) {

        for (Player player :
                listOfPlayers.values()) {
            Broadcaster.broadcastToPlayer(player, EncodeDecode.SCORE, points + " " + roundNumber);
            //Get the list of players in a file, check who's there, and overwrite incrementing rounds survived.
        }
    }
}
