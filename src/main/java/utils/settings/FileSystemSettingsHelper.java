package utils.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Вспомогательный класс для связи UUID экземпляра файловой системы с ID из настроек
 * */
public class FileSystemSettingsHelper
{
    // тут будем хранить связь UUID с путём
    private Map<String, String> fileSystemsKeys;
    private final SettingsManager settingsManager;

    public FileSystemSettingsHelper(SettingsManager settingsManager)
    {
        fileSystemsKeys = new HashMap<>();
        this.settingsManager = settingsManager;
    }


    /**
     * Установить связь между UUID файловой системой и ключом настроек, по которому хранится последняя открытая в этой
     * файловой системы директория
     * @param uuid UUID файловой системы, для которой ключ из настроек
     * @param value ключ из настроек, по которому хранится последняя открытая директория
     * */
    public void setFileSystemSettingsKey(String uuid, String value)
    {
        fileSystemsKeys.put(uuid, value);
    }

    /**
     * Установить путь в настройках для файловой системы
     * @param uuid UUID файловой системы, для которой нужно сохранить путь в настройках
     * @param path путь для сохранения
     * */
    public void setPath(String uuid, String path)
    {
        settingsManager.set(fileSystemsKeys.get(uuid), path);
    }
}
