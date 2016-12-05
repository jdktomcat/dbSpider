package com.github.bpazy.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Ziyuan.
 * 2016/12/5 11:33
 */
public class Helper {
    private static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getRandomString(int length) {
        String temp = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder buffer = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            buffer.append(temp.charAt(random.nextInt(temp.length())));
        }
        return buffer.toString();
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(hexChar[(aB & 0xf0) >>> 4]);
            sb.append(hexChar[aB & 0x0f]);
        }
        String s = sb.toString();
        return s.substring(8, s.length() - 8);
    }

    public static String MD5(String content) {
        String s = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            byte[] bytes = digest.digest();
            s = toHexString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }
}
