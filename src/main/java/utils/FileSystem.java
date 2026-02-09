package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import types.OSType;
import javafx.beans.property.StringProperty;


/**
 * Класс для работы с файловой системой
 * */
public final class FileSystem
{
    // Абсолютный путь к текущей директории. Хранится в property из JavaFX, так как таким образом можно легко
    // отследить изменения текущей директории (для передачи данных между виджетами)
    private final StringProperty currentPath = new SimpleStringProperty("");
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
        currentPath.setValue(getDefaultPath());
    }
    /**
     * Конструктор с передачей пути, на который объект будет указывать после создания.
     * Если директории по такому пути не существует, будет указывать на корень (C:\ для Windows и / для Linux)
     * */
    public FileSystem(String path)
    {
        currentPath.setValue(isDir(path) ? path : getDefaultPath());
    }

    /**
     * Выдать всё содержимое текущей директории
     * @param asNames если True, выдаст имена файлов, содержащихся в текущей директории.
     *                Если False - выдаст их абсолютные пути
     * @return Список с файлами, содержащимися внутри текущей директории
     * */
    public List<String> listCurrentPath(boolean asNames)
    {
        File dir = new File(currentPath.getValue());
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
        return String.join(delimiter, currentPath.getValue(), fileName);
    }

    /**
     * Сменить текущую директорию
     * */
    public void setCurrentPath(String currentPath)
    {
        if (osType == OSType.WINDOWS)
            currentPath = currentPath.replace("\\\\", "\\");
        this.currentPath.setValue(currentPath);
    }

    /**
     * Выдать текущую директорию
     * @return текущая директория, на которую указывает объект
     * */
    public String getCurrentPath() {
        return currentPath.getValue();
    }

    /**
     * Является ли текущая директория корнем системы?
     * */
    public boolean isCurrentPathRoot()
    {
        Pattern pattern = Pattern.compile("^([A-Z]:\\\\|/)$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(currentPath.getValue()).matches();
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
        return new File(currentPath.getValue()).getParent();
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

    //TODO возможно стоит сделать метод более гибким (например, задать возможность выбора размерности)
    /**
     * Получить строку с информацией о размере файла
     * @param filePath Абсолютный путь к файлу
     * @return Строка с размером файла
     * */
    static public String getFileSize(String filePath)
    {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = new File(filePath).length();

        while (size >= 1024 && unitIndex < units.length - 1)
        {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * Получить имя файла из абсолютного пути к нему
     * @param filePath Абсолютный путь к файлу
     * @return Имя файла
     * */
    static public String getFilenameFromPath(String filePath)
    {
        return new File(filePath).getName();
    }

    /**
     * Получить дату последнего изменения файла
     * */
    static public String lastModifiedDate(String filePath)
    {
        //TODO формат для даты вынести в строковые ресурсы
        long lastModified = new File(filePath).lastModified();
        Date date = new Date(lastModified);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);
    }

    /**
     * Выдать property текущего пути
     * @return Property текущего пути
     * */
    public StringProperty getCurrentPathProperty() {return currentPath;}

    static private String calcDelimiter()
    {
        if (osType == OSType.WINDOWS)
            return "\\";
        return "/";
    }

    /**
     * Выдать корень файловой системы (C:\ для Windows и / для Linux)
     * @return Корень системы
     * */
    private String getDefaultPath()
    {
        if (osType == OSType.WINDOWS)
            return "C:\\";
        return "/";
    }

    /**
     * Определить операционную систему. Пока все ОС делятся на Windows и Linux (всё что не Windows, то Linux)
     * @return Тип ОС
     * */
    static private OSType calcOSType()
    {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows"))
            return OSType.WINDOWS;
        return OSType.LINUX;
    }
}
