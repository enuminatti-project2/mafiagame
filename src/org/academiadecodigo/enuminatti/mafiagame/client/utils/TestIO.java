package org.academiadecodigo.enuminatti.mafiagame.client.utils;

import java.util.LinkedHashMap;

public class TestIO {
    public static void main(String[] args) {
        TestIO io = new TestIO();

        //io.testWrite();
        io.testRead();
    }

    private void testRead(){
        //LinkedHashMap<String, String> map = InputOutput.IOReadHost("savedfiles/presistenseTest.txt");
        LinkedHashMap<String, String> map2 = InputOutput.IOReadNick("savedfiles/presistenseTest.txt");
        //System.out.println(map.keySet());
        System.out.println(map2.keySet());

    }

    private void testWrite(){
        InputOutput.addHost("127.0.0.1",6667,"home", "savedfiles/presistenseTest.txt");
        //InputOutput.addHost("127.0.0.1",6668,"home", "savedfiles/presistenseTest.txt");
        //InputOutput.addHost("127.0.0.2",6667,"home", "savedfiles/presistenseTest.txt");

    }
}
