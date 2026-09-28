package widgets;


import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.util.Duration;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.fxml.FXML;

import java.util.UUID;
import java.util.List;
import java.io.File;

import app.AppContext;
import events.CloseTabEvent;
import events.EventBus;
import events.NewFileInDirEvent;
import events.NewTabEvent;
import utils.filesystem.FileSystemController;
import utils.settings.FileSystemSettingsHelper;
import widgets.interfaces.IWidget;
import utils.filesystem.FileSystem;
import resourceHandler.ResourceHandler;


/**
 * Виджет вкладок панели
 * TabViewer
 */
public final class TabViewer extends TabPane implements IWidget
{
    @FXML
    private Tab addTab;

    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы. Нужен для создания первой вкладки. Идентификатор можно получить
     *                     через {@link FileSystemController}
     * @param helper помощник для связи пути файловой системы с настройками
     */
    public TabViewer(UUID fileSystemId, FileSystemSettingsHelper helper)
    {
        super();
        load(ResourceHandler.getLayout("TabViewer.fxml"));
        initUI();

        // создание новой вкладки при нажатии на вкладку "+"
        getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == addTab)
            {
                EventBus.publish(new NewTabEvent(this));
            }
        });

        // настройка возможности закрывать вкладки при изменении числа вкладок
        getTabs().addListener((ListChangeListener<Tab>) listener -> {
            if (getTabs().size() == 2)
                getTabs().get(0).setClosable(false);
            else
            {
                for (Tab tab : getTabs())
                    if (tab != addTab)
                        tab.setClosable(true);
            }
        });

        createNewTab(fileSystemId, helper);
    }

    @Override 
    public void initUI() {}

    /**
     * Создать новую вкладку
     * @param fileSystemId идентификатор файловой системы для данной вкладки
     * @param helper
     */
    public void createNewTab(UUID fileSystemId, FileSystemSettingsHelper helper)
    {
        Tab newTab = new Tab();
        TabBody body = new TabBody(fileSystemId, helper);
        Label tabLabel = new Label();

        tabLabel.textProperty().bind(body.currentDirProperty());
        newTab.setContent(body);
        newTab.setGraphic(tabLabel);
        newTab.setUserData(fileSystemId);

        configureNewTab(newTab);

        // последней всегда располагается вкладка "+", так что новую помещаем перед ней
        getTabs().add(getTabs().size() - 1, newTab);
        getSelectionModel().select(newTab);
    }

    /**
     * Настроить вкладку виджета
     * @param tab вкладка виджета
     */
    private void configureNewTab(Tab tab)
    {
        // отписываеимся от событий при закрытии вкладки и удаляем объект файловой системы
        tab.setOnClosed(event -> {
            Node content = tab.getContent();
            if (content != null && content instanceof TabBody)
                ((TabBody) content).unsubscribe();
            Object userData = tab.getUserData();
            if (userData != null && userData instanceof UUID)
                EventBus.publish(new CloseTabEvent((UUID) userData));
        });

        Node tabLabel = tab.getGraphic();
        PauseTransition hoverTime = new PauseTransition(Duration.millis(300));

        // курсор залез на вкладку при drag'n'drop
        tabLabel.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles())
                hoverTime.playFromStart();

            event.consume();
        });

        // курсор ушёл со вкладки
        tabLabel.setOnDragExited(event -> {
            hoverTime.stop();
            event.consume();
        });

        // курсор задержался на вкладке
        tabLabel.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles())
            {
                Object rawFileObject = dragboard.getContent(AppContext.getPanelDataFormat());
                if (rawFileObject != null && rawFileObject instanceof String)
                {
                    String filePath = (String) rawFileObject;
                    Object userData = tab.getUserData();
                    if (userData != null && userData instanceof UUID)
                    {
                        UUID fileSystemId = (UUID) userData;
                        if (!FileSystemController.get(fileSystemId).getCurrentPath().toString().equals(filePath))
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                }
                event.consume();
            }
        });

        // drag'n'drop завершился на вкладке
        tabLabel.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles())
            {
                List<File> files = dragboard.getFiles();
                Object userData = tab.getUserData();
                if (userData != null && userData instanceof UUID)
                {
                    UUID fileSystemId = (UUID) userData;
                    FileSystem fileSystem = FileSystemController.get(fileSystemId);
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
            }
            else
                event.setDropCompleted(false);

            event.consume();
        });
    }
}
