package utils.i18n;

import models.Language;
import models.SettingKeys;

import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import events.EventBus;
import events.LocaleChangedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import resourceHandler.StringResourceBundleControl;
import utils.settings.SettingsManager;

/**
 * Класс для работы с переводами приложения
 */
public final class LanguageManager
{
    // хранилище строковых ресурсов
    private static ResourceBundle bundle;
    // контроллер для хранилища строковых ресурсов
    private static final StringResourceBundleControl control = new StringResourceBundleControl();
    // текущая локаль (в виде property)
    private static Locale currentLocale;
    // значение локали по умолчанию
    private static final Locale DEFAULT_LOCALE = Locale.of("en", "US");
    private static final String STRINGS_BASENAME = "strings";

    private List<Language> languages;
    private Language currentLanguage;
    private final SettingsManager settingsManager;
    private final String LANGUAGES_FILE = "/languages.yaml";

    public LanguageManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
        loadLanguages();
        initCurrentLanguage();
    }

    /**
     * Загрузить язык из настроек
     */
    private void initCurrentLanguage()
    {
        String langCode = settingsManager.get(SettingKeys.LOCALE);
        if (langCode != null)
            this.currentLanguage = getLanguageByCode(langCode);
        else
            this.currentLanguage = languages.isEmpty() ? null : languages.getFirst();

        loadBundle();
    }

    /**
     * Загрузить языковые ресурсы из файла
     */
    private void loadBundle()
    {
        if (currentLanguage != null)
        {
            currentLocale = currentLanguage.toLocale();
            bundle = ResourceBundle.getBundle(STRINGS_BASENAME, currentLocale, control);
        }
        else
        {
            currentLocale = DEFAULT_LOCALE;
            bundle = ResourceBundle.getBundle(STRINGS_BASENAME, DEFAULT_LOCALE, control);
        }
    }

    /**
     * Получить информацию по языку по его коду
     * @param code код языка
     * @return инфомрация по языку
     */
    public @Nullable Language getLanguageByCode(String code)
    {
        return languages.stream()
                .filter(lang -> lang.code().equals(code))
                .findFirst()
                .orElse(languages.isEmpty() ? null : languages.getFirst());
    }

    /**
     * Установить язык приложения
     * @param language язык для установки
     */
    public void setCurrentLanguage(Language language)
    {
        this.currentLanguage = language;
        loadBundle();
        EventBus.publish(new LocaleChangedEvent());
    }

    /**
     * Установить язык приложения
     * @param code код языка
     */
    public void setCurrentLanguage(String code)
    {
        setCurrentLanguage(getLanguageByCode(code));
    }

    /**
     * Выдать текущий выставленный язык у приложения
     * @return текущий язык приложения
     */
    public Language getCurrentLanguage() { return currentLanguage; }

    /**
     * Выдать список доступных языков приложения
     * @return список доступных языков (переводов)
     */
    public List<Language> getAvailableLanguages()
    {
        return Collections.unmodifiableList(languages);
    }

    /**
     * Выдать строку из ресурса строк
     * @param key ключ ресурса
     * @return значение ресурса по данному ключу. Если по такому ключу ресурса нет, вернёт !key!
     */
    public String getString(String key)
    {
        if (bundle != null && bundle.containsKey(key))
            return bundle.getString(key);
        return "!" + key + "!";
    }

    /**
     * Выдать строку из ресурса строк и применить к ней форматирование
     * @param key ключ ресурса
     * @param args аргументы для вставки в строку
     * @return отформатированная строка, если по переданному ключу еть ресурс. Иначе вернёт !key!
     */
    public String getString(String key, Object... args)
    {
        String pattern = getString(key);
        return String.format(pattern, args);
    }


    /**
     * Выдать коллекцию строковых ресурсов
     * @return коллекция строковых ресурсов
     */
    public ResourceBundle getBundle() { return bundle; }

    /**
     * Загрузить доступные переводы
     */
    private void loadLanguages()
    {
        try (InputStream stream = getClass().getResourceAsStream(LANGUAGES_FILE))
        {
            this.languages = new ArrayList<>();
            Yaml yaml = new Yaml();
            Map<String, List<Map<String, String>>> data = yaml.load(stream);
            List<Map<String, String>> languages = data.get("languages");
            for (var language : languages)
            {
                Language lang = new Language(language.get("code"), language.get("name"), language.get("flag"));
                this.languages.add(lang);
            }
        }
        catch (IOException ex)
        {
            // TODO сюда логгирование
            System.err.println("123");
        }
    }
}
