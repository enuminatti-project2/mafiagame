package org.academiadecodigo.enuminatti.mafiagame.server.game;

import org.academiadecodigo.enuminatti.mafiagame.server.Server;
import org.academiadecodigo.enuminatti.mafiagame.server.player.Player;
import org.academiadecodigo.enuminatti.mafiagame.server.util.Broadcaster;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by Daniel Baeta on 11/11/17.
 *
 * A support utilitary class for GameMaster to have messages decoded.
 */

public class GameMasterDecoder {

    /**
     * Decodes messages for GameMaster from a user, in order to perform the necessary operations.
     *
     * @param gameMaster The instance of GameMaster
     * @param message    The message to be decoded
     * @param nickname   The sender's nickname
     */
    static void gameMasterDecoder(GameMaster gameMaster, String message, String nickname) {

        EncodeDecode enumTag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (enumTag == null) {
            return;
        }
        Player sender = gameMaster.getListOfPlayers().get(nickname);

        switch (enumTag) {
            //Implement EncodeDecode.SERVER to be sent in a different color
            case MESSAGE:
                String messageDecoded = EncodeDecode.MESSAGE.decode(message);
                Broadcaster.broadcastToPlayers(gameMaster.getListOfPlayers(), EncodeDecode.MESSAGE,
                        String.format("<%s> %s", nickname, messageDecoded));
                break;
            case NICK:
                System.out.println("Sender is asking to change nick to: " + EncodeDecode.NICK.decode(message));
                String newNickname = EncodeDecode.NICK.decode(message);

                if (gameMaster.getListOfPlayers().containsKey(newNickname)) {
                    sender.writeToPlayer(EncodeDecode.MESSAGE.encode("The name you chose is already in use"));
                    return;
                }
                sender.setName(newNickname);
                gameMaster.getListOfPlayers().put(newNickname, sender);
                gameMaster.getListOfPlayers().remove(nickname);
                break;
            case NICKOK:
                break;
            case TIMER:
                break;
            case VOTE:
                gameMaster.addVote(EncodeDecode.VOTE.decode(message));
                break;
            case NICKLIST:
                System.out.println("Asking for a nicklist. The current list is: " + gameMaster.getNickList());
                sender.writeToPlayer(EncodeDecode.NICKLIST.encode(gameMaster.getNickList()));
                break;
            case ROLE:
                String msg = String.format("%s, and you're assigned to %s",
                        sender.getName(), sender.getRole());
                sender.writeToPlayer(EncodeDecode.ROLE.encode(msg));
                break;
            default:
                break;
        }
    }
}
