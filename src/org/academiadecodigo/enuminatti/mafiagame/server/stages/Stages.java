package org.academiadecodigo.enuminatti.mafiagame.server.stages;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public enum Stages {
    TALK,
    VOTE,
    GAMEOVERCHECK;

    public Stages getNextStage() {
        Stages next = TALK;
        switch (this) {
            case TALK:
                next = VOTE;
                break;
            case VOTE:
                next = GAMEOVERCHECK;
                break;
            case GAMEOVERCHECK:
                next = TALK;
                break;
            default:
                System.out.println("Invalid game state, returning default " + next);
        }
        return next;
    }


}
