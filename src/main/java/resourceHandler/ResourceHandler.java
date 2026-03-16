package resourceHandler;


import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import events.EventBus;
import events.LocaleChangedEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Общий класс для управления ресурсами программы (реализация паттерна фасад)
 * */
public class ResourceHandler
{
    private static volatile IconResourceManager iconManager;
    private static volatile StringResourceManager stringManager;
    private static volatile LayoutResourceManager layoutManager;
    private static volatile StylesResourceManager stylesManager;
    private static volatile DefaultSettingsResourceManager settingsManager;

    private static final String ICONS_PATH = "/icons";
    private static final String LAYOUTS_PATH = "/layouts";
    private static final String STYLES_PATH = "/styles";
    private static final String STRINGS_BASENAME = "strings";
    private static final String SETTINGS_PATH = "/settings/settings.properties";

    // Методы для работы с иконками
    /**
     * Метод для получения иконки
     * @param size размер иконки
     * @param iconName имя иконки
     * @return Image, если ресурс для иконки удалось найти, иначе null
     * */
    public static Image getIcon(@NotNull IconSize size,
                         @NotNull IconName iconName)
    {
        return getIconManager().getIcon(size, iconName);
    }


    // Методы для работы со строками
    /**
     * Установить новую локаль для программы
     * @param locale новая локаль
     * */
    public static void setLocale(Locale locale)
    {
        getStringManager().setLocale(locale);
        EventBus.publish(LocaleChangedEvent.class);
    }

    /**
     * Выдать текущую локаль
     * @return текущую локаль в качестве объекта Locale
     * */
    public static Locale getLocale() {return getStringManager().getLocale();}

    /**
     * Получить хранилище строковых ресурсов
     * @return хранилище строковых ресурсов
     * */
    public static ResourceBundle getStringBundle() {return getStringManager().getBundle();}

    //TODO немножко переделать метод, мне не нравится блок catch
    /**
     * Получить строковый ресурс по ключу
     * @param key ключ, по которому строковый ресурс расположен в хранилище
     * @return строковый ресурс, если он есть в хранилище
     * */
    public static String getString(String key)
    {
        try
        {
            return getStringBundle().getString(key);
        }
        catch (MissingResourceException ex)
        {
            System.err.println("Отсутствует ключ: " + key);
            return "!" + key + "!";
        }
    }

    /**
     * Получить строковый ресурс с форматированием
     * @param key ключ, по которому строковый ресурс расположен в хранилище
     * @param args аргументы для форматирования
     * @return отформатированная строка
     * */
    public static String getString(String key, Object... args)
    {
        String pattern = getString(key);
        return String.format(pattern, args);
    }


    // Методы работы с ресурсами лайаутов
    /**
     * Получить лайаут для виджета по названию файла
     * @param layoutFileName название файла с описанием интерфейса
     * @return URL, если файл с таким названием есть в ресурсах, иначе null
     * */
    @Nullable
    public static URL getLayout(String layoutFileName)
    {
        return getLayoutManager().getResource(layoutFileName);
    }

    // Методы работы с ресурсами стилей
    /**
     * Получить стиль для виджета по названию файла
     * @param styleFileName название файла со стилем
     * @return URL, если файл с таким названием есть в ресурсах, иначе null
     * */
    @Nullable
    public static URL getStyle(String styleFileName)
    {
        return getStylesManager().getResource(styleFileName);
    }

    // Методы работы с настройками
    public static InputStream getDefaultSettingsAsStream()
    {
        return getSettingsManager().getSettingsAsStream();
    }

    // Приватные методы класса
    /**
     * Выдать объект менеджера ресурсов иконок. Если его нет, сначала создаст его
     * @return менеджер ресурсов иконок
     * */
    private static IconResourceManager getIconManager()
    {
        if (iconManager == null)
            iconManager = new IconResourceManager(ICONS_PATH);
        return iconManager;
    }

    /**
     * Выдать объекта менеджера строковых ресурсов. Если его нет, сначала создаст его
     * @return менеджер строковых ресурсов
     * */
    private static StringResourceManager getStringManager()
    {
        if (stringManager == null)
            stringManager = new StringResourceManager(STRINGS_BASENAME);
        return stringManager;
    }

    /**
     * Выдать объект менеджера ресурсов интерфейса. Если его нет, сначала создаст его
     * @return менеджер ресурсов интерфейса
     * */
    private static LayoutResourceManager getLayoutManager()
    {
        if (layoutManager == null)
            layoutManager = new LayoutResourceManager(LAYOUTS_PATH);
        return layoutManager;
    }

