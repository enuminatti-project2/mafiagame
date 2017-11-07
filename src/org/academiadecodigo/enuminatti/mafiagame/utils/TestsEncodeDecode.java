package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.Scanner;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public class TestsEncodeDecode {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        if (EncodeDecode.MESSAGE.decode("MessagewithoutTags") == null)
            System.out.println("Test1 success");
        else
            System.out.println("Test1 fail");


    }
}
