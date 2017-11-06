package org.academiadecodigo.enuminatti.mafiagame.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameMaster {
    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;

    public GameMaster() {
        listOfPlayers = new HashMap<>();
    }

    private void broadcastToPlayers(String type, String message) {
        for (String playerNick : listOfPlayers.keySet()) {
            listOfPlayers.get(playerNick).sendMessage(message);
        }
    }

    private void setDayAndNight() {
    }

    private void setPlayerRole() {

    }

    private void calculateVotes() {

    }

    private void receiveAndDecode(String message) {
        // Map<String, Integer> votes = new HashMap<>();
    }

    private void sendNickList() {

    }


}
