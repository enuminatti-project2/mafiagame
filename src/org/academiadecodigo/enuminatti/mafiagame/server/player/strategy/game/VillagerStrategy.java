package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;


/**
 * Created by codecadet on 13/11/17.
 */
public class VillagerStrategy extends GenericGameStrategy {


    @Override
    public String dieMessage() {
        return "They were a Villager!";
    }

    @Override
    public boolean checkWinCondition(GameMaster gameMaster) {
        if(gameMaster.getMafiosiNicks().size() == 0){
            return true;
        }

        return false;
    }

    @Override
    public void prepareNight(Player player) {
        return;
    }

    public String role(){
        return "Villager";
    }
}
