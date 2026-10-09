package controllers;


import app.AppContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Контроллер для ToolBar. Отвечает за настройку действий при нажатии на кнопки ToolBar.
 * ToolBarController
 */
public class ToolBarController implements Initializable
{
    @FXML
    private Button findDuplicateButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button trashButton;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }

    /**
     * Реакция на нажатие кнопкм "Показать настройки"
     */
    @FXML
    private void onSettingsButtonClick()
    {
        AppContext.getWindowManager().createSettingsStage().showAndWait();
    }

    /**
     * Реакция на нажатие кнопки "Показать корзину"
     */
    @FXML
    private void onTrashButtonClick()
    {
        AppContext.getWindowManager().createOrGetTrashStage().show();
    }
}
