package d1.project.docsmgr.view;


import com.alibaba.fastjson.JSONObject;
import d1.project.docsmgr.util.Constant;
import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;

public class ChangePasswordDialog extends Dialog<Pair<String, String>> {
    public ChangePasswordDialog(String username) {
        // Create the custom 
        setTitle("重设密码");
        setHeaderText("密码太简单，请重新设置你的密码");

        UIHelper.setDialogLogo(this.getClass(), this);

        // Set the button types.
        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstInput = new TextField();
        TextField secondInput = new TextField();

        grid.add(new Label("输入新密码:"), 0, 0);
        grid.add(firstInput, 1, 0);
        grid.add(new Label("再次输入:"), 0, 1);
        grid.add(secondInput, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node saveButton = getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        secondInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().equals(firstInput.getText().trim()) && !newValue.trim().equals("123456"))
                saveButton.setDisable(false);
            else
                saveButton.setDisable(true);
        });
        firstInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().equals(secondInput.getText().trim()) && !newValue.trim().equals("123456"))
                saveButton.setDisable(false);
            else
                saveButton.setDisable(true);
        });

        getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> firstInput.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Pair<>(firstInput.getText(), secondInput.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = showAndWait();

        result.ifPresent(usernamePassword -> {
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
            String md5Password = DigestUtils.md5Hex(usernamePassword.getKey());
            String url = Constant.UPDATE_PWD_URL + "?username=" + username + "&password=" + md5Password;
            try {
                String resultGet = MiscHelper.doGet(url);
                if (resultGet == null) throw new Exception("未知错误");
                JSONObject object = JSONObject.parseObject(resultGet);
                String code = object.getString("code");
                if (!code.equals("1")) throw new Exception(object.getString("data"));
            } catch (Exception e) {
                UIHelper.showErrorDialog(this.getClass(), e.getMessage());
                MiscHelper.writeErrorLog(e);
            }

        });
    }
}
