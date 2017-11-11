package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public enum Role {

    MAFIA,
    VILLAGER;


    public static void setRolesToAllPlayers(Map<String, Server.PlayerHandler> listOfPlayers,
                                            List<String> mafiaListOfPlayers, List<String> villagerListOfPlayers) {

        int numberOfMafia = (int) Math.ceil(listOfPlayers.size() / 5.0); // always at least one mafia

        System.out.println("Number of mafia is: " + numberOfMafia);
        List<String> players = new LinkedList<>(listOfPlayers.keySet());

        for (int i = 0; i < numberOfMafia; i++) {

            int roll = (int) (Math.random() * players.size());
            String selected = players.remove(roll);
            listOfPlayers.get(selected).setRole(MAFIA);

            mafiaListOfPlayers.add(selected);
        }

        for (String player : players) {
            listOfPlayers.get(player).setRole(VILLAGER);
            villagerListOfPlayers.add(player);
        }
    }
}
