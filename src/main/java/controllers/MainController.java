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
        
        // при закрытии приложения удаляем виджеты, чтобы спровоцировать сохранение настроек
        AppContext.getMainWindow().setOnCloseRequest(event -> mainWidget.getItems().clear());
    }
}