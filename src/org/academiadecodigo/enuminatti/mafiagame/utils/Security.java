package org.academiadecodigo.enuminatti.mafiagame.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {

    private final static String ALGORITHM = "SHA";

    public static String getHash(String message) {

        try {

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);

            // obtain a new hash
            md.reset();
            md.update(message.getBytes());
            byte[] digest = md.digest();

            // convert hash bytes into string
            BigInteger bigInt = new BigInteger(1, digest);
            StringBuilder hashtext = new StringBuilder(bigInt.toString(16));

            // zero pad the hash for the full 32 chars
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();

        } catch (NoSuchAlgorithmException ex) {
            return message;
        }

    }

}
