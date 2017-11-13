package org.academiadecodigo.enuminatti.mafiagame.client.control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;

public class LoginController implements Controller{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button joinButton;

    @FXML
    private PasswordField pwdField;

    @FXML
    private Label serverError;

    @FXML
    private Label pwdError;

    @FXML
    private ComboBox<String> nicksCombo;

    @FXML
    private ComboBox<String> serversCombo;

    @FXML
    private Button guestButton;

    private Client client;

    @FXML
    void connectToServer(ActionEvent event) {
        if (client == null) {
            client = new Client(this);
            String host = serversCombo.getValue();
            try {
                serverError.setVisible(false);
                client.connect(host);
            } catch (IOException e) {
                serverError.setVisible(true);
                client = null;
            }
        }

    }

    @FXML
    void initialize() {
        assert joinButton != null : "fx:id=\"joinButton\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert pwdField != null : "fx:id=\"pwdField\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert serverError != null : "fx:id=\"serverError\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert pwdError != null : "fx:id=\"pwdError\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert nicksCombo != null : "fx:id=\"nicksCombo\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert serversCombo != null : "fx:id=\"serversCombo\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert guestButton != null : "fx:id=\"guestButton\" was not injected: check your FXML file 'LoginScreen.fxml'.";

    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public void getMessage(String message) {

        ControllerDecoder.loginControllerDecoder(this, message);
    }

    public Client getClient() {
        return client;
    }

}
