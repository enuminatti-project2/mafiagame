package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.Map;

/**
 * Created by Daniel Baeta on 14/11/17.
 */
public class ScoreCalculator {

    private static int roundNumber = 0;
    private static int points = 0;

    static void calculate(String nickname, Player player) {

        points = roundNumber * 100;
        Broadcaster.broadcastToPlayer(player, EncodeDecode.SCORE, points + " " + roundNumber);
        //Get the list of players in a file, check if the player's there and increment rounds killed.
    }

    static void nextRound() {
        roundNumber++;
    }

    static void calculateEnd(Map<String, Player> listOfPlayers) {

        for (Player player :
                listOfPlayers.values()) {
            Broadcaster.broadcastToPlayer(player, EncodeDecode.SCORE, points + " " + roundNumber);
            //Get the list of players in a file, check who's there, and overwrite incrementing rounds survived.
        }
    }
}
