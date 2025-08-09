package com.adasplus.adas.security;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Crypto {
    private static final String ADAS_KEY = "kyadas66";
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    public static String encode(byte[] buffer) throws Exception {
        try {
            Key secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(ADAS_KEY.getBytes()));
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(1, secretKey, new IvParameterSpec("12345678".getBytes()));
            return base64Encode(cipher.doFinal(buffer));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decode(byte[] buffer) throws Exception {
        try {
            byte[] data = base64Decode(buffer);
            Key secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(ADAS_KEY.getBytes()));
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(2, secretKey, new IvParameterSpec("12345678".getBytes()));
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, 0);
    }

    private static byte[] base64Decode(byte[] data) {
        return Base64.decode(data, 0);
    }
}
