package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import models.SettingKeys;
import utils.FileSystemController;
import utils.settingsUtils.FileSystemSettingsHelper;
import utils.settingsUtils.SettingsUtils;
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
        String leftPath = SettingsUtils.get(SettingKeys.LastDirectory.LEFT);
        String rightPath = SettingsUtils.get(SettingKeys.LastDirectory.RIGHT);
        String leftFileSystemID = FileSystemController.create(leftPath == null ? "" : leftPath);
        String rightFileSystemID = FileSystemController.create(rightPath == null ? "" : rightPath);

        // устанавливаем связь между UUID файловой системы и ключом в настройках
        FileSystemSettingsHelper.setFileSystemSettingsKey(leftFileSystemID, SettingKeys.LastDirectory.LEFT);
        FileSystemSettingsHelper.setFileSystemSettingsKey(rightFileSystemID, SettingKeys.LastDirectory.RIGHT);

        // настраиваем левую часть окна
        leftContainer.setTop(new ControlPanel(leftFileSystemID));
        leftContainer.setCenter(new Panel(leftFileSystemID));

        // настраиваем правую часть окна
        rightContainer.setTop(new ControlPanel(rightFileSystemID));
        rightContainer.setCenter(new Panel(rightFileSystemID));
    }
}
