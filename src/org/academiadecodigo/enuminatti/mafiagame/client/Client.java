package org.academiadecodigo.enuminatti.mafiagame.client;

import org.academiadecodigo.enuminatti.mafiagame.client.control.Controller;
import org.academiadecodigo.enuminatti.mafiagame.utils.Constants;
import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

// brain
public class Client {

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Controller controller;
    private Thread readerThread;

    public Client(Controller controller) {
        this.controller = controller;
    }

    public void connect(String host) throws IOException {
        socket = new Socket(host, Constants.PORT);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ServerListener serverListener = new ServerListener();
        readerThread = new Thread(serverListener);
        readerThread.start();
    }

    public void encodeAndSend(EncodeDecode tag, String message) {
        writer.println(tag.encode(message));
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private class ServerListener implements Runnable {

        private void receiveAndDecode() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    controller.getMessage(message);
                }
            } catch (IOException e) {
                System.out.println("Socket was closed.");
            }
        }

        @Override
        public void run() {
            receiveAndDecode();
        }
    }


}
