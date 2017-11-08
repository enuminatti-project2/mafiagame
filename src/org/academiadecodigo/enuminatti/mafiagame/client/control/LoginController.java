package org.academiadecodigo.enuminatti.mafiagame.client.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * Created by codecadet on 08/11/17.
 */
public class LoginController implements Controller {

    @FXML
    private TextField ipAddressTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextArea serverMessageArea;

    @FXML
    void connectoServer(ActionEvent event) {

    }

    @Override
    public void shutdown() {
//        client.shutdown();
    }

}
