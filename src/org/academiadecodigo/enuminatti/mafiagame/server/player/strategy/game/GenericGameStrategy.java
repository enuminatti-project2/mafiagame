package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 13/11/17.
 */
public abstract class GenericGameStrategy implements GameStrategy {

    @Override
    public void prepareDay(Player player){
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_TALK,null);
        Broadcaster.broadcastToPlayer(player, EncodeDecode.ALLOW_VOTE,null);
    }

}
