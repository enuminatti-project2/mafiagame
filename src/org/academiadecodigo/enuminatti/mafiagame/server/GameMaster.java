package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.util.*;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class GameMaster {
    private Map<String, Server.PlayerHandler> listOfPlayers;
    private List<String> mafiosiNicks;
    private List<String> villagersNicks;
    private Map<String, Integer> votesCount;

    public GameMaster() {
        listOfPlayers = new HashMap<>();
        votesCount = new HashMap<>();
    }

    private void broadcastToPlayers(String type, String message) {
        for (String playerNick : listOfPlayers.keySet()) {
            listOfPlayers.get(playerNick).sendMessage(message);
        }
    }

    private void setDayAndNight() {
    }

    private void setPlayerRole() {

    }

    private void addVote(String nickname) {

        if (votesCount.size() != listOfPlayers.size()) {
            if (votesCount.containsKey(nickname)) {
                votesCount.replace(nickname, votesCount.get(nickname) + 1);
                return;
            }
            votesCount.put(nickname, 1);
        } else {
            calculateVotes();
        }
    }

    private void calculateVotes() {

        String mostVotedPlayer = mafiosiNicks.get(0);

        Set<String> votedPlayers = votesCount.keySet();

        for (String player : votedPlayers) {

            if (votesCount.get(mostVotedPlayer) > votesCount.get(player)){
                mostVotedPlayer = player;
            }
        }
        votesCount.clear();
        killPlayer(mostVotedPlayer);

    }

    public void receiveAndDecode(String message) {
        // Map<String, Integer> votes = new HashMap<>();

        String tag = EncodeDecode.getStartTag(message);

        switch (tag) {
            case "<VOTE>":
                addVote(EncodeDecode.VOTE.decode(message));
                break;
            case "<MSG>":
                broadcastToPlayers(tag, EncodeDecode.MESSAGE.decode(message));
                break;
        }
    }

    private void sendNickList() {

        //send list of players
    }

    private void killPlayer(String nickname){

        broadcastToPlayers("cenas", "Player " + nickname + " was sentenced to death. The role was: "
                            + listOfPlayers.get(nickname).getRole().toString());
        listOfPlayers.remove(nickname);
        sendNickList();
    }


}
