package koal.twoday.simple.utils;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import koal.twoday.simple.constant.CryptInfo;

public class CryptUtils 
{
    /**
     * 单例模式实例化Base64
     */
    private static class Base64UtilInstance{
        private static final Base64 BASE64 = new Base64();
    }
    public static Base64 base64GetInstance(){
        return Base64UtilInstance.BASE64;
    }
    /**
     * 生成AESKey
     * @param key
     * @return
     */
    public static SecretKey generateAESKey(byte[] key){
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(CryptInfo.IMPLEMENTS_ALGO);
            secureRandom.setSeed(key);
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(CryptInfo.AES_KEY_LENGTH, secureRandom);
            SecretKey sk = kg.generateKey();
            // byte[] by = sk.getEncoded();
            // String key = Base64.encodeBase64String(by);
            return sk;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("没有此算法");
        }
    }
    /**
     * RSA加密执行方法
     * @param plainText
     * @param pubFile
     * @return
     * @throws Exception
     */
    public static String rsaEncrypt(String plainText, File pubFile) throws Exception{
        PublicKey publicKey = CryptUtils.getPublicKey(IOUtils.readFile(pubFile));
        Cipher cipher = Cipher.getInstance(CryptInfo.CRYPT_METHOD);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] finalBytes = cipher.doFinal(plainText.getBytes());
        String result = base64GetInstance().encodeToString(finalBytes);
        return result;
    }
    /**
     * RSA解密执行方法
     * @param cryBytes
     * @param prvFile
     * @return
     * @throws Exception
     */
    public static String rsaDecrypt(byte[] cryBytes, String prvKeyContent) throws Exception{
        PrivateKey prvKey = CryptUtils.getPrivateKey(prvKeyContent);
        Cipher cipher = Cipher.getInstance(CryptInfo.CRYPT_METHOD);
        cipher.init(Cipher.DECRYPT_MODE, prvKey);
        byte[] plain = cipher.doFinal(base64GetInstance().decode(cryBytes));
        String plainText = new String(plain);
        return plainText;
    }
    /**
     * aes加密执行方法
     * @param plainBytes
     * @param model
     * @return
     * @throws Exception
     */
    public static String[] aesEncrypt(byte[] plainBytes,int model) throws Exception{
        SecretKey secretKey = generateAESKey(CryptInfo.AES_KEY.getBytes());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] resultb = cipher.doFinal(plainBytes);
        byte[] by = secretKey.getEncoded();
        String key = Base64.encodeBase64String(by);
        String encodeRe = base64GetInstance().encodeToString(resultb);
        String[] result = {key, encodeRe};
        return result;
    }
    /**
     * AES解密执行方法
     * @param cryBytes
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(byte[] cryBytes, String aesPlanKey) throws Exception{
        byte[] aesKeyBytes = base64GetInstance().decode(aesPlanKey);
        SecretKey secretKey = new SecretKeySpec(aesKeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plain = cipher.doFinal(base64GetInstance().decode(cryBytes));
        String plainText = new String(plain);
        return plainText;
    }
    /**
     * RSA获取公钥方法
     * @param pubKey
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String pubKey) throws Exception{
        byte[] encodePubKey = base64GetInstance().decode(pubKey.getBytes());
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encodePubKey);
        return KeyFactory.getInstance(CryptInfo.CRYPT_METHOD).generatePublic(encodedKeySpec);
    }
    /**
     * RSA获取私钥方法
     * @param prvKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String prvKey) throws Exception{
        byte[] encodePrvKey = base64GetInstance().decode(prvKey.getBytes());
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(encodePrvKey);
        return KeyFactory.getInstance(CryptInfo.CRYPT_METHOD).generatePrivate(encodedKeySpec);
    }
    /**
     * 保存RSA密钥通用方法
     * @param key
     * @param keyFile
     * @throws IOException
     */
    public static void saveKeyFile(Key key, File keyFile) throws IOException{
        byte[] encodeBytes = key.getEncoded();
        String base64 = base64GetInstance().encodeToString(encodeBytes);
        IOUtils.wirteFiles(base64, keyFile);
    }
    /**
     * 生成RSA Keypair方法
     * @param secureRandomSeed
     * @return
     */
    public static KeyPair generateKeyPair(String secureRandomSeed){
        KeyPair keyPair = null;// KeyPair是密钥对的简单持有者，加密和解密都需要用到
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CryptInfo.CRYPT_METHOD); // 获取生成RSA加密算法的公钥/私钥对
            SecureRandom secureRandom = SecureRandom.getInstance(CryptInfo.IMPLEMENTS_ALGO); // 获取实现指定算法的随机数生成器（RNG）对象
            secureRandom.setSeed(secureRandomSeed.getBytes());
            keyPairGenerator.initialize(CryptInfo.RSA_KEY_LENGTH, secureRandom);
            keyPair = keyPairGenerator.genKeyPair();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return keyPair;
    }
}
