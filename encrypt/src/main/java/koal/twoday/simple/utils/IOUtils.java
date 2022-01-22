package koal.twoday.simple.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
public class IOUtils {
    /**
     * 写文件通用方法
     * @param data 写入数据
     * @param file 写入的File
     * @throws IOException
     */
    public static void wirteFiles(String data, File file) throws IOException{
        OutputStreamWriter oWriter = null;
        oWriter = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
        oWriter.append(data);
        oWriter.flush();
        oWriter.close();
    }
    /**
     * 读文件通用方法
     * @param file 读入文件
     * @return
     * @throws IOException
     */
    public static String readFile(File file) throws IOException{
        InputStream in = null;
        ByteArrayOutputStream out = null;
        String readData = "";
        in = new FileInputStream(file);
        out = new ByteArrayOutputStream();
        int len = 0;
        byte[] date = new byte[1024];
        while((len = in.read(date)) != -1){
            out.write(date, 0, len);
        }
        byte[] bytes = out.toByteArray();
        readData = new String(bytes,"UTF-8");
        in.close();
        out.close();
        return readData;
    }
}
