package utils.settingsUtils;


import app.AppContext;
import resourceHandler.ResourceHandler;
import utils.FileSystemUtils;

import java.io.*;
import java.util.Properties;


/**
 * Класс для работы с настройками приложения
 * */
public class SettingsUtils
{
    private static final String SETTINGS_PATH = FileSystemUtils.adjustPath(
            AppContext.getAppFolder(), "settings.properties");
    private static final Properties properties = new Properties();

    /**
     * Загрузить настройки из файла
     * */
    public static void loadSettings()
    {
        if (FileSystemUtils.isExist(SETTINGS_PATH))
        {
            try (FileInputStream fis = new FileInputStream(SETTINGS_PATH))
            {
                properties.load(fis);
            }
            catch (IOException ex)
            {
                System.err.println(
                        "Не удалось загрузить файл пользовательских настроек. Будут применены настройки по умолчанию"
                );
                loadDefaultSettings();
            }
        }
        else
            loadDefaultSettings();
    }

    /**
     * Сохранить пользовательские настройки в файл
     * */
    public static void saveSettings()
    {
        if (!FileSystemUtils.isExist(SETTINGS_PATH))
            FileSystemUtils.createFile(SETTINGS_PATH);

        try (FileOutputStream fos = new FileOutputStream(SETTINGS_PATH))
        {
            properties.store(fos, "Prosto File Manager settings");
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось сохранить пользовательские настройки :(");
        }
    }

    /**
     * Получить значение из настроек по ключу. Вернёт null, если значения по этому ключу нет
     * @param key ключ, по которому нужно искать поле настроек
     * */
    public static String get(String key)
    {
        return properties.getProperty(key);
    }

    /**
     * Выставить значение в настройках. Изменит значение только если оно отличается от того, что записано в настройках
     * в данный момент
     * @param key ключ
     * @param value значение
     * @return true, если значение было изменено, иначе false
     * */
    public static boolean set(String key, String value)
    {
        boolean result = false;
        String oldValue = properties.getProperty(key);
        if (!oldValue.equals(value))
        {
            properties.setProperty(key, value);
            result = true;
        }
        return result;
    }

    public static Properties getSettings() {return properties;}

    private static void loadDefaultSettings()
    {
        try (InputStream stream = ResourceHandler.getDefaultSettingsAsStream())
        {
            properties.load(stream);
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось загрузить файл настроек :(");
        }
    }
}
