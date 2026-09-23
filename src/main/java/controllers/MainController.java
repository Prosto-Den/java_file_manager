package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.nio.file.Path;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.BorderPane;

import app.AppContext;
import models.SettingKeys;
import utils.filesystem.FileSystemController;
import widgets.ControlPanel;
import widgets.Panel;


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
        Path leftPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.LEFT));
        Path rightPath = Path.of(AppContext.getSettings().get(SettingKeys.LastDirectory.RIGHT));

        createNewTab(leftTabPane, leftPath, SettingKeys.LastDirectory.LEFT);
        createNewTab(rightTabPane, rightPath, SettingKeys.LastDirectory.RIGHT);

        configureTabPane(leftTabPane);
        configureTabPane(rightTabPane);
    }

    // TODO так как теперь на каждой стороне несколько вкладок, надо решить, как сохранять последнюю открытую директорию
    /**
     * Создать новую вкладку панели
     * @param tabPane панельный виджет, для которого создаётся вкладка
     * @param initPath инициализирующий путь
     * @param lastDirectoryKey ключ для сохранения директории
     */
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

    /**
     * Настроить панельный виджет
     * @param tabPane панельный виджет
     */
    private void configureTabPane(TabPane tabPane)
    {
        Tab addTab = new Tab("+");
        addTab.setClosable(false);
        tabPane.getTabs().add(addTab);

        // настраиваем поведение при добавлении новой вкладки
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == addTab)
            {
                tabPane.getTabs().remove(addTab);
                createNewTab(tabPane, Path.of(System.getProperty("user.home")), null);
                tabPane.getTabs().add(addTab);
            }
        });

        // настраиваем вкладки, когда их остаётся только 2 (последняя открытая + кнопка "+")
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