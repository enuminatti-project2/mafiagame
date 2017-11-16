package org.academiadecodigo.enuminatti.mafiagame.client.control.decoder;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.academiadecodigo.enuminatti.mafiagame.client.control.ChatController;
import org.academiadecodigo.enuminatti.mafiagame.client.control.Controller;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 16/11/17.
 */
public class ChatDecoder implements Decoder {

    /**
     * Decodes messages sent to a ChatController instance.
     * Will perform the changes on the UI's elements associated with the ChatController scene.
     *
     * @param controller The instance of controller
     * @param message        The tagged message to be decoded
     */

    @Override
    public void controllerDecoder(Controller controller, String message) {

        ChatController chatController = (ChatController) controller;
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
                chatController.getVoteButton().setDisable(true);
                chatController.getSendButton().setDisable(true);
                Platform.runLater(() -> SceneNavigator.getInstance().back());
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


}

