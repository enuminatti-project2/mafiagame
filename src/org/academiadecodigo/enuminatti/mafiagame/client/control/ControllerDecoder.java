package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by Daniel Baeta on 11/11/17.
 */

/**
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

        System.out.println(message + " on Login controller");

        String tempTag = EncodeDecode.getStartTag(message);

        if (tempTag == null) {
            System.out.println("invalid message: " + message);
            return;
        }

        EncodeDecode tag = EncodeDecode.getEnum(tempTag);

        switch (tag) {
            case LOBBY:
                loginController.saveLists();
                Platform.runLater(() -> {
                    SceneNavigator.getInstance().loadScreen("Lobby");
                    SceneNavigator.getInstance().<LobbyController>getController("Lobby")
                            .setClient(loginController.getClient());
                });
                break;
            case NICKOK:
                loginController.nickInUse();
                break;
            case PWDERROR:
                loginController.wrongPWD();
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
        System.out.println(message + " on chat");

        if (tag == null) {

            chatController.writeNewLine(message, Color.DEEPPINK);
            System.out.println(message);
            return;
        }

        switch (tag) {

            case MESSAGE:
                chatController.writeNewLine(EncodeDecode.MESSAGE.decode(message), Color.BLACK);
                break;
            case KILL:
                if (!chatController.isNight()) {
                    chatController.getEndImage().setImage(new Image(Constants.ROPE_IMAGE_PATH));
                    chatController.getEndImage().setFitWidth(240.0);
                    chatController.getEndImage().setFitHeight(400.0);
                    chatController.getEndImage().setY(-100.0);
                }
                chatController.getEndImage().setVisible(true);
                chatController.getGunShotSound().play(true);
                chatController.getSendButton().setDisable(false);
                Platform.runLater(() -> chatController.getSendButton().setText("Back"));
            case NICKOK:
                break;
            case TIMER:
                chatController.writeNewLine("Timer message: " + EncodeDecode.TIMER.decode(message), Color.CHOCOLATE);
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
                Platform.runLater(() -> SceneNavigator.getInstance().back());
                break;
            case ALLOW_TALK:
                chatController.getSendButton().setDisable(false);
                System.out.println("Enabling send button");
                break;
            case ALLOW_VOTE:
                chatController.getSendButton().setDisable(true);
                chatController.getVoteButton().setDisable(false);
                break;
            case SCORE:
                chatController.writeNewLine(EncodeDecode.SCORE.decode(message), Color.BLUEVIOLET);
                //Will receive a String "points rounds" to be split
                break;
            default:
                chatController.writeNewLine(message, Color.BLUE);
                System.out.println("Deu merda");
        }
    }


    static void lobbyControllerDecoder(LobbyController lobbyController, String message) {

        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (tag == null) {
            System.out.println(message);
            return;
        }

        switch (tag) {
            case START:
                Platform.runLater(() -> {
                    SceneNavigator.getInstance().loadScreen("ClientView");
                    SceneNavigator.getInstance().<ChatController>getController("ClientView")
                            .setClient(lobbyController.getClient());
                });
            case LOBBYMESSAGE:
                lobbyController.writeNewLine(EncodeDecode.LOBBYMESSAGE.decode(message));
                break;
            case NICK:
                System.out.println("Message" + message);
                lobbyController.updateStats(EncodeDecode.NICK.decode(message));
                break;
            case LOBBYNICKLIST:
                message = EncodeDecode.LOBBYNICKLIST.decode(message);
                lobbyController.updateNickList(message);
                break;
            case TIMER:
                lobbyController.writeNewLine(message);
                break;

        }
    }
}
