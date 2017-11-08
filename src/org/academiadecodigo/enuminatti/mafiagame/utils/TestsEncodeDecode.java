package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.util.Map;
import java.util.Scanner;

/**
 * Created by Samuel Laço on 07/11/17.
 */
public class TestsEncodeDecode {
    public static void main(String[] args) {
        TestsEncodeDecode test = new TestsEncodeDecode();
        test.test1();
        test.test2();
        test.test3();
        test.test4();
        test.test5();
    }

    private void test1() {
        if (EncodeDecode.MESSAGE.decode("<MSG>sss</MSG>") == null) {
            System.out.println("Fail test 1");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSG>sss</MSGG>") != null) {
            System.out.println("Fail test 2");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSGs>sss</MSG>") != null) {
            System.out.println("Fail test 3");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSG>sss<MSG>") != null) {
            System.out.println("Fail test 4");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSGsss<MSG>") != null) {
            System.out.println("Fail test 5");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSG>sssMSG>") != null) {
            System.out.println("Fail test 6");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSGsssMSG>") != null) {
            System.out.println("Fail test 7");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("</MSG>sss</MSG>") != null) {
            System.out.println("Fail test 8");
            return;
        }

        if (EncodeDecode.MESSAGE.decode("<MSG>sss<//MSG>") != null) {
            System.out.println("Fail test 9");
            return;
        }

        System.out.println("Passed test of decode malformed tags");
    }

    private void test2(){

        String message = "This is a test message";

        if (!EncodeDecode.MESSAGE.decode(EncodeDecode.MESSAGE.encode(message)).equals(message)) {
            System.out.println("Fail test 1");
            return;
        }

        System.out.println("Passed test of encoding strings");
    }

    private void test3(){

        String[] validStrings = {"<MSG>", "<NICK>", "<NICKOK>"};
        String[] invalidStrings = {"<Message>", "<NICKo>", "<anotehrtag>"};
        int failedTests = 0;

        for (String validString : validStrings) {
            if (!EncodeDecode.isInEnum(validString)) {
                System.out.println("Failed the test of valid strings" + validString);
                failedTests++;
            }
        }

        for (String invalidString : invalidStrings) {
            if (EncodeDecode.isInEnum(invalidString)) {
                System.out.println("Failed the test of invalid strings");
                failedTests++;
            }
        }

        if (failedTests == 0) {
            System.out.println("Passed test of find strings inside the enum");
            return;
        }

        System.out.println("Failed " + failedTests + " tests");
    }

    private void test4(){

        if (!EncodeDecode.getStartTag("<sss>ssss</sss>").equals("<sss>")){
            System.out.println("Fail test 1");
            return;
        }

        if (EncodeDecode.getStartTag("<<<<<<sss>ssss</sss>")!= null){
            System.out.println("Fail test 2");
            return;
        }

        if (EncodeDecode.getStartTag("<ss<s>>>>>>ssss</sss>")!= null){
            System.out.println("Fail test 3");
            return;
        }

        if (EncodeDecode.getStartTag("<<<<<<ss<>s>ssss</sss>")!= null){
            System.out.println("Fail test 4");
            return;
        }

        if (EncodeDecode.getStartTag("<!<<<<<ss<>s>ssss</sss>")!= null){
            System.out.println("Fail test 5");
            return;
        }

        if (EncodeDecode.getStartTag("<!sss>ssss</sss>")!= null){
            System.out.println("Fail test 6");
            return;
        }

        System.out.println("Passed test of get the startTag");

    }

    private void test5(){
        String message = "<_NICKMESSAGE><NICK>Samuel</NICK><MSG>ola mundo</MSG></_NICKMESSAGE>";
        String tag = EncodeDecode.getStartTag(message);
        EncodeDecode enumType = EncodeDecode.getEnum(tag);

        if (enumType != null) {
            Map<EncodeDecode, String> mapEnum = enumType.decodeStringMap(message);

            System.out.println("The user: " + mapEnum.get(EncodeDecode.NICK) + " said: " + mapEnum.get(EncodeDecode.MESSAGE));
        }
    }
}
