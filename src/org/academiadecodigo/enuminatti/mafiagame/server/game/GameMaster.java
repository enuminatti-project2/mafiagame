package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.server.stages.*;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameMaster {

    private static final int MIN_PLAYERS = 1; //1 PLAYER

    private static final int SECONDS_TO_START_GAME = 10;
    private static final int SECONDS_TO_TALK = 60;
    private static final int SECONDS_TO_VOTE = 30;

    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;
    private List<String> thirdPartyNicks;

    private boolean gameHasStarted;
    private boolean night;
    private Stages currentGameStage;

    private Map<String, Integer> votesCount;
    private int numberOfVotes;

    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;

    Stage talkStage;
    Stage voteStage;
    Stage gameOverStage;

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
    public void toggleDayAndNight() {

        night = !night;
        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NIGHT, Boolean.toString(night));
    }

    /**
     * This adds a vote to votesCount for the player specified in nickname parameter.
     *
     * If the nickname isn't a valid player, it doesn't do anything.
     *
     * @param nickname player to add a vote to
     */
    void addVote(String nickname) {

        if (nickname == null || !listOfPlayers.containsKey(nickname)) {
            return;
        }

        numberOfVotes++;
        votesCount.merge(nickname, 1, Integer::sum);

        System.out.println("Votes N = " + numberOfVotes + " List of Players: " + listOfPlayers.size());

        if (numberOfVotes >= listOfPlayers.size()) {
            calculateVotes(Collections.max(votesCount.values()));
            toggleDayAndNight();
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

        for (String player : votesCount.keySet()) {

            if (votesCount.get(player).equals(mostVotes)) {

                votesCount.clear();
                numberOfVotes = 0;
                killPlayer(player);

                gameover();

                break;
            }
        }
    }

    public void receiveMessage(String message, String nickname) {


        GameMasterDecoder.gameMasterDecoder(this, message, nickname);
    }

    String getNickList() {
        return String.join(" ", listOfPlayers.keySet());
    }

    public void killPlayer(String nickname) {

        listOfPlayers.get(nickname).sendMessage(EncodeDecode.KILL.encode(nickname));

        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.MESSAGE,
                String.format("Player %s was sentenced to death. The role was: %s",
                        nickname, listOfPlayers.get(nickname).getRole()));
        kickPlayer(nickname);
    }

    public boolean addNick(String nick, Server.PlayerHandler playerHandler) {

        if (listOfPlayers.get(nick) != null) {
            return false;
        }

        listOfPlayers.put(nick, playerHandler);
        System.out.println("Player added");
        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.MESSAGE, nick + " has entered the game.");

        if (!gameHasStarted && listOfPlayers.size() >= MIN_PLAYERS) { // Se o jogo ainda não começou, reset ao timer
            if (schedule != null) {
                schedule.cancel(true);
            }
            schedule = startGame.schedule(this::startGame, SECONDS_TO_START_GAME, TimeUnit.SECONDS); //substituir this por uma runnable task
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.TIMER, Integer.toString(SECONDS_TO_START_GAME)); //Send boadcast to reset the timer
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
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NICKLIST, getNickList());

        }

    }

    private void gameover() {

        String message = null;

        if (mafiosiNicks.size() == 0) {
            message = "The villagers won, all the mobsters are dead!";
        }

        if (villagersNicks.size() < mafiosiNicks.size()) {
            message = "The mobsters won!";
        }

        if (message != null) {
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.MESSAGE, message);
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.OVER, "GAME OVER");
        }
    }


    private void startGame() {
        System.out.println("Let the game Begin");
        gameHasStarted = true;
        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.START, "begin");
        Role.setRolesToAllPlayers(listOfPlayers, mafiosiNicks, villagersNicks);

        // Stage initiation
        talkStage = new Talk(this);
        voteStage = new Vote(this);
        gameOverStage = new GameOverCheck(this);
    }


    public Map<String, Server.PlayerHandler> getListOfPlayers() {
        return listOfPlayers;
    }

    private void startCurrentStage() {
        Stage currentStage = null;
        switch (currentGameStage) {
            case TALK:
                currentStage = talkStage;
                break;
            case VOTE:
                currentStage = voteStage;
                break;
            case GAMEOVERCHECK:
                currentStage = gameOverStage;
                break;
        }

        if (currentStage != null) {

            // pass active users on current stage <- relevant for Vote and Talk
            // and possible targets on current stage <- relevant for Vote
            currentStage.runStage(getActiveNicks(), listOfPlayers.keySet());
            return;
        }

        System.out.println("Something went wrong, we're at null currentGameStage.");
    }

    public void changeStage(Stages nextStage) {
        Stages newGameStage = currentGameStage.getNextStage();
        System.out.printf("Changing stage from %s to %s.\n", currentGameStage, newGameStage);
        currentGameStage = newGameStage;
        startCurrentStage();
    }

    public List<String> getThirdParties() {
        return thirdPartyNicks;
    }

    public List<String> getVillagers() {
        return villagersNicks;
    }

    public List<String> getMafias() {
        return mafiosiNicks;
    }

    public Set<String> getActiveNicks() {
        if (night) {
            return listOfPlayers.keySet().stream().filter(mafiosiNicks::contains).collect(Collectors.toSet());
        }
        return listOfPlayers.keySet();
    }
}