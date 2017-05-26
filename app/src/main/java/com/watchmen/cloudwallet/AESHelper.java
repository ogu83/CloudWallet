package com.watchmen.cloudwallet;

import java.nio.charset.Charset;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ogu on 22.05.2017.
 */

public class AESHelper {
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static byte[] salt() {
        return encodeUTF8("ksdj123987654");
    }

    private static String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    public static String Encrypt(String password, byte[] text) throws Exception {
        byte[] bytes = EncryptOrDecrypt(password, text, true);
        if (bytes == null) return null;
        return decodeUTF8(bytes);
    }

    public static String Decrypt(String password, byte[] text) throws Exception{
        byte[] bytes = EncryptOrDecrypt(password, text, false);
        if (bytes == null) return null;
        return decodeUTF8(bytes);
    }

    private static byte[] EncryptOrDecrypt(String password, byte[] text, boolean encrypt) throws Exception {
        Rfc2898DeriveBytes keyGenerator = new Rfc2898DeriveBytes(password, salt(), 1000);
        byte[] keyBytes = keyGenerator.getBytes(32);
        byte[] ivBytes = keyGenerator.getBytes(16);
        SecretKeySpec secret = new SecretKeySpec(keyBytes, "AES");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secret, ivSpec);
        byte[] result = cipher.doFinal(text);
        return result;
    }
}