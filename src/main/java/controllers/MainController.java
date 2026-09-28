package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.control.SplitPane;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;

import app.AppContext;
import events.CloseTabEvent;
import events.EventBus;
import events.NewTabEvent;
import utils.filesystem.FileSystemController;
import widgets.TabViewer;


/**
 * Класс для инициализации интерфейса приложения
 * */
public class MainController implements Initializable
{
    @FXML 
    private SplitPane mainWidget;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        TabViewer left = new TabViewer(FileSystemController.create(), AppContext.getSettingsHelper());   
        TabViewer right = new TabViewer(FileSystemController.create(), AppContext.getSettingsHelper());   
        mainWidget.getItems().addAll(left, right);
        EventBus.subscribe(NewTabEvent.class, this, event -> {
            UUID fileSystemId = FileSystemController.create();
            Node tabOwner = event.getTabOwner();
            if (tabOwner != null && tabOwner instanceof TabViewer)
                ((TabViewer) tabOwner).createNewTab(fileSystemId, AppContext.getSettingsHelper());
        });
        EventBus.subscribe(CloseTabEvent.class, this, event -> FileSystemController.delete(event.getFileSystemId()));
        
        setupTabViewerShortCut();
    }

    // TODO может просто считать активной ту панель, которая под курсором?
    /**
     * Получить активную панель вкладок
     * @return активная панель вкладок
     */
    private TabViewer getActiveTabViewer()
    {
        TabViewer result = null;

        Scene scene = mainWidget.getScene();
        Node current = scene.getFocusOwner();
        ObservableList<Node> items = mainWidget.getItems();

        while (current != null)
        {
            if (items.contains(current))
                break;

            current = current.getParent();
        }

        if (current != null && current instanceof TabViewer)
            result = (TabViewer) current;
        else
            for (Node item : items)
                if (item.isHover() && item instanceof TabViewer)
                {
                    result = (TabViewer) item;
                    break;
                }

        return result;
    }

    /**
     * Настроить горячие клавиши для панели вкладок
     */
    private void setupTabViewerShortCut()
    {
        Platform.runLater(() -> {
            Scene scene = mainWidget.getScene();
            if (scene == null)
                return;

            scene.setOnKeyPressed(event -> {
                if (event.isControlDown())
                {
                    TabViewer activeTabViewer = getActiveTabViewer();
                    switch (event.getCode())
                    {
                        // создание новой вкладки
                        case KeyCode.T -> {
                            UUID fileSystemId = FileSystemController.create();
                            activeTabViewer.createNewTab(fileSystemId, AppContext.getSettingsHelper());
                            event.consume();
                        }
                        // закрытие активной вкладки
                        case KeyCode.W -> {
                            if (activeTabViewer.getTabs().size() > 2)
                            {
                                Tab activeTab = activeTabViewer.getSelectionModel().getSelectedItem();
                                activeTabViewer.getTabs().remove(activeTab);
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
        });
    }
}