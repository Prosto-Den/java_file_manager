package utils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
import types.FileSystemErrors;
import types.OSType;
import javafx.beans.property.StringProperty;


// TODO может быть статические методы всё-таки вынести в отдельную утилиту для удобства
/**
 * Класс для работы с файловой системой
 * */
public final class FileSystem
{
    // Абсолютный путь к текущей директории. Хранится в property из JavaFX, так как таким образом можно легко
    // отследить изменения текущей директории (для передачи данных между виджетами)
    private final StringProperty currentPath = new SimpleStringProperty("");

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
        currentPath.setValue(FileSystemUtils.getDefaultPath());
    }
    /**
     * Конструктор с передачей пути, на который объект будет указывать после создания.
     * Если директории по такому пути не существует, будет указывать на корень (C:\ для Windows и / для Linux)
     * */
    public FileSystem(String path)
    {
        currentPath.setValue(FileSystemUtils.isDir(path) ? path : FileSystemUtils.getDefaultPath());
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
        return FileSystemUtils.adjustPath(getCurrentPath(), fileName);
    }

    /**
     * Сменить текущую директорию
     * */
    public void setCurrentPath(String currentPath)
    {
        if (FileSystemUtils.checkOS(OSType.WINDOWS))
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
     * Выдать property текущего пути
     * @return Property текущего пути
     * */
    public StringProperty getCurrentPathProperty() {return currentPath;}


    public FileSystemErrors openFile(String fileName)
    {
        String path = FileSystemUtils.adjustPath(getCurrentPath(), fileName);
        return FileSystemUtils.openFile(path);
    }
}
