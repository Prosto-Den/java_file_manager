package utils;


import resourceHandler.ResourceHandler;

import java.io.*;
import java.util.Properties;


/**
 * Класс для работы с настройками приложения
 * */
public class SettingsUtils
{
    private static final String SETTINGS_PATH = FileSystemUtils.adjustPath(
            FileSystemUtils.getAppFolder(), "settings.properties");
    private static Properties properties = new Properties();

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
     * Выставить значение в настройках
     * @param key ключ
     * @param value значение
     * */
    public static void set(String key, String value)
    {
        properties.setProperty(key, value);
    }

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
