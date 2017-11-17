package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.stages.*;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
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


    private Map<String, Player> listOfPlayers;
    private Map<String, Player> listOfLobby;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;
    private List<String> thirdPartyNicks;

    private boolean gameHasStarted;
    private boolean night;
    private Stages currentGameStage;

    private ScheduledExecutorService startGame;
    private ScheduledFuture<?> schedule;

    private Stage talkStage;
    private Stage voteStage;
    private Stage gameOverStage;

    public GameMaster() {

        listOfPlayers = new HashMap<>();
        listOfLobby = new HashMap<>();
        startGame = Executors.newSingleThreadScheduledExecutor();
        mafiosiNicks = new LinkedList<>();
        villagersNicks = new LinkedList<>();
        thirdPartyNicks = new LinkedList<>();
    }

    /**
     * Toggle day and night, when called they switch...
     */
    public void toggleDayAndNight() {

        night = !night;

        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NIGHT, Boolean.toString(night));

        for (Player player : listOfPlayers.values()) {
            if (night) {
                player.doNightStrategy();
                continue;
            }
            player.doDayStrategy();
        }
        ScoreCalculator.nextRound();

    }

    /**
     * This adds a vote to votesCount for the player specified in nickname parameter.
     *
     * If the nickname isn't a valid player, it doesn't do anything.
     *
     * @param nickname player to add a vote to
     */
    void addVote(String nickname) {

        if (currentGameStage == Stages.VOTE) {
            ((Vote) voteStage).addVote(nickname);
        }

    }

    public void receiveMessage(String message, String nickname) {

        GameMasterDecoder.gameMasterDecoder(this, message, nickname);
    }

    String getNickList() {
        return String.join(" ", listOfPlayers.keySet());
    }

    public void killPlayer(String nickname) {

        ScoreCalculator.calculate(nickname, listOfPlayers.get(nickname));
        listOfPlayers.get(nickname).writeToPlayer(EncodeDecode.KILL.encode(nickname));

        Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.MESSAGE,
                String.format("Player %s was sentenced to death. %s",
                        nickname, listOfPlayers.get(nickname).getStrategyMessage()));
        kickPlayer(nickname);
    }

    public boolean addNick(String nick, Server.ServerWorker serverWorker) {


        if (listOfLobby.get(nick) != null) {

            return false;
        }

        Player newPlayer = new Player(serverWorker, nick, this);
        listOfLobby.put(nick, newPlayer);
        Broadcaster.broadcastToPlayer(newPlayer, EncodeDecode.LOBBY,
                "Welcome, " + nick + ". You have successfully logged in!");

        Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.LOBBYNICKLIST, getNickListOfLobby());

        canGameStart();

        return true;
    }

    private void canGameStart() {


        if (!gameHasStarted && listOfLobby.size() >= Constants.MIN_PLAYERS) {

            // Se o jogo ainda não começou, reset ao timer
            if (schedule != null) {
                schedule.cancel(true);
            }
            schedule = startGame.schedule(this::startGame,
                    Constants.SECONDS_TO_START_GAME, TimeUnit.SECONDS);

            // Send broadcast to reset the timer
            Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.TIMER,
                    String.format("Game will start in %d seconds.", Constants.SECONDS_TO_START_GAME));
        }
    }

    /**
     * Remove the player with this nickname from all lists and maps.
     *
     * At the end, it updates everyone's nicklist to reflect this change.
     *
     * @param nickname player to be kicked from the game
     */

    private void kickPlayer(String nickname) {

        if (nickname == null) {
            return;
        }

        mafiosiNicks.remove(nickname);
        villagersNicks.remove(nickname);
        thirdPartyNicks.remove(nickname);

        Player playerRemoved = listOfPlayers.remove(nickname);

        if (playerRemoved != null) {
            listOfLobby.put(nickname, playerRemoved);
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NICKLIST, getNickList());
            Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.LOBBYNICKLIST, getNickListOfLobby());
        }

    }

    public void kickPlayerFromLobby(String nickname) {

        if (nickname == null) {
            return;
        }

        Player playerRemoved = listOfLobby.remove(nickname);

        if (playerRemoved != null) {

            playerRemoved.disconnect();
            Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.LOBBYNICKLIST, getNickListOfLobby());

        }

    }

    public void removePlayerFromLists(Player player) {
        String nickname = player.getName();

        mafiosiNicks.remove(nickname);
        villagersNicks.remove(nickname);
        thirdPartyNicks.remove(nickname);

        Player playerRemovedFromGame = listOfPlayers.remove(nickname);
        Player playerRemovedFromLobby = listOfLobby.remove(nickname);

        if (playerRemovedFromGame != null || playerRemovedFromLobby != null) {
            Broadcaster.broadcastToPlayers(listOfPlayers, EncodeDecode.NICKLIST, getNickList());
            Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.LOBBYNICKLIST, getNickListOfLobby());
        }

    }

    private void startGame() {

        if (listOfLobby.size() < Constants.MIN_PLAYERS) {
            Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.MESSAGE,
                    "Not enough players to start the game");
            return;
        }

        gameHasStarted = true;
        Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.START, "begin");
        listOfPlayers.putAll(listOfLobby);
        listOfLobby.clear();
        RoleFactory.setRolesToAllPlayers(listOfPlayers, mafiosiNicks, villagersNicks);

        // Stage initiation
        talkStage = new Talk(this);
        voteStage = new Vote(this);
        gameOverStage = new GameOverCheck(this);

        currentGameStage = Stages.TALK;
        startCurrentStage();
    }


    public Map<String, Player> getListOfPlayers() {
        return listOfPlayers;
    }

    public Map<String, Player> getListOfLobby() {
        return listOfLobby;
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
        System.out.printf("Changing stage from %s to %s.\n", currentGameStage, nextStage);
        currentGameStage = nextStage;
        startCurrentStage();
    }

    public List<String> getMafiosiNicks() {
        return mafiosiNicks;
    }

    public List<String> getVillagersNicks() {
        return villagersNicks;
    }

    public List<String> getThirdParties() {
        return thirdPartyNicks;
    }

    String getNickListOfLobby() {
        return String.join(" ", listOfLobby.keySet());
    }

    Set<String> getActiveNicks() {
        if (night) {
            return listOfPlayers.keySet().stream().filter(mafiosiNicks::contains).collect(Collectors.toSet());
        }
        return listOfPlayers.keySet();
    }

    public void gameOver() {

        ScoreCalculator.calculateEnd(getListOfPlayers());
        Broadcaster.broadcastToPlayers(getListOfPlayers(),
                EncodeDecode.OVER, "GAME OVER");

        // cleanup roles/strategies from players
        for (Player player : getListOfPlayers().values()) {
            player.endGameAction();
        }

        listOfLobby.putAll(listOfPlayers);
        Broadcaster.broadcastToPlayers(listOfLobby, EncodeDecode.LOBBYNICKLIST, getNickListOfLobby());
        listOfPlayers.clear();
        gameHasStarted = false;
        ScoreCalculator.resetCalculator();
        //canGameStart();

        schedule = startGame.schedule(this::canGameStart,
                Constants.SECONDS_ENDGAME, TimeUnit.SECONDS);
    }
}