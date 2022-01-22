package koal.twoday.simple.interfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import koal.twoday.simple.entity.FileProperties;

// 文件操作的接口
public interface IFileOperation {
    public void saveKeyAndFile(List<FileProperties> fileProperties)  throws IOException;
    public void decryptFile(HashMap<String, FileProperties> fileMap);
}
