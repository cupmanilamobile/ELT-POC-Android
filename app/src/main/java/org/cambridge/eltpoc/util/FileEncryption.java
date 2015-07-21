package org.cambridge.eltpoc.util;

import org.cambridge.eltpoc.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by etorres on 7/21/15.
 */
public class FileEncryption {
    public static void encrypt(String directory) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(directory);
        FileOutputStream fos = new FileOutputStream(directory+"_encrypted");
        SecretKeySpec sks = new SecretKeySpec(Constants.SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void decrypt(String directory) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(directory);
        String decrypted = directory.replace("_encrypted", "");
        FileOutputStream fos = new FileOutputStream(decrypted+"_decrypted");
        SecretKeySpec sks = new SecretKeySpec(Constants.SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }
}
