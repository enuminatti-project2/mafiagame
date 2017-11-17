package org.academiadecodigo.enuminatti.mafiagame.server.util;

import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.Collection;
import java.util.Map;

/**
 * MIT License
 * (c) 2017 Rodrigo Sanches, Ricardo Constantino
 */

public class Broadcaster {

    /**
     * Broadcast encoded message to all in playersList.
     *
     * @param playersList players to send message to
     * @param encoding    to encode message with
     * @param message     to send
     */
    public static void broadcastToPlayers(Map<String, Player> playersList,
                                          EncodeDecode encoding, String message) {

        broadcastToPlayers(playersList, playersList.keySet(), encoding, message);

    }

    /**
     * Broadcast encoded message to only the nicks in messageTargets.
     *
     * @param playersList    players which can receive messages
     * @param messageTargets exclusive recipients of the message
     * @param encoding       to encode message with
     * @param message        to send to specific targets
     */
    public static void broadcastToPlayers(Map<String, Player> playersList,
                                          Collection<String> messageTargets,
                                          EncodeDecode encoding, String message) {

        if (encoding == null || playersList == null) {
            return;
        }

        message = sanitizeMessage(message);
        message = encoding.encode(message);

        for (String nick : playersList.keySet()) {

            if (!messageTargets.contains(nick)) {
                continue;
            }
            playersList.get(nick).writeToPlayer(message);
        }

    }


    public static  void broadcastToPlayer(Player player,
                                          EncodeDecode encoding , String message){

        if (encoding == null || player == null) {
            return;
        }

        message = sanitizeMessage(message);
        message = encoding.encode(message);

        player.writeToPlayer(message);
    }

    /**
     * Sanitizes the message before encoding and broadcasting.
     *
     * If we never use tags within tags this could escape <> characters
     * and the client would un-escape them after decoding for proper display
     * to the player.
     *
     * @param message to sanitize, if null will return an empty string
     * @return sanitized message
     */
    private static String sanitizeMessage(String message) {
        // Allow sending empty messages with just the wrapper tags
        if (message == null) {
            return "";
        }

        // replace repeated whitespace with just one space
        message = message.replaceAll("\\s+", " ");

        // censorMessage();

        return message;
    }


}
