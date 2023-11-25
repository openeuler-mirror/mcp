package com.hnkylin.cloud.core.common;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class AESUtil {
    private static Cipher AES_CIPHER;
    private static SecretKey AES_KEY;

    static {
        try {
            AES_CIPHER = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            AES_KEY = new SecretKeySpec("ksksvdd's secret".getBytes("UTF-8"), "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String obfuscateHex(byte[] data) throws Exception {
        AES_CIPHER.init(Cipher.ENCRYPT_MODE, AES_KEY);
        byte[] cipherText = AES_CIPHER.doFinal(data);
        return new Hex().encodeHexString(cipherText);
    }

    public static byte[] revealHex(String codedText) throws Exception {
        byte[] encypted = new Hex().decode(codedText.getBytes("UTF-8"));
        AES_CIPHER.init(Cipher.DECRYPT_MODE, AES_KEY);
        byte[] decrypted = AES_CIPHER.doFinal(encypted);
        return decrypted;
    }

    /**
     * 加密
     *
     * @param plainText
     * @return
     * @throws Exception
     */
    public static String obfuscate(String plainText) throws Exception {
        AES_CIPHER.init(Cipher.ENCRYPT_MODE, AES_KEY);
        byte[] cipherText = AES_CIPHER.doFinal(plainText.getBytes("UTF-8"));
        return new Hex().encodeHexString(cipherText);
    }

    /**
     * 解密
     *
     * @param codedText
     * @return
     * @throws Exception
     */
    public static String reveal(String codedText) throws Exception {
        byte[] encypted = new Hex().decode(codedText.getBytes("UTF-8"));
        AES_CIPHER.init(Cipher.DECRYPT_MODE, AES_KEY);
        byte[] decrypted = AES_CIPHER.doFinal(encypted);
        return new String(decrypted);
    }

}
