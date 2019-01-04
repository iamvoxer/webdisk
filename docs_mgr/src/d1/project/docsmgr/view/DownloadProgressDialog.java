package d1.project.docsmgr.view;

import d1.project.docsmgr.model.AliOSSFile;
import d1.project.docsmgr.service.AliOSSServie;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.RoleService;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadProgressDialog extends Dialog {
    public DownloadProgressDialog(AliOSSFile remoteFile, String localCurrentFolder, String remoteCurrentFolder) {
        setTitle("下载进度");
        setHeaderText("获取所有下载文件列表...");

        GridPane gridPane = new GridPane();
        Label contentLabel = new Label();
        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(600);
        gridPane.add(contentLabel, 0, 0);
        gridPane.add(bar, 0, 1);

        getDialogPane().setContent(gridPane);

        ButtonType buttonTypeCancel = new ButtonType("上传中...", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().setAll(buttonTypeCancel);

        Node okButton = getDialogPane().lookupButton(buttonTypeCancel);
        okButton.setDisable(true);
        UIHelper.setDialogLogo(this.getClass(), this);

        if (!localCurrentFolder.endsWith(File.separator))
            localCurrentFolder = localCurrentFolder + File.separator;
        String finalLocalCurrentFolder = localCurrentFolder;

        List<String> files;
        String path = remoteFile.getPath();
        if (remoteFile.isDirectory()) {
            files = AliOSSServie.getInstance().listAllFilesStringInFolder(RoleService.getInstance().getAuth(path), path);
        } else {
            files = new ArrayList<String>();
            files.add(path);
        }

        setHeaderText("下载文件(0/" + files.size() + ")...");
        String finalLocalCurrentFolder1 = localCurrentFolder;
        Task task = new Task() {
            @Override
            protected Integer call() throws Exception {
                try {
                    for (int i = 0; i < files.size(); i++) {
                        String file = files.get(i);
                        final int finalI = i;
                        Platform.runLater(() -> {
                            setHeaderText("下载文件(" + (finalI + 1) + "/" + files.size() + ")");
                            contentLabel.setText(file + "--->" + finalLocalCurrentFolder1);
                        });
                        String loaclFile = file.replace(remoteCurrentFolder, finalLocalCurrentFolder);
                        loaclFile = loaclFile.replace("/", File.separator);
                        AliOSSServie.getInstance().downloadFileWithProgress(RoleService.getInstance().getAuth(remoteCurrentFolder), file, loaclFile, bar);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MiscHelper.writeErrorLog(e1);
                } finally {
                    Platform.runLater(() -> {
                        okButton.setDisable(false);
                        if (okButton instanceof Button)
                            ((Button) okButton).setText("退出");
                        setHeaderText("下载文件结束，请退出");
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
