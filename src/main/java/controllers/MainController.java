package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import utils.FileSystemController;
import widgets.*;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Класс для инициализации интерфейса приложения
 * */
public class MainController implements Initializable
{
    // левая панель с отображаемыми файлами директории
    @FXML
    private BorderPane leftContainer;

    // правая панель с отображаемыми файлами директории
    @FXML
    private BorderPane rightContainer;

    @Override
    public void initialize(URL url, ResourceBundle bundle)
    {
        // Создаём экземпляры файловых систем
        String leftFileSystemID = FileSystemController.create();
        String rightFileSystemID = FileSystemController.create();

        // настраиваем левую часть окна
        leftContainer.setTop(new ControlPanel(leftFileSystemID));
        leftContainer.setCenter(new Panel(leftFileSystemID));

        // настраиваем правую часть окна
        rightContainer.setTop(new ControlPanel(rightFileSystemID));
        rightContainer.setCenter(new Panel(rightFileSystemID));
    }
}
