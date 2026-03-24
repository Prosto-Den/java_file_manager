package controllers;


import app.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import models.SettingKeys;
import utils.LanguageManager;
import utils.SettingsUtils;

import java.net.URL;
import java.util.ResourceBundle;


public class ToolBarController implements Initializable
{
    @FXML
    private Button findDuplicateButton;

    @FXML
    private Button settingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        settingsButton.setOnAction(event -> onSettingsButtonClick());
    }

    private void onSettingsButtonClick()
    {
        LanguageManager.getInstance().setCurrentLanguage(SettingsUtils.get(SettingKeys.LOCALE));
        AppContext.getSettingsStage().showAndWait();
    }
}
