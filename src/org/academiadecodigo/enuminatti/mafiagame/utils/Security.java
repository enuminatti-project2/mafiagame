package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {

    public final static String ALGORITHM = "SHA";

    public static String getHash(String message) {

        try {

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);

            // obtain a new hash
            md.reset();
            md.update(message.getBytes());
            byte[] digest = md.digest();

            // convert hash bytes into string
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);

            // zero pad the hash for the full 32 chars
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;

        } catch (NoSuchAlgorithmException ex) {
            return message;
        }

    }

}
