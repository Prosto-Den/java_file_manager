package app;


import controllers.SettingsController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import models.SettingKeys;
import models.StringKeys;
import monitors.ClipboardMonitor;
import resourceHandler.ResourceHandler;
import utils.FileSystemUtils;
import utils.LanguageManager;
import utils.settingsUtils.SettingsUtils;


/**
 * Вспомогательный класс приложения. Хранит общую для приложения информацию, отвечает за работу с модальными окнами
 * */
public class AppContext
{
    private static Stage mainStage; // главное окно приложения
    private static final String APP_NAME = ResourceHandler.getString(StringKeys.TITLE); // название приложения
    private static final String APP_FOLDER = createAppFolder(); // папка приложения
    private static boolean isAppPrepared = false; // флаг готовности приложения к работе. Нужен, чтобы
    // обезопасить метод prepareApp от повторного вызова

    /**
     * Выполнить первичную инициализацию для приложения. Будет определено главное окно приложения, загружены настройки,
     * определены локали и прочее. Обязателен для вызова перед началом работы программы
     * @param stage главное окно приложения
     * */
    public static void prepareApp(Stage stage)
    {
        if (!isAppPrepared)
        {
            mainStage = stage;
            SettingsUtils.loadSettings();
            LanguageManager.getInstance().setCurrentLanguage(SettingsUtils.get(SettingKeys.LOCALE));
            ResourceHandler.setLocale(LanguageManager.getInstance().getCurrentLanguage().toLocale());
            // TODO нужен ли этот монитор вообще? Может быть достаточно события на EventBus?
            ClipboardMonitor.start();

            isAppPrepared = true;
        }
    }

    // TODO нужно создать отдельный класс для работы с модальными окнами, в этом классе хранить его экземпляр,
    //  а в этом методы просто вызывать нужный метод из созданного класса
    /**
     * Создать окно для работы с настройками приложения
     * @return окно для работы с настройками
     * */
    public static Stage getSettingsStage()
    {
        Stage settingsStage = new Stage();

        if (mainStage != null)
        {
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(mainStage);

            try
            {
                FXMLLoader settingsLoader = new FXMLLoader(ResourceHandler.getLayout("SettingsLayout.fxml"),
                        ResourceHandler.getStringBundle());
                Parent root = settingsLoader.load();
                SettingsController controller = settingsLoader.getController();
                controller.setStage(settingsStage);

                Scene scene = new Scene(root);
                settingsStage.setScene(scene);
                settingsStage.setTitle(ResourceHandler.getString(StringKeys.SETTINGS_TITLE));
            }
            catch (IOException ex)
            {
                System.err.println("gnegli");
            }
        }

        return settingsStage;
    }

    /**
     * Получить путь к директории приложения
     * @return путь к директории
     * */
    public static String getAppFolder() { return APP_FOLDER; }

    /**
     * Получить название приложения
     * @return название приложения
     * */
    public static String getAppName() {return APP_NAME;}

    /**
     * Создать директорию приложения, если она ещё не создана
     * @return путь к директории приложения
     * */
    private static String createAppFolder()
    {
        String userFolder = System.getProperty("user.home");
        String path = FileSystemUtils.adjustPath(userFolder, APP_NAME);

        if (!FileSystemUtils.isExist(path))
            FileSystemUtils.createDir(path);
        return path;
    }
}
