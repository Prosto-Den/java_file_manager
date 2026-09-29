package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;

import app.AppContext;
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
        TabViewer left = new TabViewer(AppContext.getSettings().getSettings().session.left);   
        TabViewer right = new TabViewer(AppContext.getSettings().getSettings().session.right);
        mainWidget.getItems().addAll(left, right);
        //lastActiveTabPane = left; // по умолчанию оставим активной левую панель
        
        // при закрытии приложения удаляем виджеты, чтобы спровоцировать сохранение настроек
        AppContext.getMainWindow().setOnCloseRequest(event -> mainWidget.getItems().clear());

        //setupTabViewerShortCut();
    }

    // Пока метод не нужен, закомментировал его
    /**
     * Получить активную панель вкладок
     * @return активная панель вкладок
     */
    // private Node getActiveTabViewer()
    // {
    //     Scene scene = mainWidget.getScene();

    //     if (scene == null)
    //         return lastActiveTabPane;

    //     Node focusOwner = scene.getFocusOwner();
    //     while (focusOwner != null)
    //     {
    //         if (mainWidget.getItems().contains(focusOwner))
    //             {
    //                 lastActiveTabPane = focusOwner;
    //                 break;
    //             }

    //             focusOwner = focusOwner.getParent();
    //     }

    //     return lastActiveTabPane;
    // }

    // Этот код перенесён в TabViewer. Какие подводные камни образуются не известно, потому пока оставил этот код тут
    // Если работа горячих клавиш останется приемлемой - удалю
    /**
     * Настроить горячие клавиши для панели вкладок
     */
    // private void setupTabViewerShortCut()
    // {
    //     Platform.runLater(() -> {
    //         Scene scene = mainWidget.getScene();
    //         if (scene == null)
    //             return;

    //         scene.setOnKeyPressed(event -> {
    //             if (event.isControlDown())
    //             {
    //                 TabViewer activeTabViewer = (TabViewer) getActiveTabViewer();
    //                 switch (event.getCode())
    //                 {
    //                     // создание новой вкладки
    //                     case KeyCode.T -> {
    //                         activeTabViewer.createNewTab(FileSystemController.create());
    //                         event.consume();
    //                     }
    //                     // закрытие активной вкладки
    //                     case KeyCode.W -> {
    //                         if (activeTabViewer.getTabs().size() > 2)
    //                         {
    //                             Tab activeTab = activeTabViewer.getSelectionModel().getSelectedItem();
    //                             activeTabViewer.getTabs().remove(activeTab);
    //                             // при ручном удалении вкладки событие закрытия не генерируется, поэтому вызовем его сами
    //                             Event closedEvent = new Event(activeTab, activeTab, Tab.CLOSED_EVENT);
    //                             Event.fireEvent(activeTab, closedEvent);
    //                             event.consume();
    //                         }
    //                     }

    //                     default -> {/* ничего не делаем */}
    //                 }
    //             }
    //         });
    //     });
    // }
}