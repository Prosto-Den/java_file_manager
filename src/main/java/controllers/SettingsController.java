package controllers;


import events.EventBus;
import events.LocaleChangedEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Language;
import models.SettingKeys;
import models.StringKeys;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import types.OSType;
import utils.i18n.LanguageManager;
import utils.settings.SettingsManager;

import java.net.URL;
import java.util.ResourceBundle;

import app.AppContext;
import javafx.scene.image.ImageView;
import widgets.interfaces.ITranslatable;


/**
 * Контроллер для окна настроек
 * */
public class SettingsController implements Initializable, ITranslatable
{
    @FXML
    private Label languageLabel; // надпись "Язык"
    @FXML
    private ComboBox<Language> localeBox; // комбобокс с выбором локали
    @FXML
    private Button saveButton; // кнопка "Сохранить"
    @FXML
    private Button cancelButton; // кнопка "Отмена"
    @FXML
    private Button applyButton; // кнопка "Применить"
    @FXML
    private Separator linuxSettingsSeparator; // разделитель для Linux настроек
    @FXML
    private GridPane linuxSettings; // контейнер со всеми виджетами для Linux настроек
    @FXML
    private TextField terminalTextField; // поле ввода Linux терминала
    @FXML
    private TextField openCommandTextField; // поле ввода команды открытия для Linux
    @FXML
    private Label languageSettingsTitle; // заголовок настроек языка
    @FXML
    private Label linuxSettingsTitle; // заголовок Linux настроек
    @FXML
    private Label linuxUsedTerminalLabel; // надпись "Используемый терминал"
    @FXML
    private Label linuxOpenCommandLabel; // надпись "Команда открытия"

    private Stage dialogStage; // диалог настроек. Нужно сохранить диалог здесь, чтобы работала кнопка "Отменить"
    private SettingsManager settingsManager;
    private LanguageManager languageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        EventBus.subscribe(LocaleChangedEvent.class, event -> updateText());

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
        cancelButton.setOnAction(event -> onCancelButtonClicked());
    }

    /**
     * Выставить диалог настроек в контроллер
     * @param stage диалог настроек
     * */
    public void setStage(Stage stage) { dialogStage = stage; }

    public void init(Stage stage, SettingsManager settingsManager, LanguageManager languageManager)
    {
        dialogStage = stage;
        this.settingsManager = settingsManager;
        this.languageManager = languageManager;

        localeBox.setItems(FXCollections.observableArrayList(this.languageManager.getAvailableLanguages()));
        localeBox.setValue(this.languageManager.getCurrentLanguage());

        if (OSType.is(OSType.LINUX))
        {
            configureLinuxControls();
        }
        else
            setLinuxControlsVisible(false);

        settingsManager.beginEdit();
    }

    /**
     * Реакция на нажатие кнопки "Сохранить"
     * */
    private void onSaveButtonClick()
    {
        // применяем изменения
        onApplyButtonClicked();
        
        settingsManager.commitEdit();

        // в конце просто закрываем настройки
        onCancelButtonClicked();
    }

    /**
     * Реакция на нажатие кнопки "Применить"
     * */
    private void onApplyButtonClicked()
    {
        // сохраняем локаль
        Language value = localeBox.getValue();
        if (settingsManager.set(SettingKeys.LOCALE, value.code()))
            languageManager.setCurrentLanguage(value);

        // сохраняем настройки Linux
        if (OSType.is(OSType.LINUX))
        {
            // сохраняем используемый терминал и команду открытия
            String linuxTerminal = terminalTextField.getText();
            String linuxOpenCommand = openCommandTextField.getText();

            settingsManager.set(SettingKeys.LINUX_CONSOLE, linuxTerminal);
            settingsManager.set(SettingKeys.LINUX_OPEN_COMMAND, linuxOpenCommand);
        }
    }

    /**
     * Реакция на нажатие кнопки "Отменить"
     * */
    private void onCancelButtonClicked()
    {
        if (dialogStage != null)
            dialogStage.close();
        settingsManager.rollbackEdit();
        // пересохраняем текущую локаль, чтобы сбросить изменения
        languageManager.setCurrentLanguage(settingsManager.get(SettingKeys.LOCALE));
    }

    @Override
    public void updateText()
    {
        languageLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_LANGUAGE_LABEL));
        saveButton.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_BUTTON_SAVE));
        applyButton.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_BUTTON_APPLY));
        cancelButton.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_BUTTON_CANCEL));
        languageSettingsTitle.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_LANGUAGE_TITLE));
        linuxSettingsTitle.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_LINUX_TITLE));
        linuxUsedTerminalLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_LINUX_USED_TERMINAL));
        linuxOpenCommandLabel.setText(AppContext.getLanguageManager().getString(StringKeys.SETTINGS_LINUX_OPEN_COMMAND));
    }

    // Приватные методы

    /**
     * Выставить видимость для элементов управления настроек Linux
     * @param visible флаг видимости
     * */
    private void setLinuxControlsVisible(boolean visible)
    {
        linuxSettingsSeparator.setVisible(visible);
        linuxSettings.setVisible(visible);
    }

    /**
     * Настроить элементы управления настроек Linux
     * */
    private void configureLinuxControls()
    {
        terminalTextField.setText(settingsManager.get(SettingKeys.LINUX_CONSOLE));
        openCommandTextField.setText(settingsManager.get(SettingKeys.LINUX_OPEN_COMMAND));
    }
}
