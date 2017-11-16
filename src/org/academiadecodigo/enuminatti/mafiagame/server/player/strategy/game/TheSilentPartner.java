package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 16/11/17.
 */
public class TheSilentPartner extends MafiaStrategy {

    @Override
    public void prepareNight(Player player){
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_VOTE,null);
    }

    @Override
    public String role() {
        return "Silent Partner";
    }

    public String roleDescription() {
        return "As a Silent Partner, during nighttime you may vote to kill one villager but you cant talk. " +
                "During the day, convince the villagers to vote someone other than yourself! " +
                "You win when the mobsters outnumber the villagers, if the villagers kill all the mobsters, you lose!";
    }
}
