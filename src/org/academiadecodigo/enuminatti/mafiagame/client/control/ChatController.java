package org.academiadecodigo.enuminatti.mafiagame.client.control;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.academiadecodigo.enuminatti.mafiagame.client.Client;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

public class ChatController implements Controller {

    private Client client;
    private String nightCSS;
    private String dayCSS;
    private boolean night;

    @FXML
    private TextFlow flowChat;

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
    private ScrollPane scrollPane;

    @FXML
    void initialize() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert clientPrompt != null : "fx:id=\"clientPrompt\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert flowChat != null : "fx:id=\"flowChat\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert usersList != null : "fx:id=\"usersList\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'ClientView.fxml'.";
        assert voteButton != null : "fx:id=\"voteButton\" was not injected: check your FXML file 'ClientView.fxml'.";

        scrollPane.vvalueProperty().bind(flowChat.heightProperty());

    }

    @FXML
    void sendMessageToClient(ActionEvent event) {

        if (clientPrompt.getText().matches(".*\\S.*")) {
            //chatWindow.appendText(clientPrompt.getText().replaceAll("\\s+", " ") + "\n");
            String message = clientPrompt.getText();
            client.encodeAndSend(EncodeDecode.MESSAGE, message);
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
        client.encodeAndSend(EncodeDecode.NICKLIST, "");
        client.encodeAndSend(EncodeDecode.NICK, "asking for my nick");
        client.encodeAndSend(EncodeDecode.ROLE, "asking for my role");
        toggleCss("true");
    }


    private void updateNickList(String message) {
        String allnick[] = message.split(" ");
        ObservableList<String> names = FXCollections.observableArrayList(allnick);
        Platform.runLater(() -> usersList.setItems(names));
    }

    public void messagTag(String message) {

        EncodeDecode tag = EncodeDecode.getEnum(EncodeDecode.getStartTag(message));

        if (tag == null) {
            writeNewLine(message, Color.BLACK);
            return;
        }

        switch (tag) {

            case MESSAGE:
                writeNewLine(EncodeDecode.MESSAGE.decode(message), Color.BLACK);
                break;
            case KILL:
                voteButton.setDisable(true);
                writeNewLine("Foste com o C******", Color.RED);
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
                writeNewLine("You are " + EncodeDecode.NICK.decode(message), Color.HOTPINK);
                break;
            case ROLE:
                writeNewLine("You are assigned to " + EncodeDecode.ROLE.decode(message), Color.YELLOW);
                break;
            default:
                System.out.println("Deu merda");
        }
    }

    public void toggleCss(String message) {

        night = Boolean.parseBoolean(message);

        System.out.println("Night: " + night);


        if (dayCSS == null) {
            nightCSS = getClass().getResource("css/night.css").toExternalForm();
            dayCSS = getClass().getResource("css/day.css").toExternalForm();
        }
        voteButton.setDisable(false);

        if (night) {
            StyleManager.getInstance().removeUserAgentStylesheet(dayCSS);
            StyleManager.getInstance().addUserAgentStylesheet(nightCSS);
            return;
        }
        StyleManager.getInstance().removeUserAgentStylesheet(nightCSS);
        StyleManager.getInstance().addUserAgentStylesheet(dayCSS);

    }

    private void writeNewLine(String message, Color color) {

        final String finalMessage = (message == null ? "": message);
        final Color textColor;

        if (night && color == Color.BLACK){
            textColor = Color.WHITE;
        } else {
            textColor = color;
        }

        Platform.runLater(
                () -> {

                    Text newText = new Text();
                    newText.setFill(textColor);
                    newText.setText(finalMessage + "\n");
                    flowChat.getChildren().add(newText);                }
        );

    }
}

