package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import resourceHandler.IconName;
import resourceHandler.ResourceHandler;
import types.IconTypes;

import java.net.URL;
import java.util.ResourceBundle;

public class ControlPanelController implements Initializable
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Image addIcon = ResourceHandler.getIcon(IconTypes.CONTROL_PANEL, "24x24", IconName.ADD);
        if (addIcon != null)
        {
            ImageView addImage = new ImageView();
            addImage.setImage(addIcon);
            addButton.setGraphic(addImage);
        }
    }
}
