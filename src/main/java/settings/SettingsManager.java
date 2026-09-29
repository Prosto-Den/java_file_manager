package settings;


import utils.filesystem.FileSystemUtils;

import java.io.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import models.AppSettings;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import types.OSType;


// TODO стоит создать ещё один уровень абстракции. Если я откажусь от yaml, класс придётся переделывать (как я уже делал ранее...)
public final class SettingsManager
{
    private final Path settingsPath; // путь к настройкам пользователя
    private static final String SETTINGS_PATH = "/settings/default_settings.yaml"; // настройки по умолчанию
    private final Yaml yaml;
    private AppSettings settings;
    private AppSettings bufferSettings = null; // временные настройки (для того, чтобы сразу не применять изменения с UI)

    public SettingsManager(Path settingsPath)
    {
        LoaderOptions loaderOptions = new LoaderOptions();
        TagInspector tagInspector = tag -> tag.getClassName().equals(AppSettings.class.getName());
        loaderOptions.setTagInspector(tagInspector);

        this.settingsPath = settingsPath;
        yaml = new Yaml(new Constructor(AppSettings.class, loaderOptions));
        settings = loadSettings();
        loadSettings();
    }

    /**
     * Загрузить настройки из файла
     * */
    private AppSettings loadSettings()
    {
        AppSettings result = null;

        if (FileSystemUtils.isExist(settingsPath))
        {
            try (Reader reader = new InputStreamReader(Files.newInputStream(settingsPath), StandardCharsets.UTF_8))
            {
                result = yaml.loadAs(reader, AppSettings.class);
            }
            catch (Exception ex)
            {
                System.err.println("Не удалось загрузить настройки пользователя");
                result = loadDefaultSettings();
            }
        }
        else
            result = loadDefaultSettings();

        if (result == null)
        {
            result = new AppSettings();
            String defaultDir;
            if (OSType.is(OSType.WINDOWS))
                defaultDir = result.settings.defaultDir.windows;
            else
                defaultDir = result.settings.defaultDir.linux;

            result.session.left.tabs.add(defaultDir);
            result.session.right.tabs.add(defaultDir);
        }

        return result;
    }

    /**
     * Сохранить пользовательские настройки в файл
     * */
    public void saveSettings()
    {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(settingsPath), StandardCharsets.UTF_8))
        {
            yaml.dump(settings, writer);
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось сохнарить настройки :(");
        }
    }

    public AppSettings getSettings() 
    { 
        if (bufferSettings != null)
            return bufferSettings;
        return settings; 
    }

    /**
     * Начать редактирование настроек приложения
     */
    public void beginEdit()
    {
        String settingsDump = yaml.dump(settings);
        bufferSettings = yaml.loadAs(settingsDump, AppSettings.class);
    }
    
    /**
     * Применить настройки после редактирования
     */
    public void commitEdit()
    {
        if (bufferSettings != null)
        {
            settings = bufferSettings;
            bufferSettings = null;
            saveSettings();
        }
    }

    /**
     * Откатить редактируемые изменения 
     */
    public void rollbackEdit()
    {
        bufferSettings = null;
    }

    // Приватные методы

    /**
     * Загрузить настройки по умолчанию
     */
    private AppSettings loadDefaultSettings()
    {
        try (InputStream stream = getClass().getResourceAsStream(SETTINGS_PATH))
        {
            if (stream != null)
            {
                try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
                {
                    return yaml.loadAs(reader, AppSettings.class);
                }
                catch (Exception ex)
                {
                    System.err.println("Не удалось загрузить настройки по умолчанию :(");
                }
            }
        }
        catch (IOException ex)
        {
            // маловероятно, но пускай будет
            System.err.println("Не удалось загрузить настройки по умолчанию");
        }

        return null;
    }
}
