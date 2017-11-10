package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
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

    private static final int TIMETOSTART = 10;
    private static final int MINPLAYERS = 4; //1 PLAYER

    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;

    private boolean gameHasStarted;
    private boolean night;

    private Map<String, Integer> votesCount;
    private int numberOfVotes;

    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;

    public GameMaster() {

        listOfPlayers = new HashMap<>();
        startGame = Executors.newSingleThreadScheduledExecutor();
        votesCount = new HashMap<>();
        mafiosiNicks = new LinkedList<>();
        villagersNicks = new LinkedList<>();
    }

    /**
     * Toggle day and night, when called they switch...
     */
    private void toggleDayAndNight() {

        night = !night;
        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NIGHT.encode(Boolean.toString(night)));
    }

    /**
     * This adds a vote to votesCount for the player specified in nickname parameter.
     *
     * If the nickname isn't a valid player, it doesn't do anything.
     *
     * @param nickname player to add a vote to
     */
    private void addVote(String nickname) {

        if(nickname == null || !listOfPlayers.containsKey(nickname)) {
            return;
        }

        numberOfVotes++;
        votesCount.merge(nickname, 1, Integer::sum);

        System.out.println("Votes N = " + numberOfVotes + " List of Players: " + listOfPlayers.size());

        if (numberOfVotes >= listOfPlayers.size()) {
            calculateVotes(Collections.max(votesCount.values()));
        }

    }


    /**
     * This grabs the votesCount map and finds the player which
     * had the same votes as mostVotes and kills them.
     *
     * In case of a draw, this will grab the first player.
     *
     * @param mostVotes the most votes that are in the map
     */
    private void calculateVotes(Integer mostVotes) {

        for (String player: votesCount.keySet()) {

            if(votesCount.get(player).equals(mostVotes)){

                votesCount.clear();
                numberOfVotes = 0;
                killPlayer(player);

                break;
            }
        }

    }

    public void receiveAndDecode(String message, String nickname) {

        EncodeDecode enumTag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));
        Server.PlayerHandler sender = listOfPlayers.get(nickname);

        switch (enumTag) {

            case MESSAGE:
                Broadcaster.broadcastToPlayers(listOfPlayers,
                        String.format("<%s> %s", nickname,
                        EncodeDecode.MESSAGE.decode(message)));
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

        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.MESSAGE.encode("Player " + nickname + " was sentenced to death. The role was: "
                + listOfPlayers.get(nickname).getRole().toString()));
        kickPlayer(nickname);
    }

    public boolean addNick(String nick, Server.PlayerHandler playerHandler) {

        if (listOfPlayers.get(nick) != null) {
            return false;
        }

        listOfPlayers.put(nick, playerHandler);
        System.out.println("Player added");
        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.MESSAGE.encode(nick + " has entered the game."));

        if (!gameHasStarted && listOfPlayers.size() >= MINPLAYERS) { // Se o jogo ainda não começou, reset ao timer
            if (schedule != null) {
                schedule.cancel(true);
            }
            schedule = startGame.schedule(this, TIMETOSTART, TimeUnit.SECONDS); //substituir this por uma runnable task
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.TIMER.encode(Integer.toString(TIMETOSTART))); //Send boadcast to reset the timer
        }
        return true;
    }

    /**
     * Remove the player with this nickname from all lists and maps
     * and disconnect them.
     *
     * At the end, it updates everyone's nicklist to reflect this change.
     *
     * @param nickname player to be kicked from the game
     */
    public void kickPlayer(String nickname) {

        if (nickname == null) {
            return;
        }

        mafiosiNicks.remove(nickname);
        villagersNicks.remove(nickname);

        Server.PlayerHandler playerRemoved = listOfPlayers.remove(nickname);

        if (playerRemoved != null) {
            playerRemoved.disconnectPlayer();
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NICKLIST.encode(getNickList()));

        }

    }

    @Override
    public void run() {

        System.out.println("Let the game Begin");
        gameHasStarted = true;
        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.START.encode("begin"));
        Role.setRolesToAllPlayers(listOfPlayers, mafiosiNicks, villagersNicks);
    }
}