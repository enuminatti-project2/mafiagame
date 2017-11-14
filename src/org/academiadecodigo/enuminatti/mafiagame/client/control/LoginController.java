package org.academiadecodigo.enuminatti.mafiagame.client.control;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.InputOutput;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import static org.academiadecodigo.enuminatti.mafiagame.client.utils.ClientConstants.REGEXIP;

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
    private Label nickError;

    @FXML
    private ComboBox<String> nicksCombo;

    @FXML
    private ComboBox<String> serversCombo;

    @FXML
    private Button guestButton;

    private Client client;

    private Pattern pattern;

    Map<String, String> hostsMap;

    Set<String> nicksList;

    @FXML
    void connectToServer(ActionEvent event) {
        if (client == null) {
            client = new Client(this);
            Matcher matcher = pattern.matcher(serversCombo.getValue());
            if (!matcher.find()){
                serverError.setVisible(true);
                return;
            }
            try {
                String host = matcher.group(0);
                serverError.setVisible(false);
                client.connect(host);
                sendHosts();
                doLogin();
            } catch (IOException e) {
                serverError.setVisible(true);
                client = null;
            }
        }
    }

    private void doLogin() {
        client.encodeAndSend(EncodeDecode.LOGIN, nicksCombo.getValue() + "," + pwdField.getText().hashCode());
    }

    //TODO exchange data with the server
    private void sendHosts() {
        String serversList = "";
        for (Map.Entry<String, String> entry : hostsMap.entrySet()){
            serversList += entry.getKey() + "|" + entry.getValue() + ",";
        }
        client.encodeAndSend(EncodeDecode.HOSTSLIST, serversList);
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

        populateHosts();
        populateNicks();
        pattern = Pattern.compile(REGEXIP);
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

    private void populateHosts(){
        hostsMap = InputOutput.readHosts();

        LinkedList<String> tempList = new LinkedList<>();

        for (Map.Entry<String, String> entry : hostsMap.entrySet()){
            String s = entry.getValue() + "( " + entry.getKey() + " )";
            tempList.add(s);
        }

        serversCombo.setItems(FXCollections.observableArrayList(tempList));
        serversCombo.setEditable(true);
    }

    private void populateNicks(){
        nicksList = InputOutput.readNicks();
        nicksCombo.setItems(FXCollections.observableArrayList(nicksList));
        nicksCombo.setEditable(true);
    }

    public void nickInUse() {
        nickError.setVisible(true);
    }

    public void wrongPWD() {
        pwdError.setVisible(true);
    }
}
