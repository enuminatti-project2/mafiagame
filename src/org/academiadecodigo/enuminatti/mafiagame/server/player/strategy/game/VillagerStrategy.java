package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;


/**
 * Created by codecadet on 13/11/17.
 */
public class VillagerStrategy extends GenericGameStrategy {


    @Override
    public String dieMessage() {
        return "An innocent villager.";
    }

    @Override
    public boolean checkWinCondition(GameMaster gameMaster) {
        return gameMaster.getMafiosiNicks().size() == 0;
    }

    @Override
    public void prepareNight(Player player) {
        return;
    }

    public String role(){
        return "Villager";
    }
}
