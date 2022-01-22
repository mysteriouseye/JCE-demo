package koal.twoday.simple.runtask;

import java.io.File;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import koal.twoday.simple.entity.EncryptFile;
import koal.twoday.simple.entity.FileProperties;
import koal.twoday.simple.interfaces.IFileOperation;
import koal.twoday.simple.utils.IOUtils;

public class App implements IFileOperation {

    private static Logger logger = Logger.getLogger(App.class.getName());
    private static ExecutorService excutorService = null;
    /**
     * 保存密钥和文件的方法，这里是建立多线程并执行的步
     * @param fileProperties 读入的文件属性对象列表
     */
    @Override
    public void saveKeyAndFile(List<FileProperties> fileProperties) throws IOException {
        List<EncryptFile> encryptFiles = new ArrayList<>();
        List<FutureTask<EncryptFile>> tasks = new ArrayList<>();
        excutorService = Executors.newFixedThreadPool(fileProperties.size());
        for (FileProperties filePropertie : fileProperties) {
            AppCallable callable = new AppCallable(filePropertie,"encrypt");
            FutureTask<EncryptFile> result = (FutureTask<EncryptFile>) excutorService.submit(callable);
            tasks.add(result);
        }
        for (FutureTask<EncryptFile> futureTask : tasks) {
            try{
                EncryptFile encryptRe = futureTask.get();
                encryptFiles.add(encryptRe);
            }catch(Exception e){
                logger.severe("线程错误");
            }finally{
                excutorService.shutdown();
            }
        }
        excutorService.shutdown();
        IOUtils.wirteFiles(JSON.toJSONString(encryptFiles), new File("data_encrypt.json"));
        System.out.println("加密完成，RSA公私钥和AES密钥加密文本都安置在程序根目录。");
    }
     /**
     * 解密文件的方法，这里是建立多线程并执行的步
     * @param fileProperties HashMap类型，分开存储密钥和待解密文件的对象
     */
    @Override
    public void decryptFile(HashMap<String, FileProperties> fileProperties) {
        FileProperties file = fileProperties.get("file");
        FileProperties key = fileProperties.get("prvKey");
        List<EncryptFile> encryptFiles = new ArrayList<>();
        List<FutureTask<EncryptFile>> tasks = new ArrayList<>();
        List<EncryptFile> readObject = JSONObject.parseArray(file.getFileContent(), EncryptFile.class);
        excutorService = Executors.newFixedThreadPool(readObject.size());
        for (EncryptFile encryptFile : readObject) {
            AppCallable callable = new AppCallable(key,encryptFile,"decrypt");
            FutureTask<EncryptFile> result = (FutureTask<EncryptFile>) excutorService.submit(callable);
            tasks.add(result);
        }
        for (FutureTask<EncryptFile> futureTask : tasks) {
            try{
                EncryptFile encryptRe = futureTask.get();
                encryptFiles.add(encryptRe);
            }catch(Exception e){
                logger.severe("线程错误");
            } finally{
                excutorService.shutdown();
            }
        }
        excutorService.shutdown();
        System.out.println("解密完成，源文件都安置在程序根目录。");
    }
}
