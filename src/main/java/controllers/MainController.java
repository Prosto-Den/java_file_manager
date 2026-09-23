package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import app.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import models.SettingKeys;
import utils.filesystem.FileSystemController;
import widgets.ControlPanel;
import widgets.Panel;
import java.nio.file.Path;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.BorderPane;


/**
 * Класс для инициализации интерфейса приложения
 * */
public class MainController implements Initializable
{
    @FXML 
    private TabPane leftTabPane;
    @FXML
    private TabPane rightTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Создаём экземпляры файловых систем
        Path leftPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.LEFT));
        Path rightPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.RIGHT));

        createNewTab(leftTabPane, leftPath, SettingKeys.LastDirectory.LEFT);
        createNewTab(rightTabPane, rightPath, SettingKeys.LastDirectory.RIGHT);

        configureTabPane(leftTabPane);
        configureTabPane(rightTabPane);
    }

    private void createNewTab(TabPane tabPane, Path initPath, String lastDirectoryKey)
    {
        String fileSystemId = FileSystemController.create(initPath);
        if (lastDirectoryKey != null)
            AppContext.getSettingsHelper().setFileSystemSettingsKey(fileSystemId, lastDirectoryKey);
        Panel panel = new Panel(fileSystemId, AppContext.getSettingsHelper());
        ControlPanel controlPanel = new ControlPanel(fileSystemId);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(controlPanel);
        borderPane.setCenter(panel);

        Tab tab = new Tab();
        tab.setContent(borderPane);
        tab.textProperty().bind(panel.getCurrentDirProperty());
        tab.setClosable(false);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void configureTabPane(TabPane tabPane)
    {
        Tab addTab = new Tab("+");
        addTab.setClosable(false);
        tabPane.getTabs().add(addTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == addTab)
            {
                tabPane.getTabs().remove(addTab);
                createNewTab(tabPane, Path.of(System.getProperty("user.home")), null);
                tabPane.getTabs().add(addTab);
            }
        });

        tabPane.getTabs().addListener((ListChangeListener<Tab>) listener -> {
            if (tabPane.getTabs().size() == 2)
                tabPane.getTabs().get(0).setClosable(false);
            else
                for (Tab tab : tabPane.getTabs())
                    if (tab != addTab)
                        tab.setClosable(true);
        });
    }
}

// public class MainController implements Initializable
// {
//     // левая панель с отображаемыми файлами директории
//     @FXML
//     private BorderPane leftContainer;

//     // правая панель с отображаемыми файлами директории
//     @FXML
//     private BorderPane rightContainer;

//     @Override
//     public void initialize(URL url, ResourceBundle bundle)
//     {
//         // Создаём экземпляры файловых систем
//         Path leftPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.LEFT));
//         Path rightPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.RIGHT));
//         String leftFileSystemID = FileSystemController.create(leftPath);
//         String rightFileSystemID = FileSystemController.create(rightPath);

//         // устанавливаем связь между UUID файловой системы и ключом в настройках
//         AppContext.getSettingsHelper().setFileSystemSettingsKey(leftFileSystemID, SettingKeys.LastDirectory.LEFT);
//         AppContext.getSettingsHelper().setFileSystemSettingsKey(rightFileSystemID, SettingKeys.LastDirectory.RIGHT);

//         // настраиваем левую часть окна
//         leftContainer.setTop(new ControlPanel(leftFileSystemID));
//         leftContainer.setCenter(new Panel(leftFileSystemID, AppContext.getSettingsHelper()));

//         // настраиваем правую часть окна
//         rightContainer.setTop(new ControlPanel(rightFileSystemID));
//         rightContainer.setCenter(new Panel(rightFileSystemID, AppContext.getSettingsHelper()));
//     }
// }
