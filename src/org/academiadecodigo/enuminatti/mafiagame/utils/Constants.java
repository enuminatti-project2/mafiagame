package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * MIT License
 * (c) 2017 Ricardo Constantino
 */

public class Constants {
    public static final int PORT = 1337;
    public static int MAX_PLAYERS = 22;

    public static int MIN_PLAYERS = 3; // Always at least 2 players, or the game breaks

    public static int SECONDS_TO_START_GAME = 20;
    public static int SECONDS_TO_TALK = 60;
    public static int SECONDS_TO_VOTE = 20;
    public static final int SECONDS_ENDGAME = 5;

    public static String SQL_ADDRESS = "127.0.0.1";
    public static String SQL_USERNAME = "mafiagame";
    public static String SQL_PASSWORD = "cenas";

    public static final String GUN_SHOT_SOUND_PATH = "/resources/Gun_Shot_Sound.wav";
    public static final String ROPE_IMAGE_PATH = "/endImageRope.png";
    private static final String CONFIG_FILE = "savedfiles/config.cfg";

    public static void init() {

        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return;
        }

        FileReader fReader = null;
        BufferedReader bReader = null;

        String line = "";

        try {
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            while (true) {
                line = bReader.readLine();
                if (line == null){
                    break;
                }
                if (!line.contains("=")){
                    continue;
                }
                String[] s = line.split("=");

                if (s[0].startsWith("SQL_")) {
                    setSQL(s);
                    continue;
                }

                if (!s[1].matches("^[0-9]{1,5}")) {
                    continue;
                }
                setValues(s[0], Integer.parseInt(s[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fReader != null;
                fReader.close();
                assert bReader != null;
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setSQL(String[] s) {
        switch (s[0]) {
            case "SQL_ADDRESS":
                SQL_ADDRESS = s[1];
                break;
            case "SQL_USERNAME":
                SQL_USERNAME = s[1];
                break;
            case "SQL_PASSWORD":
                SQL_PASSWORD = s[1];
                break;
            default:
                break;
        }
    }

    private static void setValues(String name, Integer value) {

        if (value == null) {
            return;
        }

        value = Math.abs(value);

        switch (name) {
            case "MAX_PLAYERS":
                MAX_PLAYERS = value;
                break;
            case "MIN_PLAYERS":
                if (value < 3) {
                    MIN_PLAYERS = 3;
                    break;
                }
                MIN_PLAYERS = value;
                break;
            case "SECONDS_TO_START_GAME":
                if (value < SECONDS_ENDGAME - 1) {
                    SECONDS_TO_START_GAME = SECONDS_ENDGAME - 1;
                    break;
                }
                SECONDS_TO_START_GAME = value;
                break;
            case "SECONDS_TO_TALK":
                SECONDS_TO_TALK = value;
            case "SECONDS_TO_VOTE":
                SECONDS_TO_VOTE = value;
            default:
                break;
        }
    }
}
