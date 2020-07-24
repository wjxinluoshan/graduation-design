package com.ajobs.yuns.tool;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyPairGenerator {

  //解密
  private PrivateKey privateKey;
  //加密
  private PublicKey publicKey;

  public RSAKeyPairGenerator() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024);
      KeyPair pair = keyGen.generateKeyPair();
      this.privateKey = pair.getPrivate();
      this.publicKey = pair.getPublic();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public String getBase64StringPublicKey() {
    return Base64.getEncoder()
        .encodeToString(publicKey.getEncoded());
  }

  public String getBase64StringPrivateKey() {
    return Base64.getEncoder()
        .encodeToString(privateKey.getEncoded());
  }
}
