package utils;


import models.Language;
import models.SettingKeys;
import utils.settingsUtils.SettingsManager;

import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class LanguageManager
{
    private List<Language> languages;
    private Language currentLanguage;
    private final SettingsManager settingsManager;

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
        try (InputStream stream = getClass().getResourceAsStream("/languages.yaml"))
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
