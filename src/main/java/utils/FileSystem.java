package utils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;
import types.OSType;


/**
 * Класс для работы с файловой системой
 * */
public final class FileSystem
{
    private String currentPath; // абсолютный путь к текущей директории
    static private final OSType osType = calcOSType(); // тип ОС
    static private final String delimiter = calcDelimiter(); // разделитель для путей этой ОС

    /**
     * Конструктор по умолчанию. После создания будет указывать на корень системы
     * (C:\ у Windows и / у Linux)
     * */
    public FileSystem()
    {
        /*
        TODO добавить чтение текущего пути из настроек. При этом сам класс файловой системы ничего не
         должен знать про настройки, т.е. имеет смысл присылать путь извне (реализовал для этого конструктор)
        *
        */
        currentPath = getDefaultPath();
    }
    /**
     * Конструктор с передачей пути, на который объект будет указывать после создания.
     * Если директории по такому пути не существует, будет указывать на корень (C:\ для Windows и / для Linux)
     * */
    public FileSystem(String path)
    {
        currentPath = isDir(path) ? path : getDefaultPath();
    }

    /**
     * Выдать всё содержимое текущей директории
     * @param asNames если True, выдаст имена файлов, содержащихся в текущей директории.
     *                Если False - выдаст их абсолютные пути
     * @return Список с файлами, содержащимися внутри текущей директории
     * */
    public List<String> listCurrentPath(boolean asNames)
    {
        File dir = new File(currentPath);
        File[] files = dir.listFiles();
        List<String> result = new ArrayList<>();

        if (files != null)
            for (File file : files)
                result.add(asNames ? file.getName() : file.getPath());

        return result;
    }

    /**
     * Построить путь до файла. Путь строится путём конкатенации текущего пути с переданным именем файла
     * (с добавлением разделителя между ними). Не проверяет, существует ли файл по этому пути на самом деле
     * @return Путь к файлу
     * */
    public String buildPath(String fileName)
    {
        return String.join(delimiter, currentPath, fileName);
    }

    /**
     * Сменить текущую директорию
     * */
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * Выдать текущую директорию
     * @return текущая директория, на которую указывает объект
     * */
    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * Является ли текущая директория корнем системы?
     * */
    public boolean isCurrentPathRoot()
    {
        Pattern pattern = Pattern.compile("^([A-Z]:\\\\|/)$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(currentPath).matches();
    }

    /**
     * Пойти вперёд по файловому дереву. Строится путь текущая директория + переданное имя файла, затем
     * текущая директория заменяется на полученную. Не проверяет, существует ли полученный путь на самом деле
     * */
    public void goForward(String filename)
    {
       setCurrentPath(buildPath(filename));
    }

    /**
     * Пойти назад по файловому дереву. Получает родительскую директорию для текущей,
     * и заменяет текущую директорию на неё. Гарантированно получится существующая директория
     * */
    public void goBack()
    {
        setCurrentPath(getParentDir());
    }

    /**
     * Получить родительскую директорию для текущей директории. Гарантированно получится существующая директория
     * @return Абсолютный путь до родительской директории
     * */
    public String getParentDir()
    {
        File dir = new File(currentPath);
        return dir.getParent();
    }

    /**
     * Существует ли файл (директория) по этому пути
     * @param path Путь к файлу/директории
     * @return True если существует, иначе False
     * */
    static public boolean isExist(String path)
    {
        return new File(path).exists();
    }

    /**
     * Возвращает список со всеми логическими дисками системы (C:\, D:\ и т.д).
     * Вызов функции актуален только для Windows.
     * @return Список со всеми логическими дисками системы для Windows, пустой список для Linux.
     * */
    static public List<String> getLogicalDrives()
    {
        List<String> logicalDrives = new ArrayList<>();

        if (osType == OSType.WINDOWS)
        {
            for (char letter = 'A'; letter <= 'Z'; letter++)
            {
                String path = String.format("%s:\\", letter);
                if (isExist(path))
                    logicalDrives.add(path);
            }
        }

        return logicalDrives;
    }

    /**
     * Является ли переданный путь директорией
     * @param path Путь
     * @return True - если переданный путь существует и является директорией, иначе False
     * */
    static public boolean isDir(String path)
    {
        return new File(path).isDirectory();
    }

    static private String calcDelimiter()
    {
        if (osType == OSType.WINDOWS)
            return "\\";
        return "/";
    }

    private String getDefaultPath()
    {
        if (osType == OSType.WINDOWS)
            return "C:\\";
        return "/";
    }

    static private OSType calcOSType()
    {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows"))
            return OSType.WINDOWS;
        return OSType.LINUX;
    }
}
