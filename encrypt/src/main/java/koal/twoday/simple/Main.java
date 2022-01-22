package koal.twoday.simple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import koal.twoday.simple.entity.FileProperties;
import koal.twoday.simple.interfaces.IFileOperation;
import koal.twoday.simple.runtask.App;
import koal.twoday.simple.utils.IOUtils;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        JFrame frame = new JFrame();
        JFileChooser chooser = new JFileChooser();
        while (true) {
            System.out.println("请选择加密或解密，1为加密，2为解密：");
            String flag = in.next();
            if ("1".equals(flag)) {
                File[] selectFile = buildSelector("选择待加密的文件", frame, chooser, true);
                doEncryptFile(selectFile);
                break;
            } else if ("2".equals(flag)) {
                File keyFile = buildSelector("选择解密私钥", frame, chooser);
                File dataFile = buildSelector("选择待解密文件", frame, chooser);
                doDecryptFile(keyFile, dataFile);
                break;
            } else {
                System.out.println("输入选择有误");
                continue;
            }
        }
        in.close();
    }

    /**
     * 执行解密的步骤
     * 
     * @param selectFile JFileChooser选择的文件列表
     */
    private static void doEncryptFile(File[] selectFile) {
        IFileOperation fileOperation = new App();
        List<FileProperties> fileProperties = new ArrayList<>();
        try {
            for (File file : selectFile) {
                System.out.println(file);
                String fileContent = IOUtils.readFile(file);
                FileProperties filePro = new FileProperties();
                filePro.setFileContent(fileContent);
                filePro.setFileName(file.getName());
                fileProperties.add(filePro);
            }
            fileOperation.saveKeyAndFile(fileProperties);
        } catch (IOException e) {
            logger.severe("操作文件发生错误,请检查路径是否正确!");
        } finally {
            System.exit(0);
        }
    }

    /**
     * 执行解密的步骤
     * 
     * @param keyFile  JFileChooser选择的私钥文件
     * @param dataFile JFileChooser选择的待解密的数据文件
     */
    private static void doDecryptFile(File keyFile, File dataFile) {
        IFileOperation fileOperation = new App();
        FileProperties fileProperties = new FileProperties();
        FileProperties keyFileProperties = new FileProperties();
        HashMap<String, FileProperties> fileMap = new HashMap<>();
        try {
            String keyFileData = IOUtils.readFile(keyFile);
            keyFileProperties.setFileContent(keyFileData);
            String fileData = IOUtils.readFile(dataFile);
            fileProperties.setFileContent(fileData);
            fileMap.put("prvKey", keyFileProperties);
            fileMap.put("file", fileProperties);
            fileOperation.decryptFile(fileMap);
            System.out.println("解密完成！解密结果连同源文件名保存在根目录下！");
        } catch (IOException e) {
            logger.severe("操作文件发生错误,请检查路径是否正确!");
        } finally {
            System.exit(0);
        }
    }

    // 以下是构建文件选择器的通用方法
    private static File buildSelector(String title, JFrame frame, JFileChooser chooser) {
        File dataFile = null;
        chooser.setDialogTitle(title);
        int flags = chooser.showOpenDialog(frame);
        if (flags == JFileChooser.APPROVE_OPTION) {
            dataFile = chooser.getSelectedFile();
        }
        return dataFile;
    }

    private static File[] buildSelector(String title, JFrame frame, JFileChooser chooser, Boolean MultiSelect) {
        File[] selectFile = null;
        chooser.setMultiSelectionEnabled(MultiSelect);
        chooser.setDialogTitle("请选择待加密的文件,可多选");
        int flags = chooser.showOpenDialog(frame);
        if (flags == JFileChooser.APPROVE_OPTION) {
            selectFile = chooser.getSelectedFiles();
        }
        return selectFile;
    }
}
