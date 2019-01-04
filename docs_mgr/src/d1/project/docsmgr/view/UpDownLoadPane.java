package d1.project.docsmgr.view;

import d1.project.docsmgr.model.AliOSSFile;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.ICallbackObject;
import d1.project.docsmgr.service.RoleService;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.UIHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UpDownLoadPane extends GridPane implements ICallbackObject {

    private final Button uploadButton;
    private final Button downloadButton;
    private File localSelectFile;
    private AliOSSFile remoteSelectFile;
    private String currentLocalFolder;
    private String currentRemoteFolder;

    public UpDownLoadPane() {
        EventService.getInstance().on(Constant.REMOTE_FILE_SELECTED, this);
        EventService.getInstance().on(Constant.LOCAL_FILE_SELECTED, this);
        EventService.getInstance().on(Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE, this);
        EventService.getInstance().on(Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE, this);

        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);

        uploadButton = new Button("===>");
        uploadButton.setDisable(true);
        this.add(uploadButton, 0, 0);
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (localSelectFile == null)
                    return;
                if (currentRemoteFolder == null || currentRemoteFolder.equals(""))
                    UIHelper.showErrorDialog(this.getClass(), "根目录不允许上传");
                if (!RoleService.getInstance().verifyNewRole(currentRemoteFolder)) {
                    UIHelper.showErrorDialog(this.getClass(), "你没有上传文件到这个目录的权限!");
                    return;
                }
                List<String> files = new ArrayList<String>();
                List<String> folders = new ArrayList<String>();
                if (localSelectFile.isFile())
                    files.add(localSelectFile.getAbsolutePath());
                else {
                    Collection<File> temps = FileUtils.listFiles(localSelectFile, null, true);
                    for (File temp : temps) {
                        if (temp.isFile())
                            files.add(temp.getAbsolutePath());
                        if (!folders.contains(temp.getParent() + File.separator))
                            folders.add(temp.getParent() + File.separator);
                    }
                }
                UploadProgressDialog dialog = new UploadProgressDialog(folders, files, currentLocalFolder, currentRemoteFolder);
                dialog.showAndWait();
            }
        });

        downloadButton = new Button("<===");
        downloadButton.setDisable(true);
        this.add(downloadButton, 0, 4);
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (remoteSelectFile == null)
                    return;
                if (!RoleService.getInstance().verifyDownloadRole(currentRemoteFolder)) {
                    UIHelper.showErrorDialog(this.getClass(), "你没有下载这个目录下文件的权限!");
                    return;
                }
                DownloadProgressDialog dialog = new DownloadProgressDialog(remoteSelectFile, currentLocalFolder, currentRemoteFolder);
                dialog.showAndWait();
            }
        });
    }

    @Override
    public void invoke(Integer event, Object obj) {
        if (event.equals(Constant.LOCAL_FILE_SELECTED)) {
            uploadButton.setDisable(obj == null);
            localSelectFile = (File) obj;
        } else if (event.equals(Constant.REMOTE_FILE_SELECTED)) {
            downloadButton.setDisable(obj == null);
            remoteSelectFile = (AliOSSFile) obj;
        } else if (event.equals(Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE)) {
            currentLocalFolder = (String) obj;
        } else if (event.equals(Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE)) {
            currentRemoteFolder = (String) obj;
        }
    }
}
