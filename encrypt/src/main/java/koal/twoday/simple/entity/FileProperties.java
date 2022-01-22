package koal.twoday.simple.entity;

//文件属性类
public class FileProperties {
    private String fileName;
    private String fileContent;
    public String getFileName() {
        return fileName;
    }
    public String getFileContent() {
        return fileContent;
    }
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
