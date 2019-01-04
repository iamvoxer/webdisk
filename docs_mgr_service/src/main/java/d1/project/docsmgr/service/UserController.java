package d1.project.docsmgr.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public JSONObject login(String username, String password) {
        JSONObject result = new JSONObject();

        try {
            JSONObject data = userService.signIn(username, password);
            result.put("code", "1");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", "0");
            result.put("data", e.getMessage());
            logger.error(username + ":" + password, e);
        }
        return result;
    }

    @RequestMapping(value = "/setPassword", method = RequestMethod.GET)
    public JSONObject setPassword(String username, String password) {
        JSONObject result = new JSONObject();

        try {
            userService.setPassword(username, password);
            result.put("code", "1");
        } catch (Exception e) {
            result.put("code", "0");
            result.put("data", e.getMessage());
            logger.error(username + ":" + password, e);
        }
        return result;
    }

    @RequestMapping(value = "/getVersion", method = RequestMethod.GET)
    public JSONObject getVersion() {
        JSONObject result = new JSONObject();

        try {
            String version = userService.getVersion();
            result.put("code", "1");
            result.put("version", version);
        } catch (Exception e) {
            result.put("code", "0");
            result.put("data", e.getMessage());
            logger.error("getVersion错误", e);
        }
        return result;
    }

    @RequestMapping(value = "/getConfig", method = RequestMethod.GET)
    public JSONObject getConfig() {
        JSONObject result = new JSONObject();

        try {
            JSONArray user = userService.getUserJson();
            JSONArray group = userService.getGroupJson();
            String version = userService.getVersion();
            result.put("code", "1");
            result.put("version", version);
            result.put("user", user);
            result.put("group", group);
        } catch (Exception e) {
            result.put("code", "0");
            result.put("data", e.getMessage());
            logger.error("getConfig错误", e);
        }
        return result;
    }

    @RequestMapping(value = "/updateConfig", method = RequestMethod.POST)
    public JSONObject updateConfig(@RequestBody Config config) {
        JSONObject result = new JSONObject();

        try {
            userService.updateJson(config.getVersion(), config.getUser(), config.getGroup());
            result.put("code", "1");
        } catch (Exception e) {
            result.put("code", "0");
            result.put("data", e.getMessage());
            logger.error("updateConfig错误", e);
        }
        return result;
    }
}
