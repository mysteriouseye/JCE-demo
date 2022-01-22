package koal.twoday.simple.runtask;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import koal.twoday.simple.constant.CryptInfo;
import koal.twoday.simple.entity.EncryptFile;
import koal.twoday.simple.entity.FileProperties;
import koal.twoday.simple.utils.CryptUtils;
import koal.twoday.simple.utils.IOUtils;

/**
 * App 实现Callable接口
 */
public class AppCallable implements Callable<EncryptFile> {
    private static volatile Logger logger = Logger.getLogger(App.class.getName());
    private volatile FileProperties properties;
    private volatile String flag;
    private volatile EncryptFile encryptFile;
    /**
     * 构造方法参数
     * @param fileProperties 文件属性对象
     * @param flag 鉴定是加密还是解密的的标志
     */
    AppCallable(FileProperties fileProperties, String flag){
        this.properties = fileProperties;
        this.flag = flag;
    }
    /**
     * 重载构造方法参数
     * @param fileProperties 文件属性对象
     * @param encryptFile 加密文件内容镀锡
     * @param flag 鉴定是加密还是解密的的标志
     */
    AppCallable(FileProperties fileProperties, EncryptFile encryptFile, String flag){
        this.properties = fileProperties;
        this.flag = flag;
        this.encryptFile = encryptFile;
    }
    /**
     * 重写实现的call方法，运行线程
     */
    @Override
    public EncryptFile call() throws Exception {
        if("encrypt".equals(flag)){
            try {
                EncryptFile encryptFile = null;
                KeyPair keyPair = CryptUtils.generateKeyPair(CryptInfo.CRYPT_SEED);
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();
                CryptUtils.saveKeyFile(publicKey, new File(CryptInfo.PUB_KEY_NAME));
                CryptUtils.saveKeyFile(privateKey, new File(CryptInfo.PRV_KEY_NAME));
                String[] contentResult = CryptUtils.aesEncrypt(properties.getFileContent().getBytes(), 1);
                String[] nameResult = CryptUtils.aesEncrypt(properties.getFileName().getBytes(), 1);
                String aesRSAECkey = CryptUtils.rsaEncrypt(contentResult[0], new File(CryptInfo.PUB_KEY_NAME));
                encryptFile = new EncryptFile(aesRSAECkey, contentResult[1], nameResult[1]);
                return encryptFile;
            } catch (Exception e) {
                logger.severe("加密错误！请检查加密源是否有问题！");
            }
        }else if("decrypt".equals(flag)){
            try {
                String aesEncrypt = encryptFile.getAesEncrypt();
                String fileEncrypt = encryptFile.getFileEncrypt();
                String nameEncrypt = encryptFile.getFileName();
                String aes_plain = CryptUtils.rsaDecrypt(aesEncrypt.getBytes(), properties.getFileContent());
                String result = CryptUtils.aesDecrypt(fileEncrypt.getBytes(), aes_plain);
                String fileName = CryptUtils.aesDecrypt(nameEncrypt.getBytes(), aes_plain);
                EncryptFile encryptFile = new EncryptFile(aes_plain, result, fileName);
                byte[] resultByte = result.getBytes();
                IOUtils.wirteFiles(new String(resultByte), new File(fileName));
                return encryptFile;
            } catch (Exception e) {
                logger.severe("解密错误！请检查源文件是否有问题！");
            }
        }
        return null;
    }
    
}
