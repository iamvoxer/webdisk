package d1.project.docsmgr.view;

import d1.project.docsmgr.service.RoleService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class MainToolbar extends BorderPane {
    private double buttonHeight = 20;
    private double buttonWidth = 20;
    private Scene scene;

    public MainToolbar() {
        this.setPadding(new Insets(5, 5, 5, 5)); //节点到边缘的距离
        this.setLeft(getConfigButton());
        // this.setStyle("-fx-background-color: #336699;"); //背景色
        this.setRight(getHelpButton());

    }

    private Button getHelpButton() {
        Button button = new Button("重设密码");
        button.setTooltip(new Tooltip("重设密码"));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                new ChangePasswordDialog(RoleService.getInstance().getUserId());
            }
        });
        return button;
    }

    //    private Button getExitButton() {
//        Button exitButton = new Button("退出");
//        exitButton.setTooltip(new Tooltip("退出"));
//        exitButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                System.exit(1);
//            }
//        });
//        return exitButton;
//    }
    private Button getConfigButton() {
        Button exitButton = new Button("配置");
        if (!RoleService.getInstance().isAdmin()) {
            exitButton.setVisible(false);
        }
        exitButton.setTooltip(new Tooltip("配置人员及权限"));
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                EditConfigDialog dialog = new EditConfigDialog(scene);
                dialog.showAndWait();
            }
        });
        return exitButton;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
