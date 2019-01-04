package d1.project.docsmgr.model;

public class Config {
    private String userName;
    private String password;
    private boolean isSave;
    private String lastOpenLocalfile;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getLastOpenLocalfile() {
        return lastOpenLocalfile;
    }

    public void setLastOpenLocalfile(String lastOpenLocalfile) {
        this.lastOpenLocalfile = lastOpenLocalfile;
    }
}
