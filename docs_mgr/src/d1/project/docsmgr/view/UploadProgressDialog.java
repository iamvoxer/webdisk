package d1.project.docsmgr.view;

import d1.project.docsmgr.service.AliOSSServie;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.RoleService;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadProgressDialog extends Dialog {
    public UploadProgressDialog(List<String> folders, List<String> files, String localCurrentFolder, String remoteCurrentFolder) {
        setTitle("上传进度");
        setHeaderText("获取所有上传文件列表...");

        GridPane gridPane = new GridPane();
        Label contentLabel = new Label();
        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(600);
        Button link = new Button("有错误，点击查看日志");
        link.setVisible(false);
        gridPane.add(contentLabel, 0, 0);
        gridPane.add(bar, 0, 1);
        gridPane.add(link, 0, 2);
        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File(MiscHelper.getLogName()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        getDialogPane().setContent(gridPane);

        ButtonType buttonTypeCancel = new ButtonType("上传中...", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().setAll(buttonTypeCancel);

        Node okButton = getDialogPane().lookupButton(buttonTypeCancel);
        okButton.setDisable(true);
        UIHelper.setDialogLogo(this.getClass(), this);

        if (!localCurrentFolder.endsWith(File.separator))
            localCurrentFolder = localCurrentFolder + File.separator;
        String finalLocalCurrentFolder = localCurrentFolder;
        setHeaderText("上传文件(0/" + files.size() + ")...");
        Task task = new Task() {
            @Override
            protected Integer call() throws Exception {
                try {
                    for (int i = 0; i < folders.size(); i++) {
                        String folder = folders.get(i);
                        final int finalI = i;
                        Platform.runLater(() -> {
                            setHeaderText("云端创建文件夹(" + (finalI + 1) + "/" + files.size() + ")...");
                            contentLabel.setText(String.valueOf(folder));
                        });
                        String key = folder.replace(finalLocalCurrentFolder, remoteCurrentFolder);
                        key = key.replace("\\", "/");
                        AliOSSServie.getInstance().uploadFolder(RoleService.getInstance().getAuth(remoteCurrentFolder), key);
                    }
                    for (int i = 0; i < files.size(); i++) {
                        String file = files.get(i);
                        final int finalI = i;
                        Platform.runLater(() -> {
                            setHeaderText("上传文件(" + (finalI + 1) + "/" + files.size() + ")");
                            contentLabel.setText(String.valueOf(file) + "(" + MiscHelper.getDataSize(new File(file).length()) + ")");
                        });
                        String key = file.replace(finalLocalCurrentFolder, remoteCurrentFolder);
                        key = key.replace("\\", "/");
                        AliOSSServie.getInstance().uploadFileWithPart(RoleService.getInstance().getAuth(remoteCurrentFolder), file, key, bar, RoleService.getInstance().verifyUploadRole(key));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MiscHelper.writeErrorLog(e1);
                    Platform.runLater(() -> {
                        link.setVisible(true);
                    });
                } finally {
                    Platform.runLater(() -> {
                        okButton.setDisable(false);
                        if (okButton instanceof Button)
                            ((Button) okButton).setText("退出");
                        setHeaderText("上传文件结束，请退出");
                        bar.setVisible(false);
                        EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, remoteCurrentFolder);
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
