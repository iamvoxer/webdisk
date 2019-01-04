package d1.project.docsmgr.service;

import d1.project.docsmgr.model.AliOSSAuth;
import d1.project.docsmgr.model.UserFolder;
import d1.project.docsmgr.model.UserRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleService {
    private static RoleService instance;
    private UserRole currentUserRole;
    private Map<String, AliOSSAuth> auths = new HashMap<String, AliOSSAuth>();
    private Map<String, String> roles = new HashMap<String, String>();

    private RoleService() {
    }

    public static RoleService getInstance() {
        if (instance == null)
            instance = new RoleService();
        return instance;
    }

    public AliOSSAuth getAuth(String path) {
        if (path.indexOf("/") > 0)
            path = path.substring(0, path.indexOf("/"));
        return auths.get(path);
    }

    public UserRole getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(UserRole currentUserRole) {
        this.currentUserRole = currentUserRole;
        auths.clear();
        roles.clear();
        List<UserFolder> folders = currentUserRole.getRoles();
        for (UserFolder folder : folders) {
            auths.put(folder.getFolder(), folder.getAliOSSAuth());
            roles.put(folder.getFolder(), folder.getRole());
        }
    }

    public boolean verifyDelRole(String path) {
        return verifyRole(path, 1);
    }

    public boolean verifyNewRole(String path) {
        return verifyRole(path, 0);
    }

    public boolean verifyUploadRole(String path) {
        return verifyRole(path, 2);
    }

    public boolean verifyDownloadRole(String path) {
        return verifyRole(path, 3);
    }

    private boolean verifyRole(String path, int position) {
        if (path.indexOf("/") > 0)
            path = path.substring(0, path.indexOf("/"));
        if (!roles.containsKey(path)) return false;
        String role = roles.get(path);
        if (role.length() > 1 && role.charAt(position) == '1')
            return true;
        return false;
    }

    public boolean isAdmin() {
        if (currentUserRole.getId().equals("admin"))
            return true;
        return false;
    }

    public String getUserId() {
        return currentUserRole.getId();
    }
}