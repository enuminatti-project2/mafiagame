package org.academiadecodigo.enuminatti.mafiagame.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameMaster implements Runnable{
    //private static final int TIMETOSTART = ;
    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;
    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;
    private boolean gameHasStarted;

    public GameMaster() {
        listOfPlayers = new HashMap<>();
        startGame = Executors.newSingleThreadScheduledExecutor();

    }

    private void broadcastToPlayers(String type, String message) {
        for (String playerNick : listOfPlayers.keySet()) {
            listOfPlayers.get(playerNick).sendMessage(message);
        }
    }

    private void setDayAndNight() {
    }

    private void setPlayerRole() {

    }

    private void calculateVotes() {

    }

    public void receiveAndDecode(String message) {
        // Map<String, Integer> votes = new HashMap<>();
    }

    private void sendNickList() {

    }

    public boolean addNick(String nick, Server.PlayerHandler playerHandler){
        if (listOfPlayers.get(nick) != null){
            return false;
        }
        listOfPlayers.put(nick, playerHandler);
        System.out.println("Player added");

        if (!gameHasStarted) { // Se o jogo ainda não começou, reset ao timer
            if (schedule != null) {
                schedule.cancel(true);
            }
            schedule = startGame.schedule(this, 10, TimeUnit.SECONDS); //substituir this por uma runnable task
        }
        return true;
    }


    @Override
    public void run() {

        System.out.println("Let the game Begin");
        gameHasStarted = true;

    }
}
