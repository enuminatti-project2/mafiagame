package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by Daniel Baeta on 11/11/17.
 *
 * A support utilitary class for JavaFX views' controllers to have messages decoded.
 */
class ControllerDecoder {

    //Make a decode(Controller controller, String message) to choose which method?


    /**
     * Decodes messages sent to a LoginController instance.
     * Will perform the changes on the UI's elements associated with the LoginController scene.
     *
     * @param loginController The instance of LoginController
     * @param message         The tagged message to be decoded
     */
    static void loginControllerDecoder(LoginController loginController, String message) {

        String tempTag = EncodeDecode.getStartTag(message);

        if (tempTag == null) {
            return;
        }

        EncodeDecode tag = EncodeDecode.getEnum(tempTag);

        if (tag == null) {
            // invalid tag
            return;
        }

        switch (tag) {
            case LOBBY:
                loginController.saveLists();
                loginController.setGone();
                SceneNavigator.getInstance().preLoadScreen("Lobby");
                SceneNavigator.getInstance().<LobbyController>getController("Lobby")
                        .setClient(loginController.getClient());
                SceneNavigator.getInstance().<LobbyController>getController("Lobby")
                        .writeNewLine(tag.decode(message));

                Platform.runLater(() -> SceneNavigator.getInstance().loadPreLoadedScreen());
                break;
            case NICKOK:
                Platform.runLater(loginController::nickInUse);
                break;
            case PWDERROR:
                Platform.runLater(loginController::wrongPWD);
                break;
            case HOSTSLIST:
                loginController.updateHostList(message);
            default:
        }
    }

    /**
     * Decodes messages sent to a ChatController instance.
     * Will perform the changes on the UI's elements associated with the ChatController scene.
     *
     * @param chatController The instance of ChatController
     * @param message        The tagged message to be decoded
     */

    static void chatControllerDecoder(ChatController chatController, String message) {

        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        // Ignore untagged messages
        if (tag == null) {
            return;
        }

        switch (tag) {

            case MESSAGE:
                chatController.writeNewLine(EncodeDecode.MESSAGE.decode(message), Color.BLACK);
                break;
            case KILL:
                chatController.killed();
            case NICKOK:
                break;
            case TIMER:
                chatController.writeNewLine(EncodeDecode.TIMER.decode(message), Color.CHOCOLATE);
                break;
            case NIGHT:
                chatController.setNight(EncodeDecode.NIGHT.decode(message));
                chatController.getVoteButton().setDisable(true);
                break;
            case NICKLIST:
                message = EncodeDecode.NICKLIST.decode(message);
                chatController.updateNickList(message);
                break;
            case NICK:
                chatController.writeNewLine("You are " + EncodeDecode.NICK.decode(message), Color.HOTPINK);
                break;
            case ROLE:
                chatController.writeNewLine(EncodeDecode.ROLE.decode(message), Color.ORANGERED);
                break;
            case OVER:
                chatController.backToLobby();
                break;
            case ALLOW_TALK:
                chatController.getSendButton().setDisable(false);
                break;
            case ALLOW_VOTE:
                chatController.getSendButton().setDisable(true);
                chatController.getVoteButton().setDisable(false);
                break;
            case SCORE:
                chatController.writeNewLine(EncodeDecode.SCORE.decode(message), Color.BLUEVIOLET);
                //Will receive a String "points rounds" to be split
                break;
        }
    }


    static void lobbyControllerDecoder(LobbyController lobbyController, String message) {

        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (tag == null) {
            return;
        }

        switch (tag) {
            case START:
                SceneNavigator.getInstance().preLoadScreen("ClientView");
                SceneNavigator.getInstance().<ChatController>getController("ClientView")
                        .setClient(lobbyController.getClient());

                Platform.runLater(() -> {
                    lobbyController.clearChat();
                    SceneNavigator.getInstance().loadPreLoadedScreen();
                });
                break;
            case TIMER:
                lobbyController.writeNewLine(EncodeDecode.TIMER.decode(message));
                break;
            case LOBBYMESSAGE:
                lobbyController.writeNewLine(EncodeDecode.LOBBYMESSAGE.decode(message));
                break;
            case LOBBYNICKLIST:
                message = EncodeDecode.LOBBYNICKLIST.decode(message);
                lobbyController.updateNickList(message);
                break;
            case SCORE:
                message = EncodeDecode.SCORE.decode(message);
                lobbyController.getStats(message);
                break;
        }
    }
}
