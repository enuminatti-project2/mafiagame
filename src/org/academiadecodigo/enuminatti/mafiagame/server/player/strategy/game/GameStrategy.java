package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;

/**
 * Created by codecadet on 13/11/17.
 */
public interface GameStrategy {

    boolean checkWinCondition(GameMaster gameMaster);

    void prepareDay(Player player);

    void prepareNight(Player player);

    String dieMessage();
}
