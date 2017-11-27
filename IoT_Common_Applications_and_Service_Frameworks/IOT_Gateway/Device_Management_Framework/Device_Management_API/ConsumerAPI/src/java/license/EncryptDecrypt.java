package license;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class EncryptDecrypt {

    private static final String UNICODE_FORMAT = "UTF-8";
    public static final String ALGORITHM = "AES";
    public static final String MODE = "CBC";
    public static final String PADDING = "PKCS5Padding";
    public static int KEYSIZE = 256;
    private Cipher cipher;
    byte[] keyAsBytes;
    private String encryptionKey;
    private String transformation;
    private String encryptionScheme;
    SecretKey secretKey;
    private static Logger logger = null;
    byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    IvParameterSpec ivspec = new IvParameterSpec(iv);

    public EncryptDecrypt(String cusName, String prdId) throws Exception {
        try {
            //      System.out.println("EncryptDecrypt");
            logger = Logger.getLogger("EncryptDecrypt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Build encryptionKey here with the passed on parameters
        try {
            encryptionKey = cusName;
            encryptionKey += prdId;
            //encryptionKey += tStamp;

            byte[] encKey = hashEncryptKey(encryptionKey);
            //String encKey=hashEncryptKey(encryptionKey);
            System.out.println("custid=" + cusName + " prodid=" + prdId);
            System.out.println("EncryptKey length = " + encKey.length);
            encryptionScheme = ALGORITHM;
            transformation = ALGORITHM + "/" + MODE + "/" + PADDING;
            KeyGenerator keyGen = KeyGenerator.getInstance(encryptionScheme);
            System.out.println("keyGen=" + keyGen.toString());
            keyGen.init(KEYSIZE);
            secretKey = keyGen.generateKey();
            System.out.println("secretKey=" + secretKey.toString());
            cipher = Cipher.getInstance(transformation);
            //byte[] keyAsBytes = encKey.getBytes(UNICODE_FORMAT);
            secretKey = new SecretKeySpec(encKey, ALGORITHM);
            //key = new SecretKeySpec(keyAsBytes, ALGORITHM);	

        } catch (Exception e) {
            e.printStackTrace();
            //    System.out.println("Constructor: " + e.getMessage());
            System.out.println("Constructor: " + e.getMessage());
        }
    }

    /**
     * Method To Encrypt the license key string
     */
    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            System.out.println("plainText="+plainText);
            byte[] encryptedText = cipher.doFinal(plainText);
            System.out.println("encryptedText="+encryptedText);
            encryptedString = Base64.encodeBase64String(encryptedText);
            System.out.println("encryptedString" + encryptedString);

        } catch (Exception e) {
            System.out.println("EncryptDecrypt::encrypt()..." + e.getMessage());
            e.printStackTrace();
        }
        return encryptedString;
    }

    /**
     * Method To Decrypt the license key string
     */
    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            System.out.println("decrypt(): After cipher init()..key= " + secretKey.toString() + "len=" + secretKey.toString().length());
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            System.out.println("decrypt(): encryptedText: " + encryptedText.toString());
            byte[] plainText = cipher.doFinal(encryptedText);
            System.out.println("decrypt(): plainText: " + plainText.toString());
            decryptedText = bytes2String(plainText);
        } catch (IllegalStateException e1) {
            System.out.println("decrypt(): IllegalState Exception: " + e1.getMessage());
            return null;
        } catch (InvalidKeyException e1) {
            System.out.println("decrypt(): Invalidkey Exception: " + e1.getMessage());
            return null;
        } catch (IllegalBlockSizeException e1) {
            System.out.println("decrypt(): IllegalBlockSize Exception: " + e1.getMessage());
            return null;
        } catch (BadPaddingException e1) {
            System.out.println("decrypt(): Padding Exception: " + e1.getMessage());
            return null;
        } catch (NullPointerException e1) {
            System.out.println("decrypt(): Padding Exception: " + e1.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    /**
     * Returns String From An Array Of Bytes
     */
    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

    /*
     * Hashes the encrypt key
     */
    public byte[] hashEncryptKey(String pencryptKey) {

        byte[] byteData = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pencryptKey.getBytes(UNICODE_FORMAT));

            byteData = md.digest();

            System.out.println("hash key length = " + byteData.length);

        } catch (NoSuchAlgorithmException e) {

            System.out.println("hashEncryptKey(): " + e.getMessage());
        } catch (UnsupportedEncodingException uee) {
            System.out.println("hashEncryptKey(): " + uee.getMessage());
        }

        return byteData;
        //return hashedKey;
    }
}
