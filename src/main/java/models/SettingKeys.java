package models;

/**
 * Коды настроек
 * */
public class SettingKeys
{
    public static final String LOCALE = "locale";
    public static final String DEFAULT_DIRECTORY = "directory.default";

    public static class LastDirectory
    {
        public static final String LEFT = "directory.last.left";
        public static final String RIGHT = "directory.last.right";
    }

    public static final String LINUX_CONSOLE = "linux.console";
    public static final String LINUX_OPEN_COMMAND = "linux.open_command";
    public static final String LINUX_MOVE_TO_TRASH_COMMAND = "linux.move_to_trash_command";
}
