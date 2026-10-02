package app;


import javafx.stage.Stage;
import javafx.scene.Scene;
import events.KeyEvent;
import monitors.ClipboardMonitor;
import settings.*;
import types.OSType;
import utils.filesystem.FileSystemUtils;
import utils.i18n.LanguageManager;
import utils.platform.OSIntegrationService;
import utils.ui.*;
import utils.ui.context.ContextMenuManager;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import events.EventBus;

/**
 * Вспомогательный класс приложения. Хранит общую для приложения информацию, отвечает за работу с модальными окнами
 * */
public final class AppContext
{
    private static final String APP_NAME = "Prosto File Manager"; // название приложения
    private static final String USER_SETTINGS_FILENAME = "user_settings.yaml";
    private static Path appFolder;// папка приложения
    private static SettingsManager settingsManager;
    private static LanguageManager languageManager;
    private static OSIntegrationService integrationService;
    private static WindowManager windowManager;
    private static ContextMenuManager contextMenuManager;

    private static final ExecutorService threadPool = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("FileManager-Worker-" + thread.threadId());
        return thread;
    });

    // TODO пока сойдёт, но если их станет много, надо будет сделать отдельный менеджер
    private static DataFormat panelDataFormat;

    private static Stage appWindow;

    /**
     * Выполнить первичную инициализацию для приложения. Будет определено главное окно приложения, загружены настройки,
     * определены локали и прочее. Обязателен для вызова перед началом работы программы
     * @param stage главное окно приложения
     * */
    public static void init(Stage stage)
    {
        appWindow = stage;
        appFolder = createAppFolder();
        
        Path settingsPath = appFolder.resolve(USER_SETTINGS_FILENAME);
        settingsManager = new SettingsManager(settingsPath);
        languageManager = new LanguageManager(settingsManager.getSettings());
        integrationService = new OSIntegrationService(OSType.getCurrentOsType(), settingsManager.getSettings());
        windowManager = new WindowManager(stage, settingsManager, languageManager);
        contextMenuManager = new ContextMenuManager();

        panelDataFormat = new DataFormat("application/panel");
        ClipboardMonitor.start();
    }

    public static void shutdown()
    {
        getSettings().saveSettings();
        threadPool.shutdownNow();
    }

    public static void initKeyboardEvent(Scene scene)
    {
        if (scene != null)
        {
            scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SHIFT)
                    EventBus.publish(new KeyEvent(event.getCode(), true));
            });

            scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> {
                if (event.getCode() == KeyCode.SHIFT)
                    EventBus.publish(new KeyEvent(event.getCode(), false));
            });
        }
    }

    /**
     * Выдать менеджер настроек приложения
     * @return менеджер настроек
     */
    public static SettingsManager getSettings() { return settingsManager; }

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
     * Выдать менеджер контекстного меню
     * @return менеджер контекстного меню
     */
    public static WindowManager getWindowManager() { return windowManager; }

    /**
     * Выдать менеджер контекстного меню
     * @return менеджер контекстного меню
     */
    public static ContextMenuManager getContextMenuManager() { return contextMenuManager; }

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
    public static Path getAppFolder() { return appFolder; }

    /**
     * Получить название приложения
     * @return название приложения
     * */
    public static String getAppName() {return APP_NAME;}

    public static DataFormat getPanelDataFormat() {return panelDataFormat;}

    public static Stage getMainWindow() { return appWindow; }

    public static ExecutorService getThreadPool() { return threadPool; }

    // Приватные методы

    /**
     * Создать директорию приложения, если она ещё не создана
     * @return путь к директории приложения
     * */
    private static Path createAppFolder()
    {
        Path userFolder = Path.of(System.getProperty("user.home"));
        Path path = userFolder.resolve(APP_NAME);

        if (!FileSystemUtils.isExist(path))
            FileSystemUtils.createDir(path);
        return path;
    }
}
