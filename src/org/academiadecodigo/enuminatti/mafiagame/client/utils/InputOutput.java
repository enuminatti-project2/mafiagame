package org.academiadecodigo.enuminatti.mafiagame.client.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class InputOutput {

    public static LinkedHashMap<String, String> IOReadHost(String pathToFile) {
        return reverseOrderMap(IORead(pathToFile, "Host"));
    }

    public static LinkedHashMap<String, String> IOReadNick(String pathToFile) {
        return reverseOrderMap(IORead(pathToFile, "Nick"));
    }

    private static LinkedHashMap<String, String> reverseOrderMap(Map <String, String> map){
        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        LinkedHashMap<String, String> newMap = new LinkedHashMap<>();
        for(int i = arrayList.size()-1; i >= 0; i--){
            String value = map.get(arrayList.get(i));
            String key = arrayList.get(i);
            newMap.put(key, value);
        }
        return newMap;
    }

    private static LinkedHashMap<String, String> IORead(String pathToFile, String type) {

        LinkedHashMap<String, String> listOfSN = new LinkedHashMap<>();

        FileReader fReader = null;
        BufferedReader bReader = null;

        String line = null;

        File file = new File(pathToFile);

        try {
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            line = bReader.readLine();

            if (!Objects.equals(line, "<HN>")) {
                return null;
            }

            line = bReader.readLine();

            while (!Objects.equals(line, "<" + type + ">") && line != null) {
                    line = bReader.readLine();
            }

            line = bReader.readLine();

            while (line != null && !line.matches("(<\\w+>)*")) {

                if (Objects.equals(type, "Nick")) {
                    listOfSN.put(line, "");
                    line = bReader.readLine();
                    continue;
                }
                String[] s = line.split("\\|");
                listOfSN.put(s[0], s[1]);
                line = bReader.readLine();
            }
            return listOfSN;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fReader.close();
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void addHost(String ip, int port, String name, String pathToFile) {
        final String lineSeparator = System.getProperty("line.separator");
        createIfNotExists(pathToFile);

        LinkedHashMap<String, String> hosts = IORead(pathToFile, "Host");
        if (hosts == null){
            hosts = new LinkedHashMap<>();
        }

        LinkedHashMap<String, String> nicks = IORead(pathToFile, "Nick");
        if (nicks == null){
            nicks = new LinkedHashMap<>();
        }

        StringBuilder bigString = new StringBuilder();

        hosts.put(ip + ":" + port, name);

        bigString.append("<HN>").append(lineSeparator).
                append("<Host>").append(lineSeparator).
                append(mapToString(hosts)).
                append("<Nick>").append(lineSeparator).
                append(mapToString(nicks));

        writeToFile(bigString, pathToFile);

    }

    public static void addNick(String nick, String pathToFile){
        final String lineSeparator = System.getProperty("line.separator");
        createIfNotExists(pathToFile);

        LinkedHashMap<String, String> hosts = IORead(pathToFile, "Host");
        if (hosts == null){
            hosts = new LinkedHashMap<>();
        }

        LinkedHashMap<String, String> nicks = IORead(pathToFile, "Nick");
        if (nicks == null){
            nicks = new LinkedHashMap<>();
        }

        StringBuilder bigString = new StringBuilder();

        bigString.append("<HN>").append(lineSeparator).
                append("<Host>").append(lineSeparator).
                append(mapToString(hosts)).
                append("<Nick>").append(lineSeparator).
                append(mapToString(nicks));

        writeToFile(bigString, pathToFile);
    }

    private static StringBuilder mapToString(LinkedHashMap<String, String> linkedHashMap) {
        final String lineSeparator = System.getProperty("line.separator");
        //final String lineSeparator = System.getProperty("\n");

        StringBuilder bigString = new StringBuilder();

        for (Map.Entry<String, String> newLine : linkedHashMap.entrySet()) {

            bigString.append(newLine.getKey()).append("|").append(newLine.getValue()).append(lineSeparator);

        }
        return bigString;
    }

    private static void writeToFile(StringBuilder bigString, String path) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(path);
            writer.append(bigString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createIfNotExists(String pathToFile){
        File file = new File(pathToFile);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
        for(int i=keys.size()-1; i>=0;i--){
        System.out.println(map.get(keys.get(i)));
    }*/

}
