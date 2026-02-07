package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

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

    }
}
