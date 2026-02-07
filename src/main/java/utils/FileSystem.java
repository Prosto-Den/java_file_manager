package utils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;


/**
 * Класс для работы с файловой системой
 * */
public final class FileSystem
{
    private String currentPath; // абсолютный путь к текущей директории
    static private String osName; // имя ОС
    static private String delimiter; // разделитель для путей этой ОС

    /**
     * Конструктор по умолчанию. После создания будет указывать на корень системы
     * (C:\ у Windows и / у Linux)
     * */
    public FileSystem()
    {
        /*
        TODO добавить чтение текущего пути из настроек. При этом сам класс файловой системы ничего не
         должен знать про настройки, т.е. имеет смысл присылать путь извне (реализовал для этого конструктор)
        *  */
        osName = System.getProperty("os.name");

        if (osName.contains("Windows"))
        {
            currentPath = "C:\\";
            delimiter = "\\";
        }
        else
        {
            currentPath = "/";
            delimiter = "/";
        }
    }
    /**
     * Конструктор с передачей пути, на который объект будет указывать после создания
     * */
    public FileSystem(String path)
    {
        currentPath = path;
        osName = System.getProperty("os.name");
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
     * @return True если существует, иначе False
     * */
    public boolean isExist(String filePath)
    {
        return new File(filePath).exists();
    }
}
