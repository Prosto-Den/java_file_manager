package utils;


import models.Language;
import models.SettingKeys;
import utils.settingsUtils.SettingsManager;

import org.yaml.snakeyaml.Yaml;

import events.EventBus;
import events.LocaleChangedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import resourceHandler.ResourceHandler;


public class LanguageManager
{
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
            setCurrentLanguage(langCode);
        else
            setCurrentLanguage(languages.isEmpty() ? null : languages.getFirst());
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
        ResourceHandler.setLocale(language.toLocale());
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

//    private void notifyLanguageChanged()
//    {
//        EventBus.publish(new LanguageChangedEvent(currentLanguage));
//    }
}
