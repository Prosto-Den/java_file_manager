package utils.ui;

import controllers.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.StringKeys;
import resourceHandler.ResourceHandler;
import utils.i18n.LanguageManager;
import utils.settings.SettingsManager;

import java.io.IOException;

/**
 * Вспомогательный класс для работы с окнами приложения.
 */
public final class WindowManager
{
    private final Stage mainStage;
    private Stage trashStage; // окно корзины не блокирует главное окно, поэтому храним его, чтобы не создавать по новой, если вдруг оно открыто
    private final SettingsManager settingsManager;
    private final LanguageManager languageManager;

    public WindowManager(Stage mainStage, SettingsManager settingsManager, LanguageManager languageManager)
    {
        this.mainStage = mainStage;
        this.settingsManager = settingsManager;
        this.languageManager = languageManager;
    }

    /**
     * Создать окно для работы с настройками приложения.
     *
     * @return окно с настройками
     */
    public Stage createSettingsStage()
    {
        Stage settingsStage = new Stage();

        if (mainStage != null)
        {
            // на Hyprland происходит баг с курсором (он не может покинуть область окна), так что блокировать окно будем вручную
            settingsStage.initModality(Modality.NONE);
            settingsStage.initOwner(mainStage);

            disableMainStage(settingsStage);

            try
            {
                FXMLLoader settingsLoader = new FXMLLoader(ResourceHandler.getLayout("SettingsLayout.fxml"),
                        languageManager.getBundle());
                Parent root = settingsLoader.load();

                Scene scene = new Scene(root);
                settingsStage.setScene(scene);
                settingsStage.setTitle(languageManager.getString(StringKeys.SETTINGS_TITLE));

                SettingsController controller = settingsLoader.getController();
                controller.init(settingsStage, settingsManager, languageManager);
            }
            catch (IOException ex)
            {
                System.err.println("Не удалось загрузить окно настроек");
            }
        }

        return settingsStage;
    }

    // Приватные методы
    /**
     * Блокирует главное окно при показе дочернего окна. При скрытии дочернего окна автоматически разблокирует главное окно
     * @param childStage дочернее окно
     */
    private void disableMainStage(Stage childStage)
    {
        // блокируем главное окно на момент показа дочернего
        Parent root = mainStage.getScene().getRoot();
        root.setDisable(true);

        // и снимаем блокировку, когда окно пропадает
        childStage.setOnHidden(event -> root.setDisable(false));
    }

    /**
     * Создать/выдать окно с
     * @return
     */
    public Stage createOrGetTrashStage()
    {
        if (trashStage != null && trashStage.isShowing())
        {
            trashStage.requestFocus();
            return trashStage;
        }

        try
        {
            trashStage = new Stage();

            FXMLLoader trashLoader = new FXMLLoader(ResourceHandler.getLayout("TrashViewer.fxml"), languageManager.getBundle());
            Parent root = trashLoader.load();

            trashStage.setScene(new Scene(root));
            trashStage.setTitle(languageManager.getString(StringKeys.TRASH_TITLE));
            trashStage.initModality(Modality.NONE);
            trashStage.initOwner(mainStage);

            return trashStage;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
