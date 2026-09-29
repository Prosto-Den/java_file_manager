package controllers;


import java.util.ResourceBundle;

import app.AppContext;
import events.EventBus;
import events.LocaleChangedEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.StringKeys;

import java.net.URL;

import widgets.interfaces.ITranslatable;

public class StatusBarController implements Initializable, ITranslatable
{
    @FXML 
    private Label renameShortcutLabel;
    @FXML
    private Label createTabShortCutLabel;
    @FXML
    private Label closeTabShortcutLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        EventBus.subscribe(LocaleChangedEvent.class, this, event -> updateText());
    }

    @Override
    public void updateText()
    {
        renameShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_RENAME));
        createTabShortCutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_CREATE_NEW_TAB));
        closeTabShortcutLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SHORTCUT_CLOSE_ACTIVE_TAB));
    }
}
