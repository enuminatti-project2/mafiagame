package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;

public class ChatController {

    Client client;


    @FXML
    private TextArea chatWindow;


    @FXML
    private TextField clientPrompt;

    @FXML
    private Button sendButton;

    @FXML
    private Button voteButton;

    ObservableList<String> names = FXCollections.observableArrayList(
            "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
    @FXML
    private ListView<String> usersList;

    @FXML
    void sendMessageToClient(ActionEvent event) {

        if (clientPrompt.getText().matches(".*\\S.*")) {
            //chatWindow.appendText(clientPrompt.getText().replaceAll("\\s+", " ") + "\n");
            String message = clientPrompt.getText();
            client.encodeAndSend("merda",message);
            clientPrompt.setText("");
            clientPrompt.requestFocus();
        }

    }


    @FXML
    void vote(ActionEvent event) {

        System.out.println(usersList.getSelectionModel().getSelectedItem());
    }

    @FXML
    void initialize() {
        assert usersList != null : "fx:id=\"usersList\" was not injected: check your FXML file 'ClientView.fxml'.";
        client = new Client(this);
        usersList.setItems(names);
        //clientPrompt.requestFocus();
    }

    public void getMessage(String message) {
        chatWindow.appendText(message + "\n");
    }


    public void shutdown() {
        client.shutdown();
    }
}

