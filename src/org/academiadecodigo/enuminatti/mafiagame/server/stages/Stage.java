package org.academiadecodigo.enuminatti.mafiagame.server.stages;

import java.util.Set;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public interface Stage {
    void startStage(Set<String> activePlayersOnStage, Set<String> possibleTargets);
}
