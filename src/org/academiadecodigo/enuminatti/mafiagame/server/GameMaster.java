package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;
import org.academiadecodigo.enuminatti.mafiagame.server.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameMaster implements Runnable{
    private static final int TIMETOSTART = 10;
    public Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;
    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;
    private boolean gameHasStarted;
    private boolean night;
    private Map<String, Integer> votesCount;

    public GameMaster() {
        listOfPlayers = new HashMap<>();
        startGame = Executors.newSingleThreadScheduledExecutor();

        votesCount = new HashMap<>();
    }

    private void broadcastToPlayers(String message) {
        for (String playerNick : listOfPlayers.keySet()) {
            listOfPlayers.get(playerNick).sendMessage(message);
        }
    }

    private void setDayAndNight() {
        night = !night;
        broadcastToPlayers(EncodeDecode.NIGHT.encode(Boolean.toString(night)));
    }

    private void setPlayerRole() {

    }

    private void addVote(String nickname) {

        if (votesCount.size() != listOfPlayers.size()) {
            if (votesCount.containsKey(nickname)) {
                votesCount.replace(nickname, votesCount.get(nickname) + 1);
                return;
            }
            votesCount.put(nickname, 1);
        } else {
            calculateVotes();
        }
    }

    private void calculateVotes() {

        String mostVotedPlayer = mafiosiNicks.get(0);

        Set<String> votedPlayers = votesCount.keySet();

        for (String player : votedPlayers) {

            if (votesCount.get(mostVotedPlayer) > votesCount.get(player)){
                mostVotedPlayer = player;
            }
        }
        votesCount.clear();
        killPlayer(mostVotedPlayer);

    }

    public void receiveAndDecode(String message) {

        String tag = EncodeDecode.getStartTag(message);

        switch (tag) {
            case "<VOTE>":
                addVote(EncodeDecode.VOTE.decode(message));
                break;
            case "<MSG>":
                broadcastToPlayers(EncodeDecode.MESSAGE.decode(message));
                break;
            case "<NIGHT>":
                setDayAndNight();
                break;
        }

        /* Suggestion of implementation
        EncodeDecode enumTag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        switch (enumTag){

            case MESSAGE:
                break;
            case NICK:
                break;
            case NICKOK:
                break;
            case TIMER:
                break;
            case NICKMESSAGE:
                break;
            case VOTE:
                break;
            default: all the cases to ignore
                break;
        }

        */


    }

    private void sendNickList() {

        String nickList = String.join(" ", listOfPlayers.keySet());

        broadcastToPlayers(EncodeDecode.NICKLIST.encode(nickList));

    }

    private void killPlayer(String nickname){

        broadcastToPlayers("Player " + nickname + " was sentenced to death. The role was: "
                            + listOfPlayers.get(nickname).getRole().toString());
        listOfPlayers.remove(nickname);
        broadcastToPlayers(nickname + " has disconnected from the game.");
        sendNickList();
    }

    public boolean addNick(String nick, Server.PlayerHandler playerHandler){
        if (listOfPlayers.get(nick) != null){
            return false;
        }
        listOfPlayers.put(nick, playerHandler);
        System.out.println("Player added");
        broadcastToPlayers(nick + " has entered to the game.");

        if (!gameHasStarted && listOfPlayers.size() >= 1) { // Se o jogo ainda não começou, reset ao timer
            if (schedule != null) {
                schedule.cancel(true);
            }
            schedule = startGame.schedule(this, TIMETOSTART, TimeUnit.SECONDS); //substituir this por uma runnable task
            broadcastToPlayers(EncodeDecode.TIMER.encode(Integer.toString(TIMETOSTART))); //Send boadcast to reset the timer

            setRolesToPlayers();
        }
        return true;
    }

    private void setRolesToPlayers(){

        Set<String> playerHandlerSet = listOfPlayers.keySet();

        for (String playerNick : playerHandlerSet) {
            Server.PlayerHandler player = listOfPlayers.get(playerNick);
            player.setRole(Role.setRoleToPlayer());
            listOfPlayers.replace(playerNick, player);
        }
    }


    @Override
    public void run() {

        System.out.println("Let the game Begin");
        gameHasStarted = true;
        broadcastToPlayers(EncodeDecode.START.encode("begin"));
        //initGame();

    }

    boolean kickPlayer(String nickname ) {
        // After is needed to kick player from mafia list or vilager list
        return listOfPlayers.remove(nickname) != null;
    }
}
