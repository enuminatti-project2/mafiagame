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
    private static final int MINPLAYERS = 0; //1 PLAYER

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

    public void receiveAndDecode(String message) {

        EncodeDecode enumTag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        switch (enumTag) {

            case MESSAGE:
                broadcastToPlayers(EncodeDecode.MESSAGE.decode(message));
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
                addVote(EncodeDecode.VOTE.decode(message));
                break;
            case NIGHT:
                setDayAndNight();
                break;
            case NICKLIST:
                sendNickList();
            default:
                break;
        }
    }

    private void sendNickList() {

        String nickList = String.join(" ", listOfPlayers.keySet());

        broadcastToPlayers(EncodeDecode.NICKLIST.encode(nickList));

    }

    private void killPlayer(String nickname) {

        listOfPlayers.get(nickname).sendMessage(EncodeDecode.KILL.encode(nickname));

        broadcastToPlayers(EncodeDecode.MESSAGE.encode("Player " + nickname + " was sentenced to death. The role was: "
                + listOfPlayers.get(nickname).getRole().toString()));

        kickPlayer(nickname);

        sendNickList();
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

        } else {
            broadcastToPlayers(EncodeDecode.START.encode("begin"));
            playerHandler.setRole(Role.setRoleToPlayer());
            broadcastToPlayers(playerHandler.getRole().name());
        }

        return true;
    }

    private void setRolesToPlayers() {

        for (String playerNickname : listOfPlayers.keySet()) {
            listOfPlayers.get(playerNickname).setRole(Role.setRoleToPlayer());

            if (listOfPlayers.get(playerNickname).getRole() == Role.MAFIA) {
                mafiosiNicks.add(playerNickname);
                continue;
            }
            villagersNicks.add(playerNickname);
        }
    }

    @Override
    public void run() {

        System.out.println("Let the game Begin");
        gameHasStarted = true;
        setRolesToPlayers();
        broadcastToPlayers(EncodeDecode.START.encode("begin"));
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
}