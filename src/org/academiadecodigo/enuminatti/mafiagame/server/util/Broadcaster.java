package org.academiadecodigo.enuminatti.mafiagame.server.util;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;

import java.util.List;
import java.util.Map;

/**
 * Created by codecadet on 10/11/17.
 */
public class Broadcaster {

    public static void broadcastToPlayers(
            Map<String, Server.PlayerHandler> playerslist, String message) {

        for (Server.PlayerHandler players : playerslist.values()) {
            players.sendMessage(message);
        }

    }


    public static void broadcastToPlayers(
            Map<String, Server.PlayerHandler> playerslist, List<String> messageTargets, String message) {

        for (String nick : playerslist.keySet()){

            if(!messageTargets.contains(nick)){
                continue;
            }
            playerslist.get(nick).sendMessage(message);
        }

    }

}
