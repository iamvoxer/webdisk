package d1.project.docsmgr;

import com.alibaba.fastjson.JSONObject;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import d1.project.docsmgr.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("企业网盘");
        primaryStage.setScene(new Scene(new LoginView(primaryStage), 300, 200));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/resource/logo.png")));
        primaryStage.show();
        checkVersion();
    }

    private void checkVersion() throws Exception {
        String url = Constant.VERSION_URL;
        String result = MiscHelper.doGet(url);
        JSONObject object = JSONObject.parseObject(result);
        String code = object.getString("code");
        if (code.equals("1")) {
            String version = object.getString("version").trim();
            if (!version.equals(Constant.VERSION)) {
                UIHelper.showWarningDialog(this.getClass(), "当前版本:" + Constant.VERSION + ",最新版本:" + version + ",请在内部共享里找到最新包升级");
            }
        }
    }

}
