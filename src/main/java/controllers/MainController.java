package controllers;

import javafx.fxml.FXML;
import utils.FileSystem;
import widgets.*;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable
{
    // левая контрольная панель
    @FXML
    private ControlPanel leftControlPanel;

    // левая панель с отображаемыми файлами директории
    @FXML
    private Panel leftPanel;

    // правая контрольная панель
    @FXML
    private ControlPanel rightControlPanel;

    // правая панель с отображаемыми файлами директории
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
