package com.lstoicescu.jpeg_images_autentification.login.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lstoicescu on 12/8/2017.
 */

public class Crypt {

    public static byte[] getHash(String password) {
        byte[] result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            result = digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
