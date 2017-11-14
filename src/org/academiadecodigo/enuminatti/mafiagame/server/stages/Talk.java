package org.academiadecodigo.enuminatti.mafiagame.server.stages;

import org.academiadecodigo.enuminatti.mafiagame.server.game.GameMaster;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class Talk implements Stage {

    private GameMaster gameMaster;

    private Set<String> talkers;

    private ScheduledExecutorService talkRunner;
    private ScheduledFuture<?> talkTimer;

    public Talk(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }


    @Override
    public void runStage(Set<String> activePlayersOnStage, Set<String> possibleTargets) {
        this.talkers = activePlayersOnStage;
        Broadcaster.broadcastToPlayers(gameMaster.getListOfPlayers(), talkers,
                EncodeDecode.ALLOW_TALK, "vote");
        startTimer();
    }

    private void startTimer() {
        if (talkRunner == null) {
            talkRunner = Executors.newSingleThreadScheduledExecutor();
        }

        if (talkTimer != null) {
            talkTimer.cancel(true);
        }

        talkTimer = talkRunner.schedule(this::endTimer,
                Constants.SECONDS_TO_TALK, TimeUnit.SECONDS);

        Broadcaster.broadcastToPlayers(gameMaster.getListOfPlayers(), talkers,
                EncodeDecode.TIMER, Integer.toString(Constants.SECONDS_TO_TALK));
    }

    private void endTimer() {

        talkTimer.cancel(true);
        goNext();

    }

    private void goNext() {
        gameMaster.changeStage(Stages.VOTE);
    }

    @Override
    public void cleanup() {
        if (talkRunner != null) {
            talkRunner.shutdown();
        }
    }
}
