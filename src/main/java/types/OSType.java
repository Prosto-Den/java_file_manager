package types;


public enum OSType
{
    WINDOWS,
    LINUX,
    UNKNOWN;

    private static OSType CURRENT_OS;

    static
    {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win"))
            CURRENT_OS = WINDOWS;
        else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix"))
            CURRENT_OS = LINUX;
        else
            CURRENT_OS = UNKNOWN;
    }

    /**
     * Получить тип ОС устройства
     * @return тип ОС устройства
     */
    public static OSType getCurrentOsType() { return CURRENT_OS; }

    /**
     * Проверить тип операционной системы
     * @param type тип операционной системы
     * @return true, если тип системы совпадает с type, иначе false
     */
    public static boolean is(OSType type)
    {
        return CURRENT_OS == type;
    }
}