    private static StylesResourceManager getStylesManager()
    {
        if (stylesManager == null)
            stylesManager = new StylesResourceManager(STYLES_PATH);
        return stylesManager;
    }

    private static DefaultSettingsResourceManager getSettingsManager()
    {
        if (settingsManager == null)
            settingsManager = new DefaultSettingsResourceManager(SETTINGS_PATH);
        return settingsManager;
    }
}


/**
 * Класс для работы с ресурсами иконок
 * */
class IconResourceManager
{
    private final String iconsPath;

    public IconResourceManager(String path) {iconsPath = path;}

    /**
     * Метод для получения иконки
     * @param size размер иконки
     * @param iconName имя иконки
     * @return Image, если ресурс для иконки удалось найти, иначе null
     * */
    @Nullable
    public Image getIcon(@NotNull IconSize size,
                         @NotNull IconName iconName)
    {
        URL url = getClass().getResource(String.join("/", iconsPath, size.getValue(), iconName.getValue()));
        if (url != null)
            return new Image(url.toString());
        return null;
    }
}


/**
 * Класс для управления строковыми ресурсами
 */
class StringResourceManager
{
    // хранилище строковых ресурсов
    private ResourceBundle bundle;
    // контроллер для хранилища строковых ресурсов
    private final StringResourceBundleControl control = new StringResourceBundleControl();
    // текущая локаль (в виде property)
    private Locale currentLocale;
    // значение локали по умолчанию
    private final Locale defaultLocale = Locale.of("en", "US");

    private final String bundleBaseName;

    /**
     * Конструктор по базовому имени файла строковых ресурсов. Локаль будет установлена в значение по умолчанию (en_US)
     * @param baseName базовое имя файла строковых ресурсов (название файла бех имени локали). Например, если файл
     *                 со строковыми ресурсами называется strings_en_US, то базовым именем является strings
     * */
    public StringResourceManager(String baseName)

    {
        bundleBaseName = baseName;
        setLocale(defaultLocale);
    }

    /**
     * Конструктор по базовому имени файла строковых ресурсов и локали
     * @param baseName базовое имя файла строковых ресурсов (название файла бех имени локали). Например, если файл
     *                 со строковыми ресурсами называется strings_en_US, то базовым именем является strings
     * @param locale объект локали. Определяет, строковые ресурсы какого языка нужно загрузить
     * */
    public StringResourceManager(String baseName, Locale locale)
    {
        bundleBaseName = baseName;
        setLocale(locale);
    }

    /**
     * Установить локаль. После установки локали будет произведена перезагрузка строковых ресурсов
     * @param locale новая используемая локаль
     * */
    public void setLocale(Locale locale)
    {
        currentLocale = locale;
        loadBundle();
    }

    /**
     * Выдать текущую локаль
     * @return текущую локаль в качестве объекта Locale
     * */
    public Locale getLocale() {return currentLocale;}

    /**
     * Получить хранилище строковых ресурсов
     * @return хранилище строковых ресурсов
     * */
    public ResourceBundle getBundle() {return bundle;}

    /**
     * Загрузить хранилище строковых ресурсов для данной локали
     * */
    private void loadBundle()
    {
        try
        {
            bundle = ResourceBundle.getBundle(bundleBaseName, currentLocale, control);
        }
        catch (MissingResourceException e)
        {
            System.err.println("Не найден файл ресурсов для локали: " + currentLocale);
            bundle = ResourceBundle.getBundle(bundleBaseName, defaultLocale, control);
        }
    }
}

/**
 * Класс для работы с файлами интерфейсов
 * */
class LayoutResourceManager
{
    private final String layoutPath;

    public LayoutResourceManager(String path) {layoutPath = path;}

    /**
     * Получить URL для интерфейса из ресурсов
     * @param layoutFileName название файла с интерфейсом
     * @return URL интерфейса или null, если файл с переданным именем не будет найден в ресурсах
     * */
    @Nullable
    public URL getResource(String layoutFileName)
    {
        return getClass().getResource(String.join("/", layoutPath, layoutFileName));
    }
}


class StylesResourceManager
{
    private final String stylesPath;

    public StylesResourceManager(String path) {stylesPath = path;}

    @Nullable
    public URL getResource(String styleFileName)
    {
        return getClass().getResource(String.join("/", stylesPath, styleFileName));
    }
}


class DefaultSettingsResourceManager
{
    private final String settingsPath;

    public DefaultSettingsResourceManager(String path) {settingsPath = path;}

    public InputStream getSettingsAsStream()
    {
        return getClass().getResourceAsStream(settingsPath);
    }
}