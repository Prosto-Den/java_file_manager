package models;

/**
 * Коды настроек
 * */
public class SettingKeys
{
    public static final String LOCALE = "locale";

    public static class LastDirectory
    {
        public static final String LEFT = "last.directory.left";
        public static final String RIGHT = "last.directory.right";
    }

    public static class DefaultDirectories
    {
        public static final String WINDOWS = "default.directory.windows";
        public static final String LINUX = "default.directory.linux";
    }

    public static final String LINUX_CONSOLE = "linux.console";
    public static final String LINUX_OPEN_COMMAND = "linux.open_command";
}
