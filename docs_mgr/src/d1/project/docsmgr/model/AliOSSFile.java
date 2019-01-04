package d1.project.docsmgr.model;

import com.aliyun.oss.model.OSSObjectSummary;

import java.text.SimpleDateFormat;

public class AliOSSFile {
    private boolean isDirectory;
    private String name;
    private String lastTime;
    private long length;
    private String path;
    private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public AliOSSFile(String perfix, String folder) {
        this.path = folder;
        this.name = folder.substring(perfix.length());
        if (this.name.endsWith("/"))
            this.name = this.name.substring(0, this.name.length() - 1);
        isDirectory = true;
    }

    public AliOSSFile(String perfix, OSSObjectSummary file) {
        this.path = file.getKey();
        this.name = file.getKey().substring(perfix.length());
        this.lastTime = formater.format(file.getLastModified());
        this.length = file.getSize();
        isDirectory = false;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getName() {
        return name;
    }

    public String getLastTime() {
        return lastTime;
    }

    public long getLength() {
        return length;
    }
}
