package org.academiadecodigo.enuminatti.mafiagame.server.stages;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameOverCheck implements Stage {

    private GameMaster gameMaster;

    public GameOverCheck(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }


    @Override
    public void runStage(Set<String> activePlayersOnStage, Set<String> possibleTargets) {

        System.out.println("checking if anyone won");

        List<String> thirdparties = gameMaster.getThirdParties();
        List<String> villagers = gameMaster.getVillagersNicks();
        List<String> mafias = gameMaster.getMafiosiNicks();

        String winningPlayer;
        String winningFaction;
        String winningMessage = null;
        if ((winningPlayer = getWinningPlayer(thirdparties)) != null) {
            // a single third-party player won
            System.out.println("single player victory");
            winningMessage = winningPlayer + " has won the game!";
        } else if ((winningFaction = getWinningFaction(mafias, villagers)) != null) {
            // a faction won
            System.out.println("faction won");
            winningMessage = "The " + winningFaction + " have won the game!";
        }

        if (gameMaster.getListOfPlayers().size() < Constants.MIN_PLAYERS) {
            Broadcaster.broadcastToPlayers(gameMaster.getListOfPlayers(),
                    EncodeDecode.MESSAGE, "Not enough players to continue the game. Exiting the game");
            gameMaster.gameOver();
            return;
        }

        if (winningMessage == null) {
            goNext();
            return;
        }

        // winningPlayers won the game
        Broadcaster.broadcastToPlayers(gameMaster.getListOfPlayers(),
                EncodeDecode.MESSAGE, winningMessage);

        gameMaster.gameOver();

    }

    private String getWinningPlayer(List<String> thirdparties) {
        System.out.println("checking if single player won");
        Map<String, Player> allPlayers = gameMaster.getListOfPlayers();

        for (String nick : thirdparties) {
            if (allPlayers.get(nick).checkWinCondition()) {
                return nick;
            }
        }
        System.out.println("no single player won");
        return null;
    }

    private String getWinningFaction(List<String> mafias, List<String> villagers) {
        System.out.println("checking if faction won");
        // first check if the villagers won
        Player villagerPlayer = villagers.isEmpty() ? null :
                gameMaster.getListOfPlayers().get(villagers.get(0));

        if (villagerPlayer != null && villagerPlayer.checkWinCondition()) {
            // villagers won
            return "villagers";
        }

        // then check if the mafia won
        Player mafiaPlayer = mafias.isEmpty() ? null :
                gameMaster.getListOfPlayers().get(mafias.get(0));

        if (mafiaPlayer != null && mafiaPlayer.checkWinCondition()) {
            // mafia won
            return "mafia";
        }

        return null;
    }

    private void goNext() {
        gameMaster.toggleDayAndNight();
        gameMaster.changeStage(Stages.TALK);
    }

    @Override
    public void cleanup() {
        // no cleanup needed in this one
    }
}
