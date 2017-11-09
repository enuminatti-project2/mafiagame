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
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

public class ChatController implements Controller {

    Client client;


    @FXML
    private TextArea chatWindow;


    @FXML
    private TextField clientPrompt;

    @FXML
    private Button sendButton;

    @FXML
    private Button voteButton;

    @FXML
    private ListView<String> usersList;

    @FXML
    void sendMessageToClient(ActionEvent event) {

        if (clientPrompt.getText().matches(".*\\S.*")) {
            //chatWindow.appendText(clientPrompt.getText().replaceAll("\\s+", " ") + "\n");
            String message = clientPrompt.getText();
            client.encodeAndSend(EncodeDecode.MESSAGE,message);
            clientPrompt.setText("");
            clientPrompt.requestFocus();
        }

    }


    @FXML
    void vote(ActionEvent event) {

        String votedUser = usersList.getSelectionModel().getSelectedItem();

        client.encodeAndSend(EncodeDecode.VOTE, votedUser);
        usersList.getSelectionModel().clearSelection();
    }

    @FXML
    void initialize() {
        assert usersList != null : "fx:id=\"usersList\" was not injected: check your FXML file 'ClientView.fxml'.";

        //client = new Client(this);
        //clientPrompt.requestFocus();
    }

    public void getMessage(String message) {
        messagTag(message);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    public void setClient(Client client) {
        this.client = client;
        this.client.setController(this);
        client.encodeAndSend(EncodeDecode.NICKLIST,"que sa foda este encode");
    }


    public void updatenicklist(String message) {
        String allnick[] = message.split(" ");
        ObservableList<String> names = FXCollections.observableArrayList(allnick);
//        usersList.getItems().clear();
        usersList.setItems(names);
    }

    public void messagTag(String message) {

        System.out.println("Enum : " + message);
        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (tag == null) {
            chatWindow.appendText(message + "\n");
            return;
        }
        switch (tag) {

            case MESSAGE:
                chatWindow.appendText(message + "\n");
                break;
            case NICKOK:
                break;
            case TIMER:
                System.out.println("Timer message");
                break;
            case NIGHT:
                break;
            case NICKLIST:
                message = EncodeDecode.NICKLIST.decode(message);
                updatenicklist(message);
                break;
            case START:
                client.encodeAndSend(EncodeDecode.NICKLIST,"que sa foda este encode");
                break;
            default:
                chatWindow.appendText(message + "\n");
                System.out.println("Deu merda");

        }

    }


}

