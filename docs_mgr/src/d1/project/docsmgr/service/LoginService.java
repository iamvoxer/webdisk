package d1.project.docsmgr.service;

import com.alibaba.fastjson.JSONObject;
import d1.project.docsmgr.model.UserRole;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import org.apache.commons.codec.digest.DigestUtils;

public class LoginService {
    private static LoginService instance;

    private LoginService() {
    }

    public static LoginService getInstance() {
        if (instance == null)
            instance = new LoginService();
        return instance;
    }

    public void login(String username, String password, boolean isSave) throws Exception {
        if ("".equals(username) || "".equals(password)) throw new Exception("用户和密码都不能为空");
        String md5Password = DigestUtils.md5Hex(password);
        String url = Constant.LOGIN_URL + "?username=" + username + "&password=" + md5Password;
        String result = MiscHelper.doGet(url);
        if (result == null) throw new Exception("未知错误");
        JSONObject object = JSONObject.parseObject(result);
        String code = object.getString("code");
        if (!code.equals("1")) throw new Exception(object.getString("data"));
        RoleService.getInstance().setCurrentUserRole(JSONObject.parseObject(object.getString("data"), UserRole.class));

        ConfigService.getInstance().saveConfig(username, password, isSave);
    }
}
