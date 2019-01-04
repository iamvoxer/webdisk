package d1.project.docsmgr.view;

import d1.project.docsmgr.model.LocalFile;
import d1.project.docsmgr.service.EventService;
import d1.project.docsmgr.service.ICallbackObject;
import d1.project.docsmgr.service.LocalFileService;
import d1.project.docsmgr.util.Constant;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.io.File;

public class LocalFilePane extends BorderPane implements ICallbackObject {

    private TableView<LocalFile> table;

    public LocalFilePane() {
        EventService.getInstance().on(Constant.LOCAL_CURRENT_FOLDER_CHANGE, this);

        LocalFileToolbar toolbar = new LocalFileToolbar();
        this.setTop(toolbar);

        TableView table = getTableView();
        this.setCenter(table);
    }

    private TableView getTableView() {
        table = new TableView<LocalFile>();
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
        table.setItems(FXCollections.observableList(LocalFileService.getInstance().getFiles()));
        table.setRowFactory(new Callback<TableView<LocalFile>, TableRow<LocalFile>>() {
            @Override
            public TableRow<LocalFile> call(TableView<LocalFile> tv) {
                TableRow<LocalFile> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (row.isEmpty()) return;
                    LocalFile current = row.getItem();
                    if (event.getClickCount() == 2) {
                        if (current.getFile() == null || current.getFile().isFile())
                            return;
                        EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_CHANGE, current.getFile());
                    } else if (event.getClickCount() == 1) {
                        EventService.getInstance().fire(Constant.LOCAL_FILE_SELECTED, current.getFile());
                    }

                });
                return row;
            }
        });
        return table;
    }

    @Override
    public void invoke(Integer event, Object obj) {
        if (event == Constant.LOCAL_CURRENT_FOLDER_CHANGE) {
            File file = (File) obj;
            String path = file.getAbsolutePath();
            table.setItems(FXCollections.observableList(LocalFileService.getInstance().getFiles(path)));
            EventService.getInstance().fire(Constant.LOCAL_CURRENT_FOLDER_NAME_CHANGE, path);
            EventService.getInstance().fire(Constant.LOCAL_FILE_SELECTED, null);
        }
    }
}
