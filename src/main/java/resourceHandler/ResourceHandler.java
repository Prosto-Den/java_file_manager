package resourceHandler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import utils.FileSystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Общий класс для управления ресурсами программы (реализация паттерна фасад)
 * */
public final class ResourceHandler
{
    // кеш иконок
    private static final Map<String, Image> iconCache = new HashMap<>();

    // пути к ресурсам
    private static final String ICONS_PATH = "/icons";
    private static final String LAYOUTS_PATH = "/layouts";
    private static final String STYLES_PATH = "/styles";
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