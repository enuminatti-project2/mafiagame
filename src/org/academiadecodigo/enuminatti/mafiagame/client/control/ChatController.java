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
        messagTag(message);
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

    public void messagTag(String message) {

        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (tag == null) {
            chatWindow.appendText(message + "\n");
            return;
        }

        switch (tag) {

            case MESSAGE:
                chatWindow.appendText(message + "\n");
                break;
            case KILL:
                voteButton.setDisable(true);
                chatWindow.appendText("Foste com o c******o\n");
                break;
            case NICKOK:
                break;
            case TIMER:
                System.out.println("Timer message");
                break;
            case NIGHT:
                toggleCss(EncodeDecode.NIGHT.decode(message));
                break;
            case NICKLIST:
                message = EncodeDecode.NICKLIST.decode(message);
                updateNickList(message);
                break;
            case NICK:
                chatWindow.appendText("You are " + EncodeDecode.NICK.decode(message) + "\n");
                break;
            case ROLE:
                chatWindow.appendText("You are assigned to " + EncodeDecode.ROLE.decode(message) + "\n");
                break;
            default:
                chatWindow.appendText(message + "\n");
                System.out.println("Deu merda");
        }
    }

    public void toggleCss(String message){

        boolean night = Boolean.parseBoolean(message);

        System.out.println("Night: " + night);


        if (dayCSS == null){
            nightCSS = getClass().getResource("css/night.css").toExternalForm();
            dayCSS = getClass().getResource("css/day.css").toExternalForm();
        }

        voteButton.setDisable(false);
        pane.getScene().getStylesheets().clear();
        pane.getScene().getStylesheets().removeAll();

        if (night) {
            pane.getScene().getStylesheets().add(nightCSS);
            return;
        }
        pane.getScene().getStylesheets().add(dayCSS);
    }
}

