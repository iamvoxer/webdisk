package d1.project.docsmgr.model;

import d1.project.docsmgr.util.MiscHelper;
import d1.project.docsmgr.util.UIHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RemoteFile {
    private final SimpleObjectProperty<Label> name;
    private final SimpleStringProperty lastTime;
    private final SimpleStringProperty length;
    private AliOSSFile file;

    public RemoteFile(AliOSSFile file) {
        this.file = file;
        Image image = UIHelper.getImageByAliOSSFileExtension(getClass(), file);
        Label label = new Label(file.getName(), new ImageView(image));
        this.name = new SimpleObjectProperty<Label>(label);
        if (file.isDirectory()) {
            this.lastTime = new SimpleStringProperty("");
            this.length = new SimpleStringProperty("");
        } else {
            this.lastTime = new SimpleStringProperty(file.getLastTime());
            this.length = new SimpleStringProperty(MiscHelper.getDataSize(file.getLength()));
        }
    }

    public AliOSSFile getFile() {
        return this.file;
    }

    public Label getName() {
        return name.get();
    }

    public void setName(Label name) {
        this.name.set(name);
    }

    public SimpleObjectProperty<Label> nameProperty() {
        return name;
    }

    public String getLastTime() {
        return lastTime.get();
    }

    public void setLastTime(String lastTime) {
        this.lastTime.set(lastTime);
    }

    public String getLength() {
        return length.get();
    }

    public void setLength(String length) {
        this.length.set(length);
    }
}
