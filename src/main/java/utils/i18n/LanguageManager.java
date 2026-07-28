package utils.i18n;

import models.Language;
import models.SettingKeys;

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

public class LanguageManager
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

    private void initCurrentLanguage()
    {
        String langCode = settingsManager.get(SettingKeys.LOCALE);
        if (langCode != null)
            this.currentLanguage = getLanguageByCode(langCode);
        else
            this.currentLanguage = languages.isEmpty() ? null : languages.getFirst();

        loadBundle();
    }

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

    public Language getLanguageByCode(String code)
    {
        return languages.stream()
                .filter(lang -> lang.code().equals(code))
                .findFirst()
                .orElse(languages.isEmpty() ? null : languages.getFirst());
    }

    public void setCurrentLanguage(Language language)
    {
        this.currentLanguage = language;
        loadBundle();
        EventBus.publish(new LocaleChangedEvent());
    }

    public void setCurrentLanguage(String code)
    {
        setCurrentLanguage(getLanguageByCode(code));
    }

    public Language getCurrentLanguage() { return currentLanguage; }

    public List<Language> getAvailableLanguages()
    {
        return Collections.unmodifiableList(languages);
    }

    public String getString(String key)
    {
        if (bundle != null && bundle.containsKey(key))
            return bundle.getString(key);
        return "!" + key + "!";
    }

    public String getString(String key, Object... args)
    {
        String pattern = getString(key);
        return String.format(pattern, args);
    }

    public ResourceBundle getBundle() { return bundle; }

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
            System.err.println("123");
        }
    }
}
