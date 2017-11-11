package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by Daniel Baeta on 11/11/17.
 */

/**
 * A support utilitary class for JavaFX views' controllers to have messages decoded.
 */
class ControllerDecoder {

    //Make a decode(Controller controller) to choose which method?


    /**
     * Decodes messages sent to a LoginController instance.
     * Will perform the changes on the UI's elements associated with the LoginController scene.
     * @param loginController The instance of LoginController
     * @param message The tagged message to be decoded
     */
    static void loginControllerDecoder(LoginController loginController, String message){

        System.out.println(message + " on Login controller");

        String tempTag = EncodeDecode.getStartTag(message);

        if (tempTag == null) {
            System.out.println("invalid message: " + message);
            return;
        }

        EncodeDecode tag = EncodeDecode.getEnum(tempTag);

        switch (tag) {
            case START:
                Platform.runLater(() -> {
                    SceneNavigator.getInstance().loadScreen("ClientView");
                    SceneNavigator.getInstance().<ChatController>getController("ClientView")
                            .setClient(loginController.getClient());
                });
                break;
            case TIMER:
                loginController.getServerMessageArea().appendText("Game will start in " + tag.decode(message) + " seconds.\n");
                break;
            default:
                loginController.getServerMessageArea().appendText(tag.decode(message) + "\n");
        }
    }

    /**
     * Decodes messages sent to a ChatController instance.
     * Will perform the changes on the UI's elements associated with the ChatController scene.
     * @param chatController The instance of ChatController
     * @param message The tagged message to be decoded
     */

    static void chatControllerDecoder(ChatController chatController, String message){

        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (tag == null) {

            chatController.getChatWindow().appendText(message + "\n");
            return;
        }

        switch (tag) {

            case MESSAGE:
                chatController.getChatWindow().appendText(message + "\n");
                break;
            case KILL:
                chatController.getVoteButton().setDisable(true);
            case NICKOK:
                break;
            case TIMER:
                System.out.println("Timer message");
                break;
            case NIGHT:
                chatController.toggleCss(EncodeDecode.NIGHT.decode(message));
                break;
            case NICKLIST:
                message = EncodeDecode.NICKLIST.decode(message);
                chatController.updateNickList(message);
                break;
            case NICK:
                chatController.getChatWindow().appendText("You are " + EncodeDecode.NICK.decode(message) + "\n");
                break;
            case ROLE:
                chatController.getChatWindow().appendText("You are assigned to " + EncodeDecode.ROLE.decode(message) + "\n");
                break;
            default:
                chatController.getChatWindow().appendText(message + "\n");
                System.out.println("Deu merda");
        }
    }
}
