package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {

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
    void cenas(ActionEvent event) {

        if (clientPrompt.getText().matches(".*\\S.*"))
            chatWindow.appendText(clientPrompt.getText().replaceAll("\\s+", " ") + "\n");
        clientPrompt.setText("");
        clientPrompt.requestFocus();
    }


    @FXML
    void vote(ActionEvent event) {

        System.out.println(usersList.getSelectionModel().getSelectedItem());
    }

    @FXML
    void initialize() {
        assert usersList != null : "fx:id=\"usersList\" was not injected: check your FXML file 'ClientView.fxml'.";

        usersList.setItems(names);
        //clientPrompt.requestFocus();
    }



}

