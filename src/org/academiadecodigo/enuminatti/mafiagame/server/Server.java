package org.academiadecodigo.enuminatti.mafiagame.server;

import org.academiadecodigo.enuminatti.mafiagame.utils.EncodeDecode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class Server {

    private GameMaster gameMaster;
    private ServerSocket server;
    private ExecutorService executorService;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Server() {
        this.gameMaster = new GameMaster();
        executorService = Executors.newFixedThreadPool(100);
    }

    private void closeServer() {
        executorService.shutdown();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        // open socket
        // while to acceptConnections

        try {
            server = new ServerSocket(13337);
            System.out.println("waiting clients");
            while (true) {
                Socket client = server.accept();
                System.out.println("New Connection was accept.Socket number: " + client.getPort());
                acceptConnection(client);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    private void acceptConnection(Socket client) {
        PlayerHandler newPlayer = null;
        try {
            newPlayer = new PlayerHandler(client);
            newPlayer.init();
            executorService.submit(newPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class PlayerHandler implements Runnable {
        private Socket clientSocket;

        private String nickname;
        private boolean alive;
        private Role role;
        private BufferedReader in;
        private PrintWriter out;

        public PlayerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void init() throws IOException {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        }

        private void receiveMessage(String message) {
            gameMaster.receiveAndDecode(message);
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getNickname() {
            return nickname;
        }

        @Override
        public void run() {
            try {
                String message = "";
                while ((message = in.readLine()) != null) {
                    if (nickname == null) {
                        if (!tryRegister(message)) {
                            sendMessage(EncodeDecode.NICKOK.encode("false"));
                            this.nickname = EncodeDecode.NICK.decode(message);
                            continue;
                        }
                    }
                    receiveMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnectPlayer();
            }
        }

        private void disconnectPlayer() {
            System.out.println("disconnected player");
            try {
                if (in != null) {
                    in.close();
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean tryRegister(String message) {
            if (!Objects.equals(EncodeDecode.getStartTag(message), EncodeDecode.NICK.getStartTag())) {
                return false;
            }

            return gameMaster.addNick(EncodeDecode.NICK.decode(message), this);
        }

        public Role getRole() {
            return role;
        }
    }
}
