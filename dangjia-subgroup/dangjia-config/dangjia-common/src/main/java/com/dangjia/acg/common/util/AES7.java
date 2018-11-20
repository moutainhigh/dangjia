package com.dangjia.acg.common.util;

import com.dangjia.acg.common.constants.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

/**
 *
 * @author ngh
 * AES128 算法
 *
 * CBC 模式
 *
 * PKCS7Padding 填充模式
 *
 * CBC模式需要添加一个参数iv
 *
 * 介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding 没有什么区别
 * 要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
 */
public class AES7 {

  // 算法名称
  final String KEY_ALGORITHM = Constants.DANGJIA_SESSION_KEY;

  // 加解密算法/模式/填充方式
  final String algorithmStr = "AES/CBC/PKCS7Padding";

  private Key key;
  private Cipher cipher;

  public static byte[] iv = Constants.DANGJIA_IV.getBytes();
  public void init(byte[] keyBytes) {

    // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
    int base = 16;
    if (keyBytes.length % base != 0) {
      int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
      byte[] temp = new byte[groups * base];
      Arrays.fill(temp, (byte) 0);
      System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
      keyBytes = temp;
    }
    // 初始化
    Security.addProvider(new BouncyCastleProvider());
    // 转化成JAVA的密钥格式
    key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    try {
      // 初始化cipher
      cipher = Cipher.getInstance(algorithmStr, "BC");
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchProviderException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  /**
   * 加密方法
   *
   * @param content
   *            要加密的字符串
   * @param keyBytes
   *            加密密钥
   * @return
   */
  public byte[] encrypt(byte[] content, byte[] keyBytes) {
    byte[] encryptedText = null;
    init(keyBytes);
    System.out.println("IV：" + new String(iv));
    try {
      cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
      encryptedText = cipher.doFinal(content);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return encryptedText;
  }
  /**
   * 解密方法
   *
   * @param encryptedData
   *            要解密的字符串
   * @param keyBytes
   *            解密密钥
   * @return
   */
  public byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
    byte[] encryptedText = null;
    init(keyBytes);
    System.out.println("IV：" + new String(iv));
    try {
      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
      encryptedText = cipher.doFinal(encryptedData);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return encryptedText;
  }


 public static void main(String[] args) {
    AES7 aes = new AES7();

    //   加解密 密钥
    String content = "{\"res\":1000,\"msg\":{\"resultCode\":1001,\"resultMsg\":\"电话号码或者密码错误\"}}";
    // 加密字符串
    System.out.println("加密前的：" + content);
    // 加密方法
    byte[] enc = aes.encrypt(content.getBytes(), AES7.iv);
    String s= new String(Hex.encode(enc));
    System.out.println("加密后的内容：" +s);
//    s= "FDE8C83AE49E96B87259ADCFCA35E61A67DA9C077A2F98B1CDB177671D6D8E1A33353E1DC6E5753A42435EBF3E8D25FDA0952B1303E7067711A403F719DDFC81E1F5AD659DCD7087DBBB80E6CAB38D7BCDB0430E7E95550C249CA7E729E08AE5";
    // 解密方法
    byte[] dec = aes.decrypt(Hex.decode(s), AES7.iv);
    System. out.println("解密后的内容：" + new String(dec));
  }

}
