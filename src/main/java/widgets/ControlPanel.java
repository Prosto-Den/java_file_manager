package widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXML;
import resourceHandler.IconName;
import resourceHandler.ResourceHandler;
import types.IconTypes;
import utils.FileSystem;

public class ControlPanel extends HBox implements IWidget
{
    @FXML
    private Button addButton;

    @FXML
    private ComboBox<String> diskComboBox;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button insertButton;

    @FXML
    private TextField currentPathField;

    public ControlPanel()
    {
        load("/layouts/ControlPanel.fxml");
        initUI();
    }

    public void initUI()
    {
        Image addIcon = ResourceHandler.getIcon(IconTypes.CONTROL_PANEL, "24x24", IconName.ADD);
        if (addIcon != null)
        {
            ImageView addImage = new ImageView();
            addImage.setImage(addIcon);
            addButton.setGraphic(addImage);
        }

        diskComboBox.setItems(FXCollections.observableArrayList(FileSystem.getLogicalDrives()));
        diskComboBox.setValue(diskComboBox.getItems().getFirst());
    }

    public void setFileSystem(FileSystem fileSystem)
    {
        currentPathField.textProperty().bind(fileSystem.getCurrentPathProperty());
    }

}
