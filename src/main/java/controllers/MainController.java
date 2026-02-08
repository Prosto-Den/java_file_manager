package controllers;

import javafx.fxml.FXML;
import utils.FileSystem;
import widgets.*;

public class MainController
{
    @FXML
    private ControlPanel leftControlPanel;

    @FXML
    private Panel leftPanel;

    @FXML
    private ControlPanel rightControlPanel;

    @FXML
    private Panel rightPanel;

    public void setLeftFileSystem(FileSystem fileSystem)
    {
        leftControlPanel.setFileSystem(fileSystem);
        leftPanel.setFileSystem(fileSystem);
    }

    public void setRightFileSystem(FileSystem fileSystem)
    {
        rightControlPanel.setFileSystem(fileSystem);
        rightPanel.setFileSystem(fileSystem);
    }
}
