package org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.visit;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;

/**
 * Created by codecadet on 13/11/17.
 */
public interface VisitStrategy {

    void performVisit(String name, GameMaster gameMaster);

}
