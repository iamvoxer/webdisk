package d1.project.docsmgr.view;

import d1.project.docsmgr.model.RemoteFile;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.ICallbackObject;
import d1.project.docsmgr.service.RemoteFileService;
import d1.project.docsmgr.util.Constant;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class RemoteFilePane extends BorderPane implements ICallbackObject {
    private final RemoteFileToolbar toolbar;
    private TableView<RemoteFile> table;
    private Scene scene;

    public RemoteFilePane() {
        EventService.getInstance().on(Constant.REMOTE_CURRENT_FOLDER_CHANGE, this);
        toolbar = new RemoteFileToolbar();
        this.setTop(toolbar);

        TableView table = getTableView();
        this.setCenter(table);
    }

    private TableView getTableView() {
        table = new TableView<RemoteFile>();
        TableColumn nameCol = new TableColumn("名称");
        nameCol.setMinWidth(200);
        TableColumn lastCol = new TableColumn("修改日期");
        TableColumn lengthCol = new TableColumn("大小");

        table.getColumns().addAll(nameCol, lastCol, lengthCol);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        lastCol.setCellValueFactory(
                new PropertyValueFactory<>("lastTime")
        );
        lengthCol.setCellValueFactory(
                new PropertyValueFactory<>("length")
        );
        table.setItems(FXCollections.observableList(RemoteFileService.getInstance().getFiles()));
        table.setRowFactory(new Callback<TableView<RemoteFile>, TableRow<RemoteFile>>() {
            @Override
            public TableRow<RemoteFile> call(TableView<RemoteFile> tv) {
                TableRow<RemoteFile> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (row.isEmpty()) return;
                    RemoteFile current = row.getItem();
                    if (event.getClickCount() == 2) {
                        if (current.getFile() == null || !current.getFile().isDirectory())
                            return;
                        EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_CHANGE, current.getFile().getPath());
                    } else if (event.getClickCount() == 1) {
                        EventService.getInstance().fire(Constant.REMOTE_FILE_SELECTED, current.getFile());
                    }
                });
                return row;
            }
        });
        return table;
    }

    @Override
    public void invoke(Integer event, Object obj) {
        if (event == Constant.REMOTE_CURRENT_FOLDER_CHANGE) {
            String path = obj.toString();
            Task task = new Task() {
                @Override
                protected Integer call() throws Exception {
                    scene.setCursor(Cursor.WAIT); //Change cursor to wait style
                    table.setItems(FXCollections.observableList(RemoteFileService.getInstance().getFiles(path)));
                    EventService.getInstance().fire(Constant.REMOTE_CURRENT_FOLDER_NAME_CHANGE, path);
                    scene.setCursor(Cursor.DEFAULT); //Change cursor to default style
                    EventService.getInstance().fire(Constant.REMOTE_FILE_SELECTED, null);
                    return 0;
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        toolbar.setScene(scene);
    }
}