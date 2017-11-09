package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

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

public class GameMaster implements Runnable {

    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;

    private boolean gameHasStarted;
    private boolean night;
    private Map<String, Integer> votesCount;

    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;
    private static final int TIMETOSTART = 10;

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

        }else {
            broadcastToPlayers(EncodeDecode.START.encode("begin"));

        }

        return true;
    }

    private void setRolesToPlayers() {

        Set<String> playerHandlerSet = listOfPlayers.keySet();

        for (String playerNick : playerHandlerSet) {

            Server.PlayerHandler player = listOfPlayers.get(playerNick);
            player.setRole(Role.setRoleToPlayer());
            listOfPlayers.replace(playerNick, player);

            if (player.getRole() == Role.MAFIA) {
                mafiosiNicks.add(playerNick);
                continue;
            }
            villagersNicks.add(playerNick);
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

        if (mafiosiNicks.contains(nickname)){
            mafiosiNicks.remove(nickname);
        } else {
            villagersNicks.remove(nickname);
        }

        return listOfPlayers.remove(nickname) != null;
    }
}