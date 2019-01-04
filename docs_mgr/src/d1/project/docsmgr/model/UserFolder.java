package d1.project.docsmgr.model;

public class UserFolder {
    private String folder;
    private String role;
    private String key;
    private AliOSSAuth auths;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AliOSSAuth getAliOSSAuth() {
        if (auths == null) {
            String[] strings = key.split("::");
            auths = new AliOSSAuth();
            auths.setAccessKeyId(strings[0]);
            auths.setAccessKeySerect(strings[1]);
            auths.setBucket(strings[2]);
            auths.setEndPoint(strings[3]);
            auths.setUrl(strings[4]);
        }
        return auths;
    }
}
/*
"folder":"项目管理",
"role":"0001",
"key":"ktyt10YCQ8HInsQrRlRzdJZXWoRRVRY1CRNPmvDY:V-DH8fMczHyCnT5EOAF6RIs3JJVvMNjuBlFyM3gW"
 */