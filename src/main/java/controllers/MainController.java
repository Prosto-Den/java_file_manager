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
import java.nio.file.Path;


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
        Path leftPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.LEFT));
        Path rightPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.RIGHT));
        String leftFileSystemID = FileSystemController.create(leftPath);
        String rightFileSystemID = FileSystemController.create(rightPath);

        // устанавливаем связь между UUID файловой системы и ключом в настройках
        AppContext.getSettingsHelper().setFileSystemSettingsKey(leftFileSystemID, SettingKeys.LastDirectory.LEFT);
        AppContext.getSettingsHelper().setFileSystemSettingsKey(rightFileSystemID, SettingKeys.LastDirectory.RIGHT);

        // настраиваем левую часть окна
        leftContainer.setTop(new ControlPanel(leftFileSystemID));
        leftContainer.setCenter(new Panel(leftFileSystemID, AppContext.getSettingsHelper(), 1));

        // настраиваем правую часть окна
        rightContainer.setTop(new ControlPanel(rightFileSystemID));
        rightContainer.setCenter(new Panel(rightFileSystemID, AppContext.getSettingsHelper(), 2));
    }
}
