package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 13/11/17.
 */
public class MafiaStrategy extends GenericGameStrategy {

    @Override
    public String dieMessage() {
        return "One Mafia member less!";
    }


    @Override
    public boolean checkWinCondition(GameMaster gameMaster) {
        return gameMaster.getMafiosiNicks().size() >= gameMaster.getVillagersNicks().size();
    }

    @Override
    public void prepareNight(Player player) {
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_TALK,null);
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_VOTE,null);
    }

    @Override
    public String role() {
        return "Mafia";
    }

    @Override
    public String roleDescription() {
        return "As a mobster, during nighttime you and your fellow mobsters can vote to kill one villager. " +
                "During the day, convince the villagers to vote someone other than yourself! " +
                "You win when the mobsters outnumber the villagers, if the villagers kill all the mobsters, you lose!";
    }
}
