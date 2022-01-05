package fr.hunh0w.wizardbox.server.managers;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class CryptoManager {

    private static final String x = "0198YPD7Y927Y937@f3#32DBXW781Y3D";
    private static final String algo = "AES";

    /* CRYPTO */
    public static byte[] encrypt(byte[] Data) throws Exception {
        Key k = new SecretKeySpec(x.getBytes(), algo);
        Cipher c = Cipher.getInstance(algo);
        c.init(Cipher.ENCRYPT_MODE, k);
        byte[] encVal = c.doFinal(Data);
        return encVal;
    }
    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Key k = new SecretKeySpec(x.getBytes(), algo);
        Cipher c = Cipher.getInstance(algo);
        c.init(Cipher.DECRYPT_MODE, k);
        byte[] decValue = c.doFinal(encryptedData);
        return decValue;
    }

}
