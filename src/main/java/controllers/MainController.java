package controllers;

import javafx.fxml.FXML;
import utils.FileSystem;
import widgets.*;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable
{
    @FXML
    private ControlPanel leftControlPanel;

    @FXML
    private Panel leftPanel;

    @FXML
    private ControlPanel rightControlPanel;

    @FXML
    private Panel rightPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        FileSystem leftFileSystem = new FileSystem();
        FileSystem rightFileSystem = new FileSystem();

        leftControlPanel.setFileSystem(leftFileSystem);
        leftPanel.setFileSystem(leftFileSystem);

        rightControlPanel.setFileSystem(rightFileSystem);
        rightPanel.setFileSystem(rightFileSystem);
    }
}
