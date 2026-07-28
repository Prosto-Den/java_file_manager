package app;


import controllers.SettingsController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import models.StringKeys;
import resourceHandler.ResourceHandler;
import types.OSType;
import utils.FileSystemUtils;
import utils.LanguageManager;
import utils.OSIntegrationService;
import utils.settingsUtils.FileSystemSettingsHelper;
import utils.settingsUtils.SettingsManager;


/**
 * Вспомогательный класс приложения. Хранит общую для приложения информацию, отвечает за работу с модальными окнами
 * */
public final class AppContext
{
    private static Stage mainStage; // главное окно приложения
    private static String appName; // название приложения
    private static String appFolder;// папка приложения
    private static SettingsManager settingsManager;
    private static FileSystemSettingsHelper settingsHelper;
    private static LanguageManager languageManager;
    private static OSIntegrationService integrationService;

    /**
     * Выполнить первичную инициализацию для приложения. Будет определено главное окно приложения, загружены настройки,
     * определены локали и прочее. Обязателен для вызова перед началом работы программы
     * @param stage главное окно приложения
     * */
    public static void init(Stage stage)
    {
        mainStage = stage;
        appFolder = createAppFolder();
        
        String settingsPath = FileSystemUtils.adjustPath(appFolder, "user_settings.properties");
        settingsManager = new SettingsManager(settingsPath);
        settingsHelper = new FileSystemSettingsHelper(settingsManager);
        languageManager = new LanguageManager(settingsManager);
        appName = languageManager.getString(StringKeys.TITLE);
        integrationService = new OSIntegrationService(OSType.getCurrentOsType(), settingsManager);
    }

    /**
     * Выдать менеджер настроек приложения
     * @return менеджер настроек
     */
    public static SettingsManager getSettings() { return settingsManager; }

    /**
     * Выдать вспомогательный менеджер для работы с настройками
     * @return вспомогательный менеджер работы с настройками
     */
    public static FileSystemSettingsHelper getSettingsHelper() { return settingsHelper; }

    /**
     * Выдать менеджер переводов приложения
     * @return менеджер переводов
     */
    public static LanguageManager getLanguageManager() { return languageManager; }

    /**
     * Выдать сервис интергации с ОС
     * @return сервис интеграции
     */
    public static OSIntegrationService getIntegrationService() { return integrationService; }

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
                System.err.println("gnegli");
            }
        }

        return settingsStage;
    }

    /**
     * Получить путь к директории приложения
     * @return путь к директории
     * */
    public static String getAppFolder() { return appFolder; }

    /**
     * Получить название приложения
     * @return название приложения
     * */
    public static String getAppName() {return appName;}

    // Приватные методы

    /**
     * Создать директорию приложения, если она ещё не создана
     * @return путь к директории приложения
     * */
    private static String createAppFolder()
    {
        String userFolder = System.getProperty("user.home");
        String path = FileSystemUtils.adjustPath(userFolder, appName);

        if (!FileSystemUtils.isExist(path))
            FileSystemUtils.createDir(path);
        return path;
    }
}
