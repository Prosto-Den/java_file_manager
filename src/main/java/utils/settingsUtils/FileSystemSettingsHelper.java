package utils.settingsUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Вспомогательный класс для связи UUID экземпляра файловой системы с ID из настроек
 * */
public class FileSystemSettingsHelper
{
    // тут будем хранить связь UUID с путём
    private static final Map<String, String> fileSystemsKeys = new HashMap<>();

    /**
     * Установить связь между UUID файловой системой и ключом настроек, по которому хранится последняя открытая в этой
     * файловой системы директория
     * @param uuid UUID файловой системы, для которой ключ из настроек
     * @param value ключ из настроек, по которому хранится последняя открытая директория
     * */
    public static void setFileSystemSettingsKey(String uuid, String value)
    {
        fileSystemsKeys.put(uuid, value);
    }

    /**
     * Установить путь в настройках для файловой системы
     * @param uuid UUID файловой системы, для которой нужно сохранить путь в настройках
     * @param path путь для сохранения
     * */
    public static void setPath(String uuid, String path)
    {
        SettingsUtils.set(fileSystemsKeys.get(uuid), path);
    }
}
