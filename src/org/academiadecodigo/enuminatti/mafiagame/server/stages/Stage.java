package org.academiadecodigo.enuminatti.mafiagame.server.stages;

import java.util.Set;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public interface Stage {

    /**
     * Start the stage and pass the necessary information on active actors.
     *
     * @param activePlayersOnStage player nicks that can participate in this stage
     * @param possibleTargets player nicks that can be targeted in this stage
     */
    void runStage(Set<String> activePlayersOnStage, Set<String> possibleTargets);

    /**
     * Do whatever cleanups are needed for a graceful shutdown of the stage.
     */
    void cleanup();
}
