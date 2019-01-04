package d1.project.docsmgr.view;

import d1.project.docsmgr.model.Config;
import d1.project.docsmgr.service.ConfigService;
import d1.project.docsmgr.service.LoginService;
import d1.project.docsmgr.util.MiscHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static javafx.geometry.HPos.RIGHT;

public class LoginView extends GridPane {

    private final Button loginbtn;

    public LoginView(Stage stage) {
        Config config = ConfigService.getInstance().getConfig();
        boolean isSave = config.isSave();

        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);

        Label userName = new Label("用户:");
        this.add(userName, 0, 1);

        TextField userTextField = new TextField();
        this.add(userTextField, 1, 1);
        if (isSave) userTextField.setText(config.getUserName());

        Label pw = new Label("密码:");
        this.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        this.add(pwBox, 1, 2);
        if (isSave) pwBox.setText(config.getPassword());

        CheckBox checkBox = new CheckBox("保存用户密码");
        this.add(checkBox, 0, 4, 2, 1);
        checkBox.setSelected(isSave);

        loginbtn = new Button("登录");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginbtn);
        this.add(hbBtn, 1, 4);


        final Text actiontarget = new Text();
        this.add(actiontarget, 0, 6);
        setColumnSpan(actiontarget, 2);
        setHalignment(actiontarget, RIGHT);
        pwBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    submit(userTextField, pwBox, stage, actiontarget, checkBox.isSelected());
                }
            }
        });
        loginbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                submit(userTextField, pwBox, stage, actiontarget, checkBox.isSelected());
            }
        });
    }

    private void submit(TextField userTextField, PasswordField pwBox, Stage stage, Text actiontarget, boolean isSave) {
        stage.getScene().setCursor(Cursor.WAIT);
        Task task = new Task() {
            @Override
            protected Integer call() throws Exception {
                try {
                    //Change cursor to wait style
                    LoginService.getInstance().login(userTextField.getText(), pwBox.getText(), isSave);
                    Platform.runLater(() -> {
                        if (pwBox.getText().equals("123456")) {
                            new ChangePasswordDialog(userTextField.getText());
                        }
                        //创建主界面窗口
                        try {
                            new MainView().start(new Stage());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            MiscHelper.writeErrorLog(e1);
                        }
                        //关闭登陆窗口
                        stage.hide();
                    });
                } catch (Exception e1) {
                    MiscHelper.writeErrorLog(e1);
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText(e1.getMessage());
                } finally {
                    stage.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
                }
                return 0;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

    }
}
