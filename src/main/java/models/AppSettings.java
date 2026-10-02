package models;


import java.util.ArrayList;
import java.util.List;


/**
 * Модель настроек приложения
 * AppSettings
 */
public class AppSettings 
{
    public SettingsData settings = new SettingsData(); // основные настройки
    public SessionData session = new SessionData(); // настройки сессии

    /**
     * Основные настройки приложения
     * SettingsData
     */
    public static class SettingsData
    {
        public String locale = "ru_RU";
        public DefaultDir defaultDir = new DefaultDir();
        public LinuxCommands linuxCommands = new LinuxCommands();
    }

    /**
     * Директории по умолчанию
     * DefaultDir
     */
    public static class DefaultDir
    {
        public String windows = "C:\\";
        public String linux = "/";
    }

    /**
     * Команды, используемы для линукса
     * LinuxCommands
     */
    public static class LinuxCommands
    {
        public String console = "foot -D";
        public String open = "xdg-open";
        public String moveToTrash = "gio trash";
    }

    /**
     * Настройки сессии
     * SessionData
     */
    public static class SessionData
    {
        public SideData left = new SideData();
        public SideData right = new SideData();
    }

    /**
     * Данные сессии по каждой панели 
     * SideData
     */
    public static class SideData
    {
        public int activeIndex = 0;
        public List<String> tabs = new ArrayList<>();
    }
}
