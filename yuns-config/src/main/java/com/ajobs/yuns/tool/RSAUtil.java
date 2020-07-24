package com.ajobs.yuns.tool;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAUtil {

  public static PublicKey getPublicKey(String base64PublicKey) {
    PublicKey publicKey = null;
    try {
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
          Base64.getDecoder().decode(base64PublicKey.getBytes()));
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      publicKey = keyFactory.generatePublic(keySpec);
      return publicKey;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return publicKey;
  }

  /**
   * @param data
   * @param publicKey :已经被base64过得publickey
   */
  public static byte[] encrypt(String data, String publicKey)
      throws Exception {
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipher.doFinal(data.getBytes());
  }

  public static PrivateKey getPrivateKey(String base64PrivateKey) {
    PrivateKey privateKey = null;
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
        Base64.getDecoder().decode(base64PrivateKey.getBytes()));
    KeyFactory keyFactory = null;
    try {
      keyFactory = KeyFactory.getInstance("RSA");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      privateKey = keyFactory.generatePrivate(keySpec);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return privateKey;
  }

  public static String decrypt(byte[] data, PrivateKey privateKey)
      throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return new String(cipher.doFinal(data));
  }

  public static String decrypt(String data, String base64PrivateKey)
      throws Exception {
    return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
  }
}
