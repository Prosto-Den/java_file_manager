package resourceHandler;


import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Общий класс для управления ресурсами программы (реализация паттерна фасад)
 * */
public class ResourceHandler
{
    static private IconResourceManager iconManager;
    static private volatile StringResourceManager stringManager;


    // Методы для работы с иконками
    /**
     * Метод для получения иконки
     * @param size размер иконки
     * @param iconName имя иконки
     * @return Image, если ресурс для иконки удалось найти, иначе null
     * */
    static public Image getIcon(@NotNull IconSize size,
                         @NotNull IconName iconName)
    {
        return getIconManager().getIcon(size, iconName);
    }


    // Методы для работы со строками
    /**
     * Установить новую локаль для программы
     * @param locale новая локаль
     * */
    static public void setLocale(Locale locale)
    {
        getStringManager().setLocale(locale);
    }

    /**
     * Выдать текущую локаль
     * @return текущую локаль в качестве объекта Locale
     * */
    static public Locale getLocale() {return getStringManager().getLocale();}

    /**
     * Выдать текущую локаль в качестве свойства. Позволяет привязывать другие элементы к изменению языка
     * @return текущую локаль в качестве property
     * */
    static public ObjectProperty<Locale> localeProperty() {return getStringManager().localeProperty();}

    /**
     * Получить хранилище строковых ресурсов
     * @return хранилище строковых ресурсов
     * */
    static public ResourceBundle getStringBundle() {return getStringManager().getBundle();}

    /**
     * Получить строковый ресурс по ключу
     * @param key ключ, по которому строковый ресурс расположен в хранилище
     * @return строковый ресурс, если он есть в хранилище
     * */
    static public String getString(String key)
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
    static public String getString(String key, Object... args)
    {
        String pattern = getString(key);
        return String.format(pattern, args);
    }

    /**
     * Подписать слушателя на изменения локали
     * @param listener слушатель изменения локали
     * */
    static public void addStringListener(Runnable listener) {getStringManager().addListener(listener);}

    /**
     * Отписать слушателя от изменения локали
     * @param listener слушатель изменения локали
     * */
    static public void removeStringListener(Runnable listener) {getStringManager().removeListener(listener);}

    // Приватные методы класса
    /**
     * Выдать объект менеджера ресурсов иконок. Если его нет, сначала создаст его
     * @return менеджер ресурсов иконок
     * */
    static private IconResourceManager getIconManager()
    {
        if (iconManager == null)
            iconManager = new IconResourceManager();
        return iconManager;
    }

    /**
     * Выдать объекта менеджера строковых ресурсов. Если его нет, сначала создаст его
     * @return менеджер строковых ресурсов
     * */
    static private StringResourceManager getStringManager()
    {
        if (stringManager == null)
            stringManager = new StringResourceManager();
        return stringManager;
    }
}


/**
 * Класс для работы с ресурсами иконок
 * */
class IconResourceManager
{
    static private final String iconsPath = "/icons";

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
    private final ObjectProperty<Locale> currentLocaleProperty = new SimpleObjectProperty<>();
    // слушатели изменения локали
    private final ObservableSet<Runnable> listeners = FXCollections.observableSet();

    // значение локали по умолчанию
    private static final Locale defaultLocale = Locale.of("en", "US");

    public StringResourceManager()
    {
        currentLocaleProperty.addListener((obs, oldVal, newVal) -> {
            loadBundle(newVal);
            notifyListeners();
        });

        setLocale(defaultLocale);
        loadBundle(getLocale());
    }

    /**
     * Установить локаль. После установки локали будет произведена перезагрузка строковых ресурсов
     * @param locale новая используемая локаль
     * */
    public void setLocale(Locale locale) { currentLocaleProperty.set(locale); }

    /**
     * Выдать текущую локаль
     * @return текущую локаль в качестве объекта Locale
     * */
    public Locale getLocale() {return currentLocaleProperty.get();}

    /**
     * Выдать текущую локаль в качестве свойства. Позволяет привязывать другие элементы к изменению языка
     * @return текущую локаль в качестве property
     * */
    public ObjectProperty<Locale> localeProperty() {return currentLocaleProperty;}

    /**
     * Получить хранилище строковых ресурсов
     * @return хранилище строковых ресурсов
     * */
    public ResourceBundle getBundle() {return bundle;}

    /**
     * Добавить слушателя обновления локали
     * @param listener слушатель изменения локали
     * */
    public void addListener(Runnable listener) {listeners.add(listener);}

    /**
     * Удалить слушателя изменения локали
     * @param listener слушатель изменения локали
     * */
    public void removeListener(Runnable listener) {listeners.remove(listener);}

    /**
     * Оповестить всех слушателей об изменении локали
     * */
    private void notifyListeners()
    {
        listeners.forEach(Runnable::run);
    }

    /**
     * Загрузить хранилище строковых ресурсов для данной локали
     * @param locale локаль, для которой нужно загрузить хранилище. Если файл для такой локали будет не найден,
     *               будет загружена локаль по умолчанию
     * */
    private void loadBundle(Locale locale)
    {
        try
        {
            bundle = ResourceBundle.getBundle("strings", locale, control);
        }
        catch (MissingResourceException e)
        {
            //TODO вынести локаль по умолчанию в соответствующее поле класса
            System.err.println("Не найден файл ресурсов для локали: " + locale);
            bundle = ResourceBundle.getBundle("strings", Locale.of("ru", "RU"), control);
        }
    }
}