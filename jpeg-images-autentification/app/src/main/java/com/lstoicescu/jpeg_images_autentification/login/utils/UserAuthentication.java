package com.lstoicescu.jpeg_images_autentification.login.utils;

/**
 * Created by lstoicescu on 12/8/2017.
 */

public class UserAuthentication {

    public boolean checkString(String password){
        byte[] hash = Crypt.getHash(password);
        String attempt = bytesToHex(hash);
        return attempt.equals("f36f0512c9f928869cf12eb68699eb5");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) result.append(Integer.toString((b & 0xff) , 16).substring(1));
        return result.toString();
    }
}
