package d1.project.docsmgr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import d1.project.docsmgr.model.Config;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ConfigService implements ICallbackObject {
    private static ConfigService instance;
    private static String PWD_KEY = "kfi03igo2ppa9";
    private String configFile = System.getProperties().getProperty("user.home") + File.separator + ".yunliandoc" + File.separator + "config.json";
    private Config config;

    private ConfigService() {
        EventService.getInstance().on(Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE, this);
        String content = null;
        try {
            content = FileUtils.readFileToString(new File(configFile), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (content == null) {
            config = new Config();
        } else {
            config = JSONObject.parseObject(content, Config.class);
            if (config != null && config.getPassword() != null && config.getPassword().length() > 0) {
                config.setPassword(MiscHelper.decrypt(config.getPassword(), PWD_KEY));
            }
        }
    }

    public static ConfigService getInstance() {
        if (instance == null)
            instance = new ConfigService();
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    private void saveConfig() {
        try {
            synchronized (this) {
                FileUtils.writeStringToFile(new File(configFile), JSON.toJSONString(config), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(String username, String password, boolean isSave) {
        config.setSave(isSave);
        config.setUserName(username);
        config.setPassword(MiscHelper.encrypt(password, PWD_KEY));
        saveConfig();
    }

    @Override
    public void invoke(Integer event, Object obj) {
        if (event == Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE) {
            config.setLastOpenLocalfile(obj.toString());
            saveConfig();
        }
    }
}
