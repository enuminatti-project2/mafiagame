package org.academiadecodigo.enuminatti.mafiagame.server;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public enum Role {

    MAFIA,
    VILLAGER;

    private static int playerRolesAssigned=0;
    private static final int MAFIA_RATIO = 3;

    public static Role setRoleToPlayer(){

        playerRolesAssigned++;

        if (playerRolesAssigned % MAFIA_RATIO == 0){
            return MAFIA;
        }
        return VILLAGER;
    }
}
