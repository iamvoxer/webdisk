package d1.project.docsmgr.view;

import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.ICallbackObject;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.UIHelper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class LocalFileToolbar extends BorderPane implements ICallbackObject {

    private TextField folderNameLabel;
    private Button deleteButton;
    private File currentSelectedFile;

    public LocalFileToolbar() {
        EventService.getInstance().on(Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE, this);
        EventService.getInstance().on(Constant.LOCAL_FILE_SELECTED, this);

        this.setPadding(new Insets(5, 5, 5, 5)); //节点到边缘的距离
        this.setLeft(getUpButton());
        HBox centerBox = new HBox();
        centerBox.getChildren().addAll(getRoot(), getCurrentFolderLabel());
        this.setCenter(centerBox);
        this.setRight(getToolbarHbox());

    }

    private HBox getToolbarHbox() {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(getNewFolderButton(), getDeleteButton(), getRefreshButton());
        return hBox;
    }

    private ComboBox getRoot() {
        ComboBox box = new ComboBox();
        box.setPrefWidth(50);
        File[] files = File.listRoots();
        for (int i = 0; i < files.length; i++) {
            box.getItems().add(files[i].getAbsolutePath());
        }
        box.setOnAction((Event ev) -> {
            EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_CHANGE, new File(box.getSelectionModel().getSelectedItem().toString()));
        });

        return box;
    }

    private Button getNewFolderButton() {
        Button button = new Button("新建");
        button.setTooltip(new Tooltip("新建目录"));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                TextInputDialog dialog = new TextInputDialog("新建文件夹");
                dialog.setTitle("新建文件夹");
                dialog.setHeaderText("在目录" + folderNameLabel.getText() + "下创建文件夹");
                dialog.setContentText("文件夹名称:");

                UIHelper.setDialogLogo(this.getClass(), dialog);
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String name = result.get();
                    if (name == null || name.isEmpty())
                        return;
                    try {
                        File newFolder = new File(folderNameLabel.getText() + File.separator + name);
                        if (newFolder.exists()) {
                            UIHelper.showErrorDialog(this.getClass(), "目录已经存在");
                            return;
                        }
                        FileUtils.forceMkdir(newFolder);
                        EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_CHANGE, new File(folderNameLabel.getText()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        return button;
    }

    private Button getDeleteButton() {
        deleteButton = new Button("删除");
        deleteButton.setDisable(true);
        deleteButton.setTooltip(new Tooltip("删除文件或目录"));
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (currentSelectedFile == null) return;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确认删除");
                alert.setHeaderText(currentSelectedFile.getAbsolutePath());
                UIHelper.setDialogLogo(this.getClass(), alert);
                if (currentSelectedFile.isFile())
                    alert.setContentText("确定删除这个文件?");
                else
                    alert.setContentText("确定删除这个目录?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    try {
                        FileUtils.forceDelete(currentSelectedFile);
                        EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_CHANGE, new File(folderNameLabel.getText()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                }
            }
        });
        return deleteButton;
    }

    private Button getRefreshButton() {
        Button button = new Button("刷新");
        button.setTooltip(new Tooltip("刷新当前目录"));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_CHANGE, new File(folderNameLabel.getText()));
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
                File current = new File(folderNameLabel.getText());
                if (current.getParentFile() == null)
                    return;
                EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_CHANGE, current.getParentFile());
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
        if (event == Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE)
            folderNameLabel.setText(obj.toString());
        else if (event == Constant.LOCAL_FILE_SELECTED) {
            deleteButton.setDisable(obj == null);
            currentSelectedFile = (File) obj;
        }
    }
}
