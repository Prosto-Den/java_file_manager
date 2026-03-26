package controllers;


import events.EventBus;
import events.LocaleChangedEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import models.Language;
import models.SettingKeys;
import models.StringKeys;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import utils.LanguageManager;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;
import utils.settingsUtils.SettingsUtils;

public class SettingsController implements Initializable
{
    @FXML
    private Label languageLabel;
    @FXML
    private ComboBox<Language> localeBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button applyButton;

    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        EventBus.subscribe(LocaleChangedEvent.class, event -> updateText());

        localeBox.setItems(FXCollections.observableArrayList(LanguageManager.getInstance().getAvailableLanguages()));
        localeBox.setValue(LanguageManager.getInstance().getCurrentLanguage());

        localeBox.setCellFactory(lv -> new ListCell<Language>() {
            @Override
            protected void updateItem(Language lang, boolean empty)
            {
                super.updateItem(lang, empty);

                if (empty || lang == null)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    setText(lang.displayName());
                    ImageView flag = new ImageView(ResourceHandler.getIcon(IconSize.SMALL, lang.flagPath()));
                    setGraphic(flag);
                }
            }
        });

        saveButton.setOnAction(event -> onSaveButtonClick());
        applyButton.setOnAction(event -> onApplyButtonClicked());
        cancelButton.setOnAction(event -> onCancelButtonCLicked());
    }

    public void setStage(Stage stage) { dialogStage = stage; }

    private void onSaveButtonClick()
    {
        // применяем изменения
        onApplyButtonClicked();

        // в конце просто закрываем настройки
        onCancelButtonCLicked();
    }

    private void onApplyButtonClicked()
    {
        // сохраняем локаль
        Language value = localeBox.getValue();
        String currentCode = SettingsUtils.get(SettingKeys.LOCALE);

        if (!value.code().equals(currentCode))
        {
            SettingsUtils.set(SettingKeys.LOCALE, value.code());
            ResourceHandler.setLocale(value.toLocale());
        }
    }

    private void onCancelButtonCLicked()
    {
        if (dialogStage != null)
            dialogStage.close();
    }

    private void updateText()
    {
        languageLabel.setText(ResourceHandler.getString(StringKeys.SETTINGS_LANGUAGE_LABEL));
        saveButton.setText(ResourceHandler.getString(StringKeys.SETTINGS_BUTTON_SAVE));
        applyButton.setText(ResourceHandler.getString(StringKeys.SETTINGS_BUTTON_APPLY));
        cancelButton.setText(ResourceHandler.getString(StringKeys.SETTINGS_BUTTON_CANCEL));
    }
}
