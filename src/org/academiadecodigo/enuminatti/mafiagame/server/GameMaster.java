package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameMaster implements Runnable {

    private static final int TIMETOSTART = 1;
    private static final int MINPLAYERS = 3; //1 PLAYER

    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;

    private boolean gameHasStarted;
    private boolean night;

    private Map<String, Integer> votesCount;

    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;

    public GameMaster() {

        listOfPlayers = new HashMap<>();
        startGame = Executors.newSingleThreadScheduledExecutor();
        votesCount = new HashMap<>();
        mafiosiNicks = new LinkedList<>();
        villagersNicks = new LinkedList<>();
    }

    private void broadcastToPlayers(String message) {

        for (String playerNick : listOfPlayers.keySet()) {
            Role role = listOfPlayers.get(playerNick).getRole();
            listOfPlayers.get(playerNick).sendMessage(message);

        }
    }

    private void setDayAndNight() {

        night = !night;
        broadcastToPlayers(EncodeDecode.NIGHT.encode(Boolean.toString(night)));
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

            if (votesCount.get(mostVotedPlayer) > votesCount.get(player)) {
                mostVotedPlayer = player;
            }
        }

        votesCount.clear();
        killPlayer(mostVotedPlayer);
    }

    public void receiveAndDecode(String message, String nickname) {

        EncodeDecode enumTag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));
        Server.PlayerHandler sender = listOfPlayers.get(nickname);

        switch (enumTag) {

            case MESSAGE:
                broadcastToPlayers(EncodeDecode.MESSAGE.decode(message));
                break;
            case NICK:
                sender.sendMessage(EncodeDecode.NICK.encode(nickname));
                break;
            case NICKOK:
                break;
            case TIMER:
                break;
            case NICKMESSAGE:
                break;
            case VOTE:
                addVote(EncodeDecode.VOTE.decode(message));
                break;
            case NIGHT:
                setDayAndNight();
                break;
            case NICKLIST:
                sender.sendMessage(EncodeDecode.NICKLIST.encode(getNickList()));
                break;
            case ROLE:
                sender.sendMessage(EncodeDecode.ROLE.encode(sender.getRole().name()));
                break;
            default:
                break;
        }
    }

    private String getNickList() {
        return String.join(" ", listOfPlayers.keySet());
    }

    private void killPlayer(String nickname) {

        listOfPlayers.get(nickname).sendMessage(EncodeDecode.KILL.encode(nickname));

        broadcastToPlayers(EncodeDecode.MESSAGE.encode("Player " + nickname + " was sentenced to death. The role was: "
                + listOfPlayers.get(nickname).getRole().toString()));

        kickPlayer(nickname);

        broadcastToPlayers(EncodeDecode.NICKLIST.encode(getNickList()));
    }

    public boolean addNick(String nick, Server.PlayerHandler playerHandler) {

        if (listOfPlayers.get(nick) != null) {
            return false;
        }

        listOfPlayers.put(nick, playerHandler);
        System.out.println("Player added");
        broadcastToPlayers(EncodeDecode.MESSAGE.encode(nick + " has entered to the game."));

        if (!gameHasStarted && listOfPlayers.size() >= MINPLAYERS) { // Se o jogo ainda não começou, reset ao timer
            if (schedule != null) {
                schedule.cancel(true);
            }
            schedule = startGame.schedule(this, TIMETOSTART, TimeUnit.SECONDS); //substituir this por uma runnable task
            broadcastToPlayers(EncodeDecode.TIMER.encode(Integer.toString(TIMETOSTART))); //Send boadcast to reset the timer
        }

        return true;
    }

    boolean kickPlayer(String nickname) {

        if (mafiosiNicks.contains(nickname)) {
            mafiosiNicks.remove(nickname);
        } else {
            villagersNicks.remove(nickname);
        }
        listOfPlayers.get(nickname).disconnectPlayer();
        return listOfPlayers.remove(nickname) != null;
    }

    @Override
    public void run() {

        System.out.println("Let the game Begin");
        gameHasStarted = true;
        broadcastToPlayers(EncodeDecode.START.encode("begin"));
        Role.setRolesToAllPlayers(listOfPlayers, mafiosiNicks, villagersNicks);
    }
}