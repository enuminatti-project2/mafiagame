package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.persistence.ConnectionManager;
import org.academiadecodigo.enuminatti.mafiagame.server.persistence.JdbcScore;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by Daniel Baeta on 11/11/17.
 *
 * A support utilitary class for GameMaster to have messages decoded.
 */

class GameMasterDecoder {

    /**
     * Decodes messages for GameMaster from a user, in order to perform the necessary operations.
     *
     * @param gameMaster The instance of GameMaster
     * @param message    The message to be decoded
     * @param nickname   The sender's nickname
     */
    static void gameMasterDecoder(GameMaster gameMaster, String message, String nickname) {

        EncodeDecode enumTag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));
        JdbcScore jdbcScore = new JdbcScore(ConnectionManager.getConnection());

        Player sender;
        String messageDecoded;

        if (enumTag == null) {
            return;
        }
        sender = gameMaster.getListOfPlayers().get(nickname);

        switch (enumTag) {
            //Implement EncodeDecode.SERVER to be sent in a different color
            case MESSAGE:
                messageDecoded = EncodeDecode.MESSAGE.decode(message);
                Broadcaster.broadcastToPlayers(gameMaster.getListOfPlayers(), gameMaster.getActiveNicks(),
                        EncodeDecode.MESSAGE, String.format("<%s> %s", nickname, messageDecoded));
                break;
            case LOBBYMESSAGE:
                messageDecoded = EncodeDecode.LOBBYMESSAGE.decode(message);
                Broadcaster.broadcastToPlayers(gameMaster.getListOfLobby(), EncodeDecode.LOBBYMESSAGE,
                        String.format("<%s> %s", nickname, messageDecoded));
                break;
            case NICK:
                sender.writeToPlayer(EncodeDecode.NICK.encode(sender.getName()));
                break;
            case NICKOK:
                break;
            case TIMER:
                break;
            case VOTE:
                gameMaster.addVote(EncodeDecode.VOTE.decode(message));
                break;
            case NICKLIST:
                sender.writeToPlayer(EncodeDecode.NICKLIST.encode(gameMaster.getNickList()));
                break;
            case LOBBYNICKLIST:
                sender = gameMaster.getListOfLobby().get(nickname);
                sender.writeToPlayer(EncodeDecode.LOBBYNICKLIST.encode(gameMaster.getNickListOfLobby()));
                break;
            case ROLE:
                String msg = String.format("%s, and you're assigned to %s.",
                        sender.getName(), sender.getRole());
                sender.writeToPlayer(EncodeDecode.ROLE.encode(msg));
                sender.writeToPlayer(EncodeDecode.ROLE.encode(sender.getDescriptionMessage()));
                break;
            case SCORE:
                sender = gameMaster.getListOfLobby().get(nickname);
                sender.writeToPlayer(EncodeDecode.SCORE.encode(jdbcScore.getPoints(sender.getName())));
                break;
            default:
                break;
        }
    }
}
