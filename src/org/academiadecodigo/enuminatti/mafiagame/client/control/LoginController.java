package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.SceneNavigator;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.io.IOException;


/**
 * Created by codecadet on 08/11/17.
 */
public class LoginController implements Controller {

    @FXML
    private TextField ipAddressTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Button mainButton;

    @FXML
    private TextArea serverMessageArea;


    private Client client;

    @FXML
    void initialize() {
        ipAddressTextField.setText("");
        usernameTextField.setText("");
        mainButton.setText("Connect");
        serverMessageArea.setText("");
    }

    @FXML
    void connectToServer(ActionEvent event) {
        if (client == null) {
            client = new Client(this);
            String host = ipAddressTextField.getText();
            try {
                client.connect(host);
                ipAddressTextField.setDisable(true);
                mainButton.setText("Change nick");
                getMessage("Successfully connected to host " + host);
                changeNick(null);
            } catch (IOException e) {
                getMessage("Failed to connect to host " + host);
            }
        }
    }

    public void changeNick(ActionEvent event) {
        client.encodeAndSend(EncodeDecode.NICK, usernameTextField.getText());
        mainButton.setOnAction(this::changeNick);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public void getMessage(String message) {
        System.out.println(message + " on Login controller");

        if (message.startsWith("<")) {

            if (EncodeDecode.getStartTag(message).equals(EncodeDecode.START.getStartTag())) {

                Platform.runLater(() -> {
                    SceneNavigator.getInstance().loadScreen("ClientView");
                    SceneNavigator.getInstance().<ChatController>getController("ClientView").setClient(client);
                });

                return;
            }
        }
        serverMessageArea.appendText(message + "\n");
    }

}
