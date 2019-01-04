package d1.project.docsmgr.util;

public class Constant {
    public static Integer LOCAL_CURRENT_FOLDER_NAME_CHANGE = 0;
    public static Integer LOCAL_CURRENT_FOLDER_CHANGE = 1;

    public static Integer REMOTE_CURRENT_FOLDER_NAME_CHANGE = 2;
    public static Integer REMOTE_CURRENT_FOLDER_CHANGE = 3;

    public static Integer LOCAL_FILE_SELECTED = 4;
    public static Integer REMOTE_FILE_SELECTED = 5;

    public static String VERSION = "1.0.5";

    public static String URL_ROOT = "http://你的服务端地址/";
    public static String LOGIN_URL = URL_ROOT + "user/signIn";
    public static String VERSION_URL = URL_ROOT + "user/getVersion";
    public static String GET_CONFIG_URL = URL_ROOT + "user/getConfig";
    public static String UPDATE_CONFIG_URL = URL_ROOT + "user/updateConfig";
    public static String UPDATE_PWD_URL = URL_ROOT + "user/setPassword";
}
