package utils;


import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Класс для работы с файловой системой
 * */
public class FileSystem
{
    private String currentPath; // абсолютный путь к текущей директории
    static private String osName; // имя ОС
    static private String delimiter; // разделитель для путей этой ОС

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

    public FileSystem(String path)
    {
        currentPath = path;
        osName = System.getProperty("os.name");
    }

    //TODO а в java вообще на интерфейсах можно работать?
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

    public String buildPath(String fileName)
    {
        return String.join(delimiter, currentPath, fileName);
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public boolean isCurrentPathRoot()
    {
        Pattern pattern = Pattern.compile("^([A-Z]:\\\\|/)$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(currentPath).matches();
    }

    public void goForward(String filename)
    {
       setCurrentPath(buildPath(filename));
    }

    public void goBack()
    {
        setCurrentPath(getParentDir());
    }

    public String getParentDir()
    {
        File dir = new File(currentPath);
        return dir.getParent();
    }
}
