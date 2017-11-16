package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game.MafiaStrategy;
import org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game.TheSilentPartner;
import org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game.VillagerStrategy;
import org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.visit.NoVisitStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class RoleFactory {

    public static void setRolesToAllPlayers(Map<String, Player> listOfPlayers,
                                            List<String> mafiaListOfPlayers, List<String> villagerListOfPlayers) {

        int numberOfMafia = (int) Math.ceil(listOfPlayers.size() / 5.0); // always at least one mafia

        List<String> players = new LinkedList<>(listOfPlayers.keySet());

        for (int i = 0; i < numberOfMafia; i++) {

            int roll = (int) (Math.random() * players.size());
            String selected = players.remove(roll);

            if(i == 1){
                listOfPlayers.get(selected).setStrategies(new TheSilentPartner(), new NoVisitStrategy());
                mafiaListOfPlayers.add(selected);
                continue;
            }


            listOfPlayers.get(selected).setStrategies(new MafiaStrategy(), new NoVisitStrategy());

            mafiaListOfPlayers.add(selected);
        }

        for (String player : players) {
            listOfPlayers.get(player).setStrategies(new VillagerStrategy(), new NoVisitStrategy());
            villagerListOfPlayers.add(player);
        }
    }
}
