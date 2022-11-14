package com.aikucun.akapp.utils;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * RSA 加密解密相关工具类
 * Created by jarry on 16/5/11.
 */
public final class RSAUtils
{
    // 加密公钥
    private static String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC36uOYiF/v3Jk5JLeRNW7"
            + "\r" + "9wvr4C8kaYg8FxIP9I/id9byyHk7wws9humNlrs8YisbtkO8YONOyOYeaNVELJDrATIFoN" + "\r"
            + "a8i9vldujn9kc5IvjHUjsasxox1IB5BLn6KnAenP+0eOeMeY3nbE74YyT5jEXsGKmGyL4QN" + "\r"
            + "BlgJys92CwIDAQAB" + "\r";

//    private static String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC36uOYiF/v3Jk5JLeRNW79wvr4C8kaYg8FxIP9I/id9byyHk7wws9humNlrs8YisbtkO8YONOyOYeaNVELJDrATIFoNa8i9vldujn9kc5IvjHUjsasxox1IB5BLn6KnAenP+0eOeMeY3nbE74YyT5jEXsGKmGyL4QNBlgJys92CwIDAQAB";

    private static String KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static String RSA = "RSA";

    /**
     * 密码加密
     * 先MD5，再用公钥RSA加密，返回base64字符串
     *
     * @param password 需加密数据的byte数据
     * @return 加密后的字符串数据
     */
    public static String rsaEncryptPassword(String password)
    {
        String encryptString = "";
        try
        {
            // 从字符串中得到公钥
            RSAPublicKey publicKey = loadPublicKey(PUCLIC_KEY);
            // MD5
            String md5String = md5String(password);
            // RSA 加密
            byte[] encryptByte = encryptData(md5String.getBytes(), publicKey);
            // Base64
            encryptString = Base64.encodeToString(encryptByte, Base64.DEFAULT);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return encryptString;
    }

    /**
     * 用公钥加密
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data      需加密数据的byte数据
     * @param publicKey 公钥
     * @return 加密后的byte型数据
     */
    public static byte[] encryptData(byte[] data, RSAPublicKey publicKey) throws Exception
    {
        try
        {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 传入编码数据并返回编码结果
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e)
        {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e)
        {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e)
        {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData 经过encryptedData()加密返回的byte数据
     * @param privateKey    私钥
     * @return
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKey(InputStream in) throws Exception
    {
        try
        {
            return loadPublicKey(readKey(in));
        } catch (IOException e)
        {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception
    {
        try
        {
            byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
//            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e)
        {
            throw new Exception("公钥非法");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 读取密钥信息
     *
     * @param in 文件数据流
     * @return
     * @throws IOException
     */
    private static String readKey(InputStream in) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null)
        {
            if (readLine.charAt(0) == '-')
            {
                continue;
            } else
            {
                sb.append(readLine);
                sb.append('\r');
            }
        }

        return sb.toString();
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string 待转换的字符串
     * @return MD5加密后的字符串
     */
    public static String md5String(String string)
    {
        byte[] hash;

        try
        {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes());

        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash)
        {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * RSA签名校验
     *
     * @param content 签名数据
     * @param sign    签名值
     * @return
     */
    public static boolean rsaVerifyData(String content, String sign)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            byte[] encodedKey = Base64.decode(PUCLIC_KEY, Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            Signature signature = Signature.getInstance("SHA1WithRSA");

            signature.initVerify(pubKey);
            signature.update(Base64.decode(content, Base64.DEFAULT));

            boolean bverify = signature.verify(Base64.decode(sign, Base64.DEFAULT));
            return bverify;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
