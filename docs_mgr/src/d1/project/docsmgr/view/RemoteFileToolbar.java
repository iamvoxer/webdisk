package d1.project.docsmgr.view;

import d1.project.docsmgr.model.AliOSSAuth;
import d1.project.docsmgr.model.AliOSSFile;
import d1.project.docsmgr.service.AliOSSServie;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.ICallbackObject;
import d1.project.docsmgr.service.RoleService;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class RemoteFileToolbar extends BorderPane implements ICallbackObject {

    private TextField folderNameLabel;
    private Button deleteButon;
    private AliOSSFile currentSelectedFile;
    private Scene scene;

    public RemoteFileToolbar() {
        EventService.getInstance().on(Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE, this);
        EventService.getInstance().on(Constant.REMOTE_FILE_SELECTED, this);

        this.setPadding(new Insets(5, 5, 5, 5)); //节点到边缘的距离
        this.setLeft(getUpButton());
        this.setCenter(getCurrentFolderLabel());
        this.setRight(getToolbarHbox());

    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private HBox getToolbarHbox() {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(getNewFolderButton(), getDeleteButton(), getRefreshButton());
        return hBox;
    }

    private Button getNewFolderButton() {
        Button button = new Button("新建");
        button.setTooltip(new Tooltip("新建目录"));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String currentFolderName = folderNameLabel.getText();
                if ("".equals(currentFolderName)) {
                    UIHelper.showErrorDialog(this.getClass(), "根目录不允许创建新目录!");
                    return;
                }
                if (!RoleService.getInstance().verifyNewRole(currentFolderName)) {
                    UIHelper.showErrorDialog(this.getClass(), "你没有删除这个目录的权限!");
                    return;
                }
                TextInputDialog dialog = new TextInputDialog("新建文件夹");
                dialog.setTitle("新建文件夹");
                dialog.setHeaderText("在目录" + currentFolderName + "下创建文件夹");
                dialog.setContentText("文件夹名称:");

                UIHelper.setDialogLogo(this.getClass(), dialog);
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String name = result.get();
                    if (name == null || name.isEmpty())
                        return;
                    scene.setCursor(Cursor.WAIT);
                    Task task = new Task() {
                        @Override
                        protected Integer call() throws Exception {
                            try {
                                AliOSSAuth auth = RoleService.getInstance().getAuth(currentFolderName);
                                AliOSSServie.getInstance().newFolder(auth, currentFolderName, name);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                MiscHelper.writeErrorLog(e1);
                            } finally {
                                Platform.runLater(() -> {
                                    scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                                });
                                EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, currentFolderName);
                            }
                            return 0;
                        }
                    };
                    Thread th = new Thread(task);
                    th.setDaemon(true);
                    th.start();
                }
            }
        });
        return button;
    }

    private Button getDeleteButton() {
        deleteButon = new Button("删除");
        deleteButon.setDisable(true);
        deleteButon.setTooltip(new Tooltip("删除文件或目录"));
        deleteButon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String path = currentSelectedFile.getPath();
                if (!RoleService.getInstance().verifyDelRole(path)) {
                    UIHelper.showErrorDialog(this.getClass(), "你没有删除这个目录的权限!");
                    return;
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确认删除");
                alert.setHeaderText(path);
                UIHelper.setDialogLogo(this.getClass(), alert);
                if (currentSelectedFile.isDirectory())
                    alert.setContentText("确定删除这个目录?");
                else
                    alert.setContentText("确定删除这个文件?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    if (currentSelectedFile.isDirectory()) {
                        DelProgressDialog delProgressDialog = new DelProgressDialog(path, folderNameLabel.getText());
                        delProgressDialog.showAndWait();
                    } else {
                        scene.setCursor(Cursor.WAIT); //Change cursor to wait style
                        Task task = new Task() {
                            @Override
                            protected Integer call() throws Exception {
                                AliOSSServie.getInstance().delFile(RoleService.getInstance().getAuth(path), path);
                                Platform.runLater(() -> {
                                    scene.setCursor(Cursor.DEFAULT); //Change cursor to default styleFOD
                                });
                                EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, folderNameLabel.getText());
                                return 0;
                            }
                        };
                        Thread th = new Thread(task);
                        th.setDaemon(true);
                        th.start();
                    }
                }
            }
        });
        return deleteButon;
    }

    private Button getRefreshButton() {
        Button button = new Button("刷新");
        button.setTooltip(new Tooltip("刷新当前目录"));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, folderNameLabel.getText());
            }
        });
        return button;
    }

    private Button getUpButton() {
        Button button = new Button("..");
        button.setTooltip(new Tooltip("返回上级目录"));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String current = folderNameLabel.getText();
                if (current.indexOf("/") < 0)
                    return;
                current = current.substring(0, current.length() - 1);
                System.out.println(current.substring(0, current.lastIndexOf("/") + 1));
                EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, current.substring(0, current.lastIndexOf("/") + 1));
            }
        });
        return button;
    }

    private TextField getCurrentFolderLabel() {
        folderNameLabel = new TextField();
        folderNameLabel.setEditable(false);
        return folderNameLabel;
    }

    @Override
    public void invoke(Integer event, Object obj) {
        if (event == Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE)
            folderNameLabel.setText(obj.toString());
        else if (event == Constant.REMOTE_FILE_SELECTED) {
            deleteButon.setDisable(obj == null);
            currentSelectedFile = (AliOSSFile) obj;
        }
    }
}
