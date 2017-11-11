package org.academiadecodigo.enuminatti.mafiagame.server.stages;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
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

public class Vote implements Stage {

    private final int SECONDS_TO_VOTE = 60;

    private GameMaster game;

    private Set<String> voters;
    private Map<String, Integer> voted;
    private int votesCounted;

    private ScheduledExecutorService calculateVotesRunner;
    private ScheduledFuture<?> calculateVotesTimer;

    public Vote(GameMaster game) {
        this.game = game;
    }

    public void startStage(Set<String> voters, Set<String> targets) {
        this.voted = new HashMap<>();
        for (String target : targets) {
            voted.put(target, 0);
        }

        this.voters = voters;
        votesCounted = 0;

        startTimer();
    }

    /**
     * Starts or restarts a vote timer and announces to the voters.
     */
    public void startTimer() {
        if (calculateVotesRunner == null) {
            calculateVotesRunner = Executors.newSingleThreadScheduledExecutor();
        }

        if (calculateVotesTimer != null) {
            calculateVotesTimer.cancel(true);
        }

        calculateVotesTimer = calculateVotesRunner.schedule(this::endTimer, SECONDS_TO_VOTE, TimeUnit.SECONDS);

        Broadcaster.broadcastToPlayers(game.getListOfPlayers(), voters,
                EncodeDecode.TIMER, Integer.toString(SECONDS_TO_VOTE));
    }

    /**
     * Adds a vote for the specified nickname.
     *
     * @param nickname to vote for
     * @return true if the vote was valid
     */
    public boolean addVote(String nickname) {
        if (nickname == null || !voted.containsKey(nickname)) {
            System.out.println("addVote: Invalid vote received for: " + nickname);
            return false;
        }

        voted.merge(nickname, 1, Integer::sum);
        votesCounted++;

        // if this is the last expected vote, calculate the votes earlier
        if (votesCounted >= voted.size()) {
            endTimer();
        }

        return true;
    }

    /**
     * Calculate votes and return the nickname which received the most votes.
     *
     * If no votes have been counted, returns null. Alternatively, a player could be
     * selected at random to be killed.
     *
     * Pitfalls: if there's more than one player with the same number of most votes,
     * it'll return the first one it finds with that number of votes.
     */
    private String calculateVotes() {
        if (votesCounted == 0) {
            return null;
        }

        int mostVotes = Collections.max(voted.values());

        for (String player: voted.keySet()) {

            if(voted.get(player).equals(mostVotes)) {

                return player;
            }
        }
        return null;
    }

    private void endTimer() {
        String playerToKill = calculateVotes();

        if (playerToKill != null) {
            calculateVotesTimer.cancel(true);
            calculateVotesRunner.shutdown();

            game.killPlayer(playerToKill);
            game.changeStage();
            return;
        }

        // if no one voted
        startTimer();
    }

}
