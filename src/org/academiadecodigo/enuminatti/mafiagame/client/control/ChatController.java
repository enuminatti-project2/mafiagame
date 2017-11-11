package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

public class ChatController implements Controller {

    private Client client;
    private String nightCSS;
    private String dayCSS;


    @FXML
    private TextArea chatWindow;

    @FXML
    private TextField clientPrompt;

    @FXML
    private Button sendButton;

    @FXML
    private Button voteButton;

    @FXML
    private Pane pane;

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

        if (votedUser != null) {
            System.out.println("I voted in " + votedUser);
            client.encodeAndSend(EncodeDecode.VOTE, votedUser);
            usersList.getSelectionModel().clearSelection();
            voteButton.setDisable(true);
        }
    }

    public void getMessage(String message) {

        ControllerDecoder.chatControllerDecoder(this, message);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    void setClient(Client client) {
        this.client = client;
        this.client.setController(this);
        client.encodeAndSend(EncodeDecode.NICKLIST,"que sa foda este encode");
        client.encodeAndSend(EncodeDecode.NICK, "asking for my nick");
        client.encodeAndSend(EncodeDecode.ROLE, "asking for my role");
        toggleCss("false");
    }


    void updateNickList(String message) {
        String allnick[] = message.split(" ");
        ObservableList<String> names = FXCollections.observableArrayList(allnick);
        Platform.runLater(() ->usersList.setItems(names));
    }


    public void toggleCss(String message){

        boolean night = Boolean.parseBoolean(message);

        System.out.println("Night: " + night);

        if (dayCSS == null){
            nightCSS = getClass().getResource("css/night.css").toExternalForm();
            dayCSS = getClass().getResource("css/day.css").toExternalForm();
        }

        pane.getScene().getStylesheets().clear();
        pane.getScene().getStylesheets().removeAll();

        if (night) {
            pane.getScene().getStylesheets().add(nightCSS);
            return;
        }
        pane.getScene().getStylesheets().add(dayCSS);
        voteButton.setDisable(false);
    }

    public TextArea getChatWindow() {
        return chatWindow;
    }

    public Button getVoteButton() {
        return voteButton;
    }
}