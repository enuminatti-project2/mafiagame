package org.academiadecodigo.enuminatti.mafiagame.client.control;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.client.utils.InputOutput;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;
import sun.net.util.IPAddressUtil;


public class LoginController implements Controller {

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

    private Map<String, String> hostsMap;

    private Set<String> nicksList;

    @FXML
    void connectToServer(ActionEvent event) {

        boolean flag = false;

        if (serversCombo.getValue() == null){
            serverError.setVisible(true);
            flag = true;
        } else {
            serverError.setVisible(false);
        }

        if(nicksCombo.getValue() == null){
            nickError.setText("Invalid Nick");
            nickError.setVisible(true);
            flag = true;
        } else {
            nickError.setVisible(false);
        }


        if (flag){
            return;
        }

        if (!connect()) {
            return;
        }

        doLogin();
    }

    private void doLogin() {
        String nick = nicksCombo.getValue();
        InputOutput.addNick(nick);
        client.encodeAndSend(EncodeDecode.LOGIN, nick + "," + pwdField.getText().hashCode());
    }

    private void sendHosts() {
        String serversList = "";
        for (Map.Entry<String, String> entry : hostsMap.entrySet()) {
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
    }

    @FXML
    void playAsGuest() {
        if (!connect()) {
            return;
        }
        client.encodeAndSend(EncodeDecode.GUESTLOGIN, "");
    }

    private boolean connect() {
        if (client != null) {
            return false;
        }
        String host = serversCombo.getValue();
        if (host == null || !host.contains("(")) {
            serverError.setVisible(true);
            return false;
        }

        host = host.substring(host.indexOf("(") + 1, host.indexOf(")"));
        if (host.length() == 0){
            host = serversCombo.getValue();
        }

        if (!IPAddressUtil.isIPv4LiteralAddress(host)){
            return false;
        }

        try {
            client = new Client(this);
            serverError.setVisible(false);
            client.connect(host);
            sendHosts();
            return true;
        } catch (IOException e) {
            serverError.setVisible(true);
            client = null;
            return false;
        }

    }

    @Override
    public void shutdown() {
        saveLists();
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

    private void populateHosts() {
        hostsMap = InputOutput.readHosts();

        LinkedList<String> tempList = new LinkedList<>();

        for (Map.Entry<String, String> entry : hostsMap.entrySet()) {
            String s = entry.getValue() + " (" + entry.getKey() + ")";
            tempList.add(s);
        }

        serversCombo.setItems(FXCollections.observableArrayList(tempList));
        serversCombo.setEditable(true);
        if (!hostsMap.isEmpty()) {
            serversCombo.getSelectionModel().select(0);
        }
    }

    private void populateNicks() {
        nicksList = InputOutput.readNicks();
        nicksCombo.setItems(FXCollections.observableArrayList(nicksList));
        nicksCombo.setEditable(true);
    }

    void nickInUse() {
        nickError.setText("Nick already in use");
        nickError.setVisible(true);
    }

    void wrongPWD() {
        pwdError.setVisible(true);
    }

    void updateHostList(String message) {
        String[] tempHost = message.split(",");

        for (String hostToSplit : tempHost) {
            String[] s = hostToSplit.split("\\|");
            if (!hostsMap.containsKey(s[0])) {
                InputOutput.addHost(s[0], s[1]);
            }
        }
    }

    public void saveLists() {
        InputOutput.addNick(nicksCombo.getEditor().getText());
        InputOutput.addHost(serversCombo.getEditor().getText());
    }
}
