package d1.project.docsmgr.view;

import d1.project.docsmgr.Main;
import d1.project.docsmgr.util.Constant;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane pane = new BorderPane();

        MainToolbar toolbar = new MainToolbar();
        pane.setTop(toolbar);

        GridPane centerPane = new GridPane();
        pane.setCenter(centerPane);
        centerPane.setStyle("-fx-border-color: black");

        UpDownLoadPane uploadDownloadPane = new UpDownLoadPane();
        uploadDownloadPane.setPrefWidth(100);
        centerPane.add(uploadDownloadPane, 10, 0, 1, 1);

        LocalFilePane localFilePane = new LocalFilePane();
        centerPane.add(localFilePane, 0, 0, 10, 1);

        RemoteFilePane remoteFilePane = new RemoteFilePane();
        centerPane.add(remoteFilePane, 11, 0, 10, 1);

        Scene scene = new Scene(pane, 1024, 600);
        remoteFilePane.setScene(scene);
        toolbar.setScene(scene);
        uploadDownloadPane.prefHeightProperty().bind(pane.heightProperty());
        localFilePane.prefWidthProperty().bind(pane.widthProperty().subtract(100).divide(2));
        remoteFilePane.prefWidthProperty().bind(pane.widthProperty().subtract(100).divide(2));

        stage.setScene(scene);
        stage.setTitle("企业网盘" + Constant.VERSION);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/resource/logo.png")));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(1);
            }
        });
    }
}
