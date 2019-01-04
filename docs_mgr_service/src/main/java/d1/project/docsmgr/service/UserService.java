package d1.project.docsmgr.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class UserService {
    private static String EXTERNAL_GROUP_JSON = "./config/group.json";
    private static String EXTERNAL_USER_JSON = "./config/user.json";
    private static String EXTERNAL_VERSION = "./config/version";

    public JSONObject signIn(String username, String password) throws Exception {
        String groupid = verifyUser(username, password);
        return verifyGroup(groupid);
    }

    public JSONArray getUserJson() throws IOException {
        return getJSONArrayFromFile(EXTERNAL_USER_JSON);
    }

    public JSONArray getGroupJson() throws IOException {
        return getJSONArrayFromFile(EXTERNAL_GROUP_JSON);
    }

    public String getVersion() throws IOException {
        return FileUtils.readFileToString(new File(EXTERNAL_VERSION), Charset.forName("UTF-8"));
    }

    public void updateJson(String version, String user, String group) throws IOException {
        synchronized (this) {
            FileUtils.writeStringToFile(new File(EXTERNAL_VERSION), version, Charset.forName("UTF-8"));
            FileUtils.writeStringToFile(new File(EXTERNAL_USER_JSON), user, Charset.forName("UTF-8"));
            FileUtils.writeStringToFile(new File(EXTERNAL_GROUP_JSON), group, Charset.forName("UTF-8"));
        }
    }

    private String verifyUser(String username, String password) throws Exception {
        JSONArray users = getJSONArrayFromFile(EXTERNAL_USER_JSON);
        boolean isExist = false;
        String group = "";
        for (int i = 0; i < users.size(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("id").equals(username)) {
                isExist = true;
                if (!user.getString("pwd").equals(password))
                    throw new Exception("密码不对");
                group = user.getString("group");
                break;
            }
        }
        if (!isExist) {
            throw new Exception("用户" + username + "不存在");
        }
        return group;
    }

    private JSONObject verifyGroup(String groupid) throws Exception {
        JSONArray groups = getJSONArrayFromFile(EXTERNAL_GROUP_JSON);
        for (int i = 0; i < groups.size(); i++) {
            JSONObject group = groups.getJSONObject(i);
            if (group.getString("id").equals(groupid)) {
                return group;
            }
        }
        throw new Exception("当前用户所属组不存在");
    }

    private JSONArray getJSONArrayFromFile(String jsonFile) throws IOException {
        String jsonData = "[]";
        File externalFile = new File(jsonFile);
        if (externalFile.exists())
            jsonData = FileUtils.readFileToString(externalFile, Charset.forName("UTF-8"));
        return JSONArray.parseArray(jsonData);
    }

    public void setPassword(String username, String password) throws Exception {
        JSONArray users = getJSONArrayFromFile(EXTERNAL_USER_JSON);
        boolean isExist = false;
        for (int i = 0; i < users.size(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("id").equals(username)) {
                isExist = true;
                user.put("pwd", password);
                break;
            }
        }
        if (!isExist) {
            throw new Exception("用户" + username + "不存在");
        }
        synchronized (this) {
            FileUtils.writeStringToFile(new File(EXTERNAL_USER_JSON), users.toJSONString(), Charset.forName("UTF-8"));
        }
    }
}
