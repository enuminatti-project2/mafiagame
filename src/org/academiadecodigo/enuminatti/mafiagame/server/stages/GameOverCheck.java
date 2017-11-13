package org.academiadecodigo.enuminatti.mafiagame.server.stages;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;

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
        List<String> thirdparties = gameMaster.getThirdParties();
        List<String> villagers = gameMaster.getVillagers();
        List<String> mafias = gameMaster.getMafias();

        String[] winningPlayers;
        if ((winningPlayers = getWinningPlayer(thirdparties)) != null) {
            // a single third-party player won
        } else if ((winningPlayers = getWinningFaction(mafias, villagers)) != null) {
            // a faction won
        } else {
            goNext();
        }

        // winningPlayers won the game
    }

    private String[] getWinningPlayer(List<String> thirdparties) {
        Map<String, Server.PlayerHandler> allPlayers = gameMaster.getListOfPlayers();

        for (String nick : thirdparties) {
            if (allPlayers.get(nick).checkWinCondition()) {
                return new String[]{nick};
            }
        }

        return null;
    }

    private String[] getWinningFaction(List<String> mafias, List<String> villagers) {

        // first check if the villagers won
        Server.PlayerHandler villagerPlayer = villagers.isEmpty() ? null :
                gameMaster.getListOfPlayers().get(villagers.get(0));

        if (villagerPlayer != null && villagerPlayer.checkWinCondition()) {
            // villagers won
            return villagers.toArray(new String[0]);
        }

        // then check if the mafia won
        Server.PlayerHandler mafiaPlayer = mafias.isEmpty() ? null :
                gameMaster.getListOfPlayers().get(mafias.get(0));

        if (mafiaPlayer != null && mafiaPlayer.checkWinCondition()) {
            // mafia won
            return mafias.toArray(new String[0]);
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
