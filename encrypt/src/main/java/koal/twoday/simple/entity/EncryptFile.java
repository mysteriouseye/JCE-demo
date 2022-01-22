package koal.twoday.simple.entity;

import com.alibaba.fastjson.annotation.JSONField;

// 加密文件json对象类
public class EncryptFile {
    @JSONField(name = "Aes_Encrypt")
    private String aesEncrypt;

    @JSONField(name = "File_Encrypt")
    private String fileEncrypt;

    @JSONField(name = "File_Name")
    private String fileName;

    public EncryptFile(String aesEncrypt, String fileEncrypt, String fileName){
        this.aesEncrypt = aesEncrypt;
        this.fileEncrypt = fileEncrypt;
        this.fileName = fileName;
    }

    public String getFileName(){
        return this.fileName;
    }
    public String getAesEncrypt(){
        return this.aesEncrypt;
    }
    public String getFileEncrypt(){
        return this.fileEncrypt;
    }
}
