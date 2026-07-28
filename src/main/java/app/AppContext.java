package app;


import javafx.stage.Stage;
import models.StringKeys;
import types.OSType;
import utils.filesystem.FileSystemUtils;
import utils.i18n.LanguageManager;
import utils.platform.OSIntegrationService;
import utils.settings.FileSystemSettingsHelper;
import utils.settings.SettingsManager;
import utils.ui.WindowManager;


/**
 * Вспомогательный класс приложения. Хранит общую для приложения информацию, отвечает за работу с модальными окнами
 * */
public final class AppContext
{
    private static String appName; // название приложения
    private static String appFolder;// папка приложения
    private static SettingsManager settingsManager;
    private static FileSystemSettingsHelper settingsHelper;
    private static LanguageManager languageManager;
    private static OSIntegrationService integrationService;
    private static WindowManager windowManager;

    /**
     * Выполнить первичную инициализацию для приложения. Будет определено главное окно приложения, загружены настройки,
     * определены локали и прочее. Обязателен для вызова перед началом работы программы
     * @param stage главное окно приложения
     * */
    public static void init(Stage stage)
    {
        appFolder = createAppFolder();
        
        String settingsPath = FileSystemUtils.adjustPath(appFolder, "user_settings.properties");
        settingsManager = new SettingsManager(settingsPath);
        settingsHelper = new FileSystemSettingsHelper(settingsManager);
        languageManager = new LanguageManager(settingsManager);
        appName = languageManager.getString(StringKeys.TITLE);
        integrationService = new OSIntegrationService(OSType.getCurrentOsType(), settingsManager);
        windowManager = new WindowManager(stage, settingsManager, languageManager);
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

    /**
     * Создать окно для работы с настройками приложения.
     *
     * @return окно для работы с настройками
     */
    public static Stage getSettingsStage()
    {
        return windowManager.createSettingsStage();
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
