package d1.project.docsmgr.util;

import d1.project.docsmgr.model.AliOSSFile;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class UIHelper {
    public static void setDialogLogo(Class cl, Dialog dialog) {
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        // Add a custom icon.
        stage.getIcons().add(new Image(cl.getResource("/resource/logo.png").toString()));
    }

    public static void showErrorDialog(Class cl, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(text);
        setDialogLogo(cl, alert);
        alert.showAndWait();
    }

    public static void showWarningDialog(Class cl, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(text);
        setDialogLogo(cl, alert);
        alert.showAndWait();
    }

    public static Image getImageByFileExtension(Class cl, File file) {
        Image defaultImage = new Image(cl.getResourceAsStream("/resource/file.png"));
        if (file == null || !file.exists()) return defaultImage;
        if (file.isDirectory()) return new Image(cl.getResourceAsStream("/resource/folder.png"));
        String fileName = file.getName();
        return getImage(cl, defaultImage, fileName);
    }

    private static Image getImage(Class cl, Image defaultImage, String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) return defaultImage;
        String extension = fileName.substring(index + 1);
        switch (extension) {
            case "xls":
            case "xlsx":
                return new Image(cl.getResourceAsStream("/resource/excel.png"));
            case "doc":
            case "docx":
                return new Image(cl.getResourceAsStream("/resource/word.png"));
            case "ppt":
            case "pptx":
                return new Image(cl.getResourceAsStream("/resource/ppt.png"));
            case "mp4":
            case "avi":
                return new Image(cl.getResourceAsStream("/resource/movie.png"));
            case "txt":
            case "text":
                return new Image(cl.getResourceAsStream("/resource/text.png"));
            case "mp3":
                return new Image(cl.getResourceAsStream("/resource/music.png"));
            case "jpg":
            case "png":
            case "jpeg":
            case "ico":
                return new Image(cl.getResourceAsStream("/resource/image.png"));
            case "zip":
            case "rar":
            case "7z":
                return new Image(cl.getResourceAsStream("/resource/zip.png"));
            case "java":
                return new Image(cl.getResourceAsStream("/resource/java.png"));
            case "cs":
                return new Image(cl.getResourceAsStream("/resource/c#.png"));
            case "pdf":
                return new Image(cl.getResourceAsStream("/resource/pdf.png"));
            default:
                return defaultImage;
        }
    }

    public static Image getImageByAliOSSFileExtension(Class cl, AliOSSFile file) {
        Image defaultImage = new Image(cl.getResourceAsStream("/resource/file.png"));
        if (file == null) return defaultImage;
        if (file.isDirectory()) return new Image(cl.getResourceAsStream("/resource/folder.png"));
        String fileName = file.getName();
        return getImage(cl, defaultImage, fileName);
    }
}
