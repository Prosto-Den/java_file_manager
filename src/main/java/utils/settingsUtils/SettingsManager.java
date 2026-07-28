package utils.settingsUtils;


import models.StringKeys;
import resourceHandler.ResourceHandler;
import utils.FileSystemUtils;

import java.io.*;
import java.util.Properties;



public class SettingsManager
{
    private final String settingsPath; // путь к настройкам пользователя
    private final Properties properties; // настройки
    private Properties bufferProperties = null; // временные настройки (для того, чтобы сразу не применять изменения с UI)
    private static final String SETTINGS_PATH = "/settings/default_settings.properties";

    public SettingsManager(String settingsPath)
    {
        this.settingsPath = settingsPath;
        this.properties = new Properties();
        loadSettings();
    }

    /**
     * Загрузить настройки из файла
     * */
    public void loadSettings()
    {
        if (FileSystemUtils.isExist(settingsPath))
        {
            try (FileInputStream fis = new FileInputStream(settingsPath))
            {
                properties.load(fis);
            }
            catch (IOException ex)
            {
                System.err.println("Не удалось загрузить пользовательские настройки из файла");
                loadDefaultSettings();
            }
        }
        else
            loadDefaultSettings();
    }

    /**
     * Сохранить пользовательские настройки в файл
     * */
    public void saveSettings()
    {
        if (!FileSystemUtils.isExist(settingsPath))
            FileSystemUtils.createFile(settingsPath);

        try (FileOutputStream fos = new FileOutputStream(settingsPath))
        {
            properties.store(fos, "Prosto File Manager settings");
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось сохранить пользовательские настройки в файл");
        }
    }

    /**
     * Получить значение из настроек по ключу. Вернёт null, если значения по этому ключу нет
     * @param key ключ, по которому нужно искать поле настроек
     * */
    public String get(String key)
    {
        if (bufferProperties != null)
            return bufferProperties.getProperty(key);
        return properties.getProperty(key);
    }

    /**
     * Выставить значение в настройках. Изменит значение только если оно отличается от того, что записано в настройках
     * в данный момент
     * @param key ключ
     * @param value значение
     * @return true, если значение было изменено, иначе false
     * */
    public boolean set(String key, String value)
    {
        Properties target = (bufferProperties != null) ? bufferProperties : properties;
        boolean result = false;
        String oldValue = target.getProperty(key);
        if (oldValue == null || !oldValue.equals(value))
        {
            target.setProperty(key, value);
            result = true;
        }
        return result;
    }

    /**
     * Начать редактирование настроек приложения
     */
    public void beginEdit()
    {
        bufferProperties = new Properties();
        bufferProperties.putAll(properties);
    }
    
    /**
     * Применить настройки после редактирования
     */
    public void commitEdit()
    {
        if (bufferProperties != null)
        {
            properties.clear();
            properties.putAll(bufferProperties);
            bufferProperties = null;
            saveSettings();
        }
    }

    /**
     * Откатить редактируемые изменения 
     */
    public void rollbackEdit()
    {
        bufferProperties = null;
    }

    // Приватные методы

    /**
     * Загрузить настройки по умолчанию
     */
    private void loadDefaultSettings()
    {
        try (InputStream stream = getClass().getResourceAsStream(SETTINGS_PATH))
        {
            properties.load(stream);
        }
        catch (IOException ex)
        {
            // маловероятно, но пускай будет
            System.err.println("Не удалось загрузить настройки по умолчанию");
        }
    }
}
