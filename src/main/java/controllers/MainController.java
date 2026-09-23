package controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.nio.file.Path;
import java.util.UUID;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.TransferMode;
import javafx.animation.PauseTransition;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.application.Platform;

import app.AppContext;
import events.EventBus;
import events.NewFileInDirEvent;
import models.SettingKeys;
import utils.filesystem.FileSystemController;
import widgets.ControlPanel;
import widgets.Panel;
import utils.filesystem.FileSystem;


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

        configureTabPane(leftTabPane);
        configureTabPane(rightTabPane);
        
        createNewTab(leftTabPane, leftPath, SettingKeys.LastDirectory.LEFT);
        createNewTab(rightTabPane, rightPath, SettingKeys.LastDirectory.RIGHT);


        setupTabShortCuts();
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
        UUID fileSystemId = FileSystemController.create(initPath);
        if (lastDirectoryKey != null)
            AppContext.getSettingsHelper().setFileSystemSettingsKey(fileSystemId, lastDirectoryKey);

        Panel panel = new Panel(fileSystemId, AppContext.getSettingsHelper());
        ControlPanel controlPanel = new ControlPanel(fileSystemId);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(controlPanel);
        borderPane.setCenter(panel);

        Tab tab = new Tab();
        Label tabLabel = new Label();
        tabLabel.textProperty().bind(panel.getCurrentDirProperty());
        tab.setContent(borderPane);
        tab.setGraphic(tabLabel);
        tab.setClosable(false);

        tab.setOnClosed(event -> {
            EventBus.unsubscribe(panel);
            EventBus.unsubscribe(controlPanel);
        });

        PauseTransition hoverTimer = new PauseTransition(Duration.millis(300));
        hoverTimer.setOnFinished(event -> {
            if (tabPane.getTabs().contains(tab))
                tabPane.getSelectionModel().select(tab);
        });

        // у самой вкладки нет настройки событий для drag'n'drop, так что настраиваем через Label
        tabLabel.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles())
                hoverTimer.playFromStart();

            event.consume();
        });

        tabLabel.setOnDragOver(event -> {
            Dragboard dragBoard = event.getDragboard();
            if (dragBoard.hasFiles())
            {
                Object rawFilePath = dragBoard.getContent(AppContext.getPanelDataFormat());
                if (rawFilePath != null && rawFilePath instanceof String)
                {
                    String filePath = (String) rawFilePath;
                    if (!FileSystemController.get(fileSystemId).getCurrentPath().toString().equals(filePath))
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }

            event.consume();
        });

        tabLabel.setOnDragExited(event -> {
            hoverTimer.stop();
            event.consume();
        });

        tabLabel.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles())
            {
                FileSystem fileSystem = FileSystemController.get(fileSystemId);
                List<File> files = dragboard.getFiles();
                if (event.getAcceptedTransferMode() == TransferMode.MOVE)
                {
                    fileSystem.moveInto(files);
                    EventBus.publish(new NewFileInDirEvent(fileSystem.getCurrentPath()));
                }
                else if (event.getAcceptedTransferMode() == TransferMode.COPY)
                {
                    fileSystem.copyInto(files);
                    EventBus.publish(new NewFileInDirEvent(fileSystem.getCurrentPath()));
                }

                event.setDropCompleted(true);
            }
            else
                event.setDropCompleted(false);

            event.consume();
        });

        tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
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
                //tabPane.getTabs().remove(addTab);
                createNewTab(tabPane, Path.of(System.getProperty("user.home")), null);
                //tabPane.getTabs().add(addTab);
            }
        });

        // настраиваем поведение вкладок, когда их остаётся только 2 (последняя открытая + кнопка "+")
        tabPane.getTabs().addListener((ListChangeListener<Tab>) listener -> {
            if (tabPane.getTabs().size() == 2)
                tabPane.getTabs().get(0).setClosable(false);
            else
                for (Tab tab : tabPane.getTabs())
                    if (tab != addTab)
                        tab.setClosable(true);
        });
    }

    private void setupTabShortCuts()
    {
        Platform.runLater(() -> {
            //TabPane activeTabPane = getActiveTabPane();
            Scene scene = leftTabPane.getScene();
            if (scene == null)
                return;

            scene.setOnKeyPressed(event -> {
                if (event.isControlDown())
                {
                    switch (event.getCode())
                    {
                        case KeyCode.T -> {
                            TabPane activeTabPane = getActiveTabPane();
                            Path homePath = Path.of(System.getProperty("user.home"));
                            createNewTab(activeTabPane, homePath, null);
                        }

                        case KeyCode.W -> {
                            TabPane activeTabPane = getActiveTabPane();
                            Tab activeTab = activeTabPane.getSelectionModel().getSelectedItem();
                            if (activeTab != null && activeTabPane.getTabs().size() > 2)
                                activeTabPane.getTabs().remove(activeTab);
                        }

                        default -> {/* ничего не делаем */}
                    }
                }

                event.consume();
            });
        });
    }

    /**
     * Вычислить активный панельный виджет
     * @return активный панельный виджет
     */
    private TabPane getActiveTabPane()
    {
        // у панелей один и тот же контейнер, сцену можно взять от любого, но на всякий случай вставим проверку
        Scene scene = leftTabPane.getScene();
        TabPane result = null;

        if (scene == null)
            scene = rightTabPane.getScene();
        if (scene != null)
        {
            Node focusOwner = scene.getFocusOwner();
            if (focusOwner != null)
            {
                Node current = focusOwner;
                while (current != null)
                {
                    if (current == leftTabPane || current == rightTabPane)
                        break;

                    current = current.getParent();
                }

                if (current != null)
                    result = (TabPane) current;
            }
        }
        
        if (result == null)
            result = rightTabPane.isHover() ? rightTabPane : leftTabPane;

        return result; // по умолчанию будем возвращать левую
    }
}