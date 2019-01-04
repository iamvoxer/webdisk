package d1.project.docsmgr.service;

import d1.project.docsmgr.model.Config;
import d1.project.docsmgr.model.LocalFile;
import d1.project.docsmgr.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalFileService {
    private static LocalFileService instance;
    private String defaultFolder = System.getProperties().getProperty("user.home");

    private LocalFileService() {
        Config config = ConfigService.getInstance().getConfig();
        if (config != null && config.getLastOpenLocalfile() != null && !config.getLastOpenLocalfile().isEmpty())
            defaultFolder = config.getLastOpenLocalfile();
    }

    public static LocalFileService getInstance() {
        if (instance == null)
            instance = new LocalFileService();
        return instance;
    }

    public List<LocalFile> getFiles() {
        return getFiles(defaultFolder);
    }

    public List<LocalFile> getFiles(String path) {
        List<LocalFile> files = new ArrayList<LocalFile>();
        File[] lists = new File(path).listFiles();
        for (int i = 0; i < lists.length; i++) {
            files.add(new LocalFile(lists[i]));
        }
        EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE, path);
        return files;
    }
}
