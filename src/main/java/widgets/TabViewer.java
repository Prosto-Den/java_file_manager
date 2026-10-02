package widgets;


import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.util.Duration;
import models.AppSettings;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.Event;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.io.File;
import java.nio.file.Path;

import app.AppContext;
import events.EventBus;
import events.NewFileInDirEvent;
import utils.filesystem.FileSystemController;
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

    private final AppSettings.SideData side;

    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы. Нужен для создания первой вкладки. Идентификатор можно получить
     *                     через {@link FileSystemController}
     * @param helper помощник для связи пути файловой системы с настройками
     */
    public TabViewer(AppSettings.SideData side)
    {
        super();
        this.side = side;
        load(ResourceHandler.getLayout("TabViewer.fxml"));
        initUI();

        // сохранение настроек перед удалением виджета
        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null)
            {
                this.side.tabs.clear();
                for (Tab tab : getTabs())
                {
                    if (tab == addTab)
                        continue;
                    UUID id = getUUIDFromTab(tab);
                    this.side.tabs.add(FileSystemController.get(id).getCurrentPath().toString());
                }
                this.side.activeIndex = getSelectionModel().getSelectedIndex();
            }
        });

        // создание новой вкладки при нажатии на вкладку "+"
        getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == addTab)
            {
                Tab createdTab = createNewTab(FileSystemController.create());
                getSelectionModel().select(createdTab);
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

        setOnKeyPressed(event -> {
            if (event.isControlDown())
                {
                    //TabViewer activeTabViewer = (TabViewer) getActiveTabViewer();
                    switch (event.getCode())
                    {
                        // создание новой вкладки
                        case KeyCode.T -> {
                            createNewTab(FileSystemController.create());
                            event.consume();
                        }
                        // закрытие активной вкладки
                        case KeyCode.W -> {
                            if (getTabs().size() > 2)
                            {
                                Tab activeTab = getSelectionModel().getSelectedItem();
                                int index = getSelectionModel().getSelectedIndex();
                                getTabs().remove(activeTab);
                                // иногда при закрытии самой первой вкладки, селектирование может уйти на кнопки тулбара
                                // чтобы такого не было, проверим, какую вкеладку закрываем и если что, вернём селектирование
                                if (index == 0)
                                    getSelectionModel().select(0);
                                // при ручном удалении вкладки событие закрытия не генерируется, поэтому вызовем его сами
                                Event closedEvent = new Event(activeTab, activeTab, Tab.CLOSED_EVENT);
                                Event.fireEvent(activeTab, closedEvent);
                                event.consume();
                            }
                        }

                        default -> {/* ничего не делаем */}
                    }
                }
        });

        if (!side.tabs.isEmpty())
            for (String path : side.tabs)
            {
                UUID id = FileSystemController.create(Path.of(path));
                createNewTab(id);
            }
        else
            createNewTab(FileSystemController.create());

        getSelectionModel().select(getTabs().get(side.activeIndex));
    }

    @Override 
    public void initUI() {}

    /**
     * Создать новую вкладку
     * @param fileSystemId идентификатор файловой системы для данной вкладки
     * @param helper
     */
    public Tab createNewTab(UUID fileSystemId)
    {
        Tab newTab = new Tab();
        TabBody body = new TabBody(fileSystemId);
        Label tabLabel = new Label();

        tabLabel.textProperty().bind(body.currentDirProperty());
        newTab.setContent(body);
        newTab.setGraphic(tabLabel);
        newTab.setUserData(fileSystemId);

        configureNewTab(newTab);

        // последней всегда располагается вкладка "+", так что новую помещаем перед ней
        getTabs().add(getTabs().size() - 1, newTab);

        Platform.runLater(() -> body.requestFocus());

        return newTab;
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

            UUID fsId = getUUIDFromTab(tab);
            if (fsId != null)
                FileSystemController.delete(fsId);
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
                        EventBus.publish(new NewFileInDirEvent(fileSystemId));
                    }
                    else if (event.getAcceptedTransferMode() == TransferMode.COPY)
                    {
                        fileSystem.copyInto(files);
                        EventBus.publish(new NewFileInDirEvent(fileSystemId));
                    }

                    event.setDropCompleted(true);
                }
            }
            else
                event.setDropCompleted(false);

            event.consume();
        });
    }

    @Nullable 
    private UUID getUUIDFromTab(Tab tab)
    {
        UUID result = null;
        Object userData = tab.getUserData();
        if (userData != null && userData instanceof UUID)
            result = (UUID) userData;

        return result;
    }
}
