package org.academiadecodigo.enuminatti.mafiagame.client.control.decoder;

import javafx.application.Platform;
import org.academiadecodigo.enuminatti.mafiagame.client.control.Controller;
import org.academiadecodigo.enuminatti.mafiagame.client.control.LobbyController;
import org.academiadecodigo.enuminatti.mafiagame.client.control.LoginController;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

/**
 * Created by codecadet on 16/11/17.
 */
public class LoginDecoder implements Decoder {

    /**
     * Decodes messages sent to a LoginController instance.
     * Will perform the changes on the UI's elements associated with the LoginController scene.
     *
     * @param controller The instance of Controller
     * @param message    The tagged message to be decoded
     */

    @Override
    public void controllerDecoder(Controller controller, String message) {

        LoginController loginController = (LoginController) controller;

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


}

