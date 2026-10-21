package controllers;


import java.util.ResourceBundle;

import app.AppContext;
import events.EventBus;
import events.LocaleChangedEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.StringKeys;
import events.KeyEvent;
import javafx.scene.input.KeyCode;

import java.net.URL;

import widgets.interfaces.ITranslatable;

/**
 * Контроллер для StatusBar. Отвечает за наполнение подсказок на дне окна при смене локали и нажатии на некоторые клавиши
 * StatusBarController
 */
public class StatusBarController implements Initializable, ITranslatable
{
    @FXML 
    private Label renameShortcutLabel;
    @FXML
    private Label createTabShortCutLabel;
    @FXML
    private Label closeTabShortcutLabel;
    @FXML
    private Label deleteShortcutLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        EventBus.subscribe(LocaleChangedEvent.class, this, event -> updateText());
        EventBus.subscribe(KeyEvent.class, this, event -> {
            if (event.getCode() == KeyCode.SHIFT)
            {
                if (event.isPressed())
                    deleteShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_DELETE));
                else
                    deleteShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_MOVE_TO_TRASH));
            }
        });
    }

    @Override
    public void updateText()
    {
        renameShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_RENAME));
        createTabShortCutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_CREATE_NEW_TAB));
        closeTabShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_CLOSE_ACTIVE_TAB));
        deleteShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_DELETE));
    }
}
