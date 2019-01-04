package d1.project.docsmgr.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class EditConfigDialog extends Dialog {
    private Scene scene;

    public EditConfigDialog(Scene scene) {
        this.scene = scene;
        setTitle("用户配置");
        setHeaderText("修改版本及用户权限");
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(buttonTypeCancel);

        UIHelper.setDialogLogo(this.getClass(), this);

        BorderPane pan = new BorderPane();

        HBox box = new HBox();
        box.setSpacing(10);
        box.setPadding(new Insets(5, 5, 5, 5));
        TextField versionTextField = new TextField();
        Button saveButton = new Button("保存所有");
        box.getChildren().addAll(new Label("版本号:"), versionTextField, saveButton);
        pan.setTop(box);

        GridPane centerPane = new GridPane();
        TextArea userArea = new TextArea();
        userArea.setPrefHeight(600);
        TextArea groupArea = new TextArea();
        centerPane.add(userArea, 0, 0);
        centerPane.add(groupArea, 1, 0);
        pan.setCenter(centerPane);

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (versionTextField.getText().isEmpty()) {
                    showWarningDialog("版本号不能为空");
                    return;
                }
                try {
                    JSONArray.parseArray(userArea.getText());
                } catch (Exception ex) {
                    showWarningDialog("左边配置的内容不是标准的JSON");
                    MiscHelper.writeErrorLog(ex);
                    return;
                }
                try {
                    JSONArray.parseArray(groupArea.getText());
                } catch (Exception ex) {
                    showWarningDialog("右边配置的内容不是标准的JSON");
                    MiscHelper.writeErrorLog(ex);
                    return;
                }
                scene.setCursor(Cursor.WAIT); //Change cursor to default style
                Task task = new Task() {
                    @Override
                    protected Integer call() throws Exception {
                        try {
                            String url = Constant.UPDATE_CONFIG_URL;
                            JSONObject object = new JSONObject();
                            object.put("version", versionTextField.getText());
                            object.put("user", userArea.getText());
                            object.put("group", groupArea.getText());
                            String result = MiscHelper.doPostWithFormString(url, object.toJSONString());
                            if (result == null) throw new Exception("未知错误");
                            JSONObject parseObject = JSONObject.parseObject(result);
                            String code = parseObject.getString("code");
                            if (!code.equals("1")) throw new Exception(parseObject.getString("data"));
                        } catch (Exception e1) {
                            showErrorDialog(e1.getMessage());
                            MiscHelper.writeErrorLog(e1);
                        } finally {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                                }
                            });
                        }
                        return 0;
                    }
                };
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });

        getDialogPane().setContent(pan);
        scene.setCursor(Cursor.WAIT);
        Task task = new Task() {
            @Override
            protected Integer call() throws Exception {
                try {
                    String url = Constant.GET_CONFIG_URL;
                    String result = MiscHelper.doGet(url);
                    if (result == null) throw new Exception("未知错误");
                    JSONObject object = JSONObject.parseObject(result);
                    String code = object.getString("code");
                    if (!code.equals("1")) throw new Exception(object.getString("data"));
                    String version = object.getString("version");
                    JSONArray user = object.getJSONArray("user");
                    JSONArray group = object.getJSONArray("group");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            versionTextField.setText(version);
                            userArea.setText(JSONObject.toJSONString(user, SerializerFeature.PrettyFormat));
                            groupArea.setText(JSONObject.toJSONString(group, SerializerFeature.PrettyFormat));
                        }
                    });
                } catch (Exception e1) {
                    showErrorDialog(e1.getMessage()); //Change cursor to default style
                    MiscHelper.writeErrorLog(e1);
                } finally {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                        }
                    });
                }
                return 0;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void showErrorDialog(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                UIHelper.showErrorDialog(this.getClass(), text);
            }
        });
    }

    private void showWarningDialog(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                UIHelper.showWarningDialog(this.getClass(), text);
            }
        });
    }
}
