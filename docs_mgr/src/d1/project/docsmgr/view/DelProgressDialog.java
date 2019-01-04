package d1.project.docsmgr.view;

import d1.project.docsmgr.model.AliOSSAuth;
import d1.project.docsmgr.service.AliOSSServie;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.RoleService;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.List;

public class DelProgressDialog extends Dialog {
    public DelProgressDialog(String path, String currentFolder) {
        setTitle("删除进度");
        setHeaderText("获取所有删除文件列表...");
        ButtonType buttonTypeCancel = new ButtonType("退出", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().setAll(buttonTypeCancel);

        Node okButton = getDialogPane().lookupButton(buttonTypeCancel);
        okButton.setDisable(true);
        UIHelper.setDialogLogo(this.getClass(), this);
        Task task = new Task() {
            @Override
            protected Integer call() throws Exception {
                try {
                    AliOSSAuth auth = RoleService.getInstance().getAuth(path);
                    List<String> files = AliOSSServie.getInstance().listAllFilesStringInFolder(auth, path);
                    Platform.runLater(() -> {
                        setHeaderText("删除文件(0/" + files.size() + ")...");
                    });
                    for (int i = 0; i < files.size(); i++) {
                        int finalI = i;
                        Platform.runLater(() -> {
                            setHeaderText("删除文件(" + (finalI + 1) + "/" + files.size() + ")...");
                            setContentText(files.get(finalI));
                        });
                        AliOSSServie.getInstance().delFile(auth, files.get(i));
                    }
                } catch (Exception e1) {
                    MiscHelper.writeErrorLog(e1);
                } finally {
                    Platform.runLater(() -> {
                        okButton.setDisable(false);
                        setHeaderText("全部删除结束，请退出");
                        EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, currentFolder);
                    });

                }
                return 0;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        //initStyle(StageStyle.UNDECORATED);
    }
}
