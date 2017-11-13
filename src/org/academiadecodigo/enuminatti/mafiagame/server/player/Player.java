package org.academiadecodigo.enuminatti.mafiagame.server.player;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.game.GameStrategy;
import org.academiadecodigo.enuminatti.mafiagame.server.player.strategy.visit.VisitStrategy;

/**
 * Created by codecadet on 13/11/17.
 */
public class Player {

    private Server.ServerWorker serverWorker;
    private String name;
    private GameStrategy gameStrategy;
    private VisitStrategy visitStrategy;

    public Player(Server.ServerWorker serverWorker, String name) {
        this.serverWorker = serverWorker;
        this.name = name;
    }

    public void writeToPlayer(String message){
        serverWorker.sendMessage(message);
    }

    public void endGameAction(){
        gameStrategy = null;
        visitStrategy = null;
    }

    public void setStrategies (GameStrategy gameStrategy,VisitStrategy visitStrategy){

        this.gameStrategy = gameStrategy;
        this.visitStrategy = visitStrategy;
    }

    public void doDayStrategy(){
        gameStrategy.prepareDay(this);
    }

    public void doNightStrategy(){
        gameStrategy.prepareNight(this);
    }

    public void disconnect(){
        serverWorker.disconnectPlayer();
    }

    public String getStrategyMessage(){
        return gameStrategy.dieMessage();
    }


    /**
     * Receives the list of mafia , villagers , others
     * @return
     */
    public boolean checkWinCondition(GameMaster gameMaster){
        gameStrategy.checkWinCondition(gameMaster);
        return false;
    }

    public void doVisitStrategy(String name , GameMaster gameMaster){
        visitStrategy.performVisit(name,gameMaster);
    }

}
