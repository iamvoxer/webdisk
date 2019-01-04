package d1.project.docsmgr.service;

import d1.project.docsmgr.model.AliOSSFile;
import d1.project.docsmgr.model.RemoteFile;
import d1.project.docsmgr.model.UserFolder;
import d1.project.docsmgr.model.UserRole;
import d1.project.docsmgr.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class RemoteFileService {
    private static RemoteFileService instance;

    private RemoteFileService() {
    }

    public static RemoteFileService getInstance() {
        if (instance == null)
            instance = new RemoteFileService();
        return instance;
    }

    public List<RemoteFile> getFiles() {
        List<RemoteFile> files = new ArrayList<RemoteFile>();
        UserRole user = RoleService.getInstance().getCurrentUserRole();
        List<UserFolder> folders = user.getRoles();
        for (UserFolder folder : folders) {
            files.add(new RemoteFile(new AliOSSFile("", folder.getFolder() + "/")));
        }
        EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE, "");
        return files;
    }

    public List<RemoteFile> getFiles(String path) {
        if ("".equals(path))
            return getFiles();
        List<RemoteFile> files = new ArrayList<RemoteFile>();
        List<AliOSSFile> lists = AliOSSServie.getInstance().listFiles(path, RoleService.getInstance().getAuth(path));
        for (int i = 0; i < lists.size(); i++) {
            files.add(new RemoteFile(lists.get(i)));
        }
        EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE, path);
        return files;
    }
}
