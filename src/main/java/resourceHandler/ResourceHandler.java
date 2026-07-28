package resourceHandler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import utils.FileSystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Общий класс для управления ресурсами программы (реализация паттерна фасад)
 * */
public final class ResourceHandler
{
    // хранилище строковых ресурсов
    private static ResourceBundle bundle;
    // контроллер для хранилища строковых ресурсов
    private static final StringResourceBundleControl control = new StringResourceBundleControl();
    // текущая локаль (в виде property)
    private static Locale currentLocale;
    // значение локали по умолчанию
    private static final Locale DEFAULT_LOCALE = Locale.of("en", "US");

    // кеш иконок
    private static final Map<String, Image> iconCache = new HashMap<>();

    // пути к ресурсам
    private static final String ICONS_PATH = "/icons";
    private static final String LAYOUTS_PATH = "/layouts";
    private static final String STYLES_PATH = "/styles";
    private static final String STRINGS_BASENAME = "strings";
    //private static final String SETTINGS_PATH = "/settings/default_settings.properties";
    //private static final String LANGUAGES_PATH = "/languages.yaml";

    // Методы для работы с иконками
    /**
     * Метод для получения иконки
     * @param size размер иконки
     * @param iconName имя иконки
     * @return Image, если ресурс для иконки удалось найти, иначе null
     * */
    public static @Nullable Image getIcon(@NotNull IconSize size,
                         @NotNull IconName iconName)
    {
        return getIcon(size, iconName.getValue());
    }

    public static @Nullable Image getIcon(IconSize size, String name)
    {
        if (size == null || name == null || name.isBlank())
            return null;

        //String path = FileSystemUtils.adjustPath(name, name)
        String path = String.join("/", ICONS_PATH, size.getValue(), name);
        if (iconCache.containsKey(path))
            return iconCache.get(path);

        URL url = ResourceHandler.class.getResource(path);
        Image image = null;
        if (url != null)
        {
            image = new Image(url.toString());
            iconCache.put(path, image);
            return image;
        }

        return image;
    }

    // Методы для работы со строками
    public static ResourceBundle getStringBundle()
    {
        return bundle;
    }

    /**
     * Установить новую локаль для программы
     * @param locale новая локаль
     * */
    public static void setLocale(Locale locale)
    {
        currentLocale = locale;

        try
        {
            bundle = ResourceBundle.getBundle(STRINGS_BASENAME, currentLocale, control);
        }
        // TODO сюда логгирование
        catch (MissingResourceException ex)
        {
            currentLocale = DEFAULT_LOCALE;
            bundle = ResourceBundle.getBundle(STRINGS_BASENAME, currentLocale, control);
        }
    }

    /**
     * Выдать текущую локаль
     * @return текущую локаль в качестве объекта Locale
     * */
    public static Locale getLocale() 
    {
        return currentLocale != null ? currentLocale : DEFAULT_LOCALE;
    }

    /**
     * Получить строковый ресурс по ключу
     * @param key ключ, по которому строковый ресурс расположен в хранилище
     * @return строковый ресурс, если он есть в хранилище
     * */
    public static String getString(String key)
    {
        if (bundle != null && bundle.containsKey(key))
            return bundle.getString(key);
        // на случай, если перевода не окажется
        return "!" + key + "!";
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
        String path = FileSystemUtils.adjustPath(LAYOUTS_PATH, layoutFileName);
        return ResourceHandler.class.getResource(path);
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
        String path = String.join("/", STYLES_PATH, styleFileName);
        return ResourceHandler.class.getResource(path);
    }
}