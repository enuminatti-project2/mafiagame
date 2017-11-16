package org.academiadecodigo.enuminatti.mafiagame.client.control.decoder;

import javafx.application.Platform;
import org.academiadecodigo.enuminatti.mafiagame.client.control.ChatController;
import org.academiadecodigo.enuminatti.mafiagame.client.control.Controller;
import org.academiadecodigo.enuminatti.mafiagame.client.control.LobbyController;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 16/11/17.
 */
public class LobbyDecoder implements Decoder{


    /**
     *  Decodes messages sent to a LobbyController instance.
     * Will perform the changes on the UI's elements associated with the LobbyController scene.
     *
     * @param controller The instance of Controller
     * @param message  The tagged message to be decoded
     *
     */

    @Override
    public void controllerDecoder(Controller controller, String message) {

        LobbyController lobbyController = (LobbyController) controller;

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
