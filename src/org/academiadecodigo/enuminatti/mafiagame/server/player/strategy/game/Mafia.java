package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 13/11/17.
 */
public class Mafia extends GenericGameStrategy {

    @Override
    public String dieMessage() {
        return "They were Mafia!";
    }

    @Override
    public boolean checkWinCondition(GameMaster gameMaster) {
        if(gameMaster.mafiaList.size() >= gameMaster.villagersList.size()){
            return true;
        }

        return false;
    }

    @Override
    public void prepareNight(Player player) {
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_TALK,null);
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_VOTE,null);
    }
}
