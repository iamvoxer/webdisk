package d1.project.docsmgr.view;

import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.stage.StageStyle;

import java.util.List;

public class ProgressDialog {
    public ProgressDialog(List<String> files) {
        Dialog dialog = new Dialog();
        dialog.setTitle("删除进度");
        dialog.setHeaderText("删除文件中...(" + files.size() + ")");

        ProgressBar bar = new ProgressBar();

        dialog.getDialogPane().setContent(bar);


        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.showAndWait();
    }

}
