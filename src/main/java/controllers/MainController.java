package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import app.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import models.SettingKeys;
import utils.filesystem.FileSystemController;
import widgets.ControlPanel;
import widgets.Panel;


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
        String leftPath = AppContext.getSettings().get(SettingKeys.LastDirectory.LEFT);
        String rightPath = AppContext.getSettings().get(SettingKeys.LastDirectory.RIGHT);
        String leftFileSystemID = FileSystemController.create(leftPath == null ? "" : leftPath);
        String rightFileSystemID = FileSystemController.create(rightPath == null ? "" : rightPath);

        // устанавливаем связь между UUID файловой системы и ключом в настройках
        AppContext.getSettingsHelper().setFileSystemSettingsKey(leftFileSystemID, SettingKeys.LastDirectory.LEFT);
        AppContext.getSettingsHelper().setFileSystemSettingsKey(rightFileSystemID, SettingKeys.LastDirectory.RIGHT);

        // настраиваем левую часть окна
        leftContainer.setTop(new ControlPanel(leftFileSystemID));
        leftContainer.setCenter(new Panel(leftFileSystemID, AppContext.getSettingsHelper()));

        // настраиваем правую часть окна
        rightContainer.setTop(new ControlPanel(rightFileSystemID));
        rightContainer.setCenter(new Panel(rightFileSystemID, AppContext.getSettingsHelper()));
    }
}
