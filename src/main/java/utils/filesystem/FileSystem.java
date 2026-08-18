package utils.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;

import events.EventBus;
import events.PathChangedEvent;

import java.util.Deque;
import java.util.ArrayDeque;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import types.OSType;

// TODO может быть статические методы всё-таки вынести в отдельную утилиту для удобства
/**
 * Класс для работы с файловой системой
 * */
public final class FileSystem
{
    // Абсолютный путь к текущей директории. Хранится в property из JavaFX, так как таким образом можно легко
    // отследить изменения текущей директории (для передачи данных между виджетами)
    private final StringProperty currentPath = new SimpleStringProperty("");

    // история перемещений пользователя
    private Deque<String> backStack;
    private Deque<String> forwardStack;

    /**
     * Конструктор по умолчанию. После создания будет указывать на корень системы
     * (C:\ у Windows и / у Linux)
     * */
    public FileSystem()
    {
        currentPath.setValue(FileSystemUtils.getDefaultPath());
        backStack = new ArrayDeque<>();
        forwardStack = new ArrayDeque<>();
    }

    /**
     * Конструктор с передачей пути, на который объект будет указывать после создания.
     * Если директории по такому пути не существует, будет указывать на корень (C:\ для Windows и / для Linux)
     * */
    public FileSystem(String path)
    {
        currentPath.setValue(FileSystemUtils.isDir(path) ? path : FileSystemUtils.getDefaultPath());
        backStack = new ArrayDeque<>();
        forwardStack = new ArrayDeque<>();
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
        if (OSType.is(OSType.WINDOWS))
            currentPath = currentPath.replace("\\\\", "\\");
        backStack.push(this.currentPath.getValue());

        if (!forwardStack.isEmpty())
        {
            String valueFromForwardStack = forwardStack.peek();
            if (!valueFromForwardStack.equals(currentPath))
                forwardStack.clear();
        }

        changeCurrentPath(currentPath);
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
    public void goDownTree(String filename)
    {
        setCurrentPath(buildPath(filename));
    }

    /**
     * Пойти назад по файловому дереву. Получает родительскую директорию для текущей,
     * и заменяет текущую директорию на неё
     * */
    public void goUpTree()
    {
        setCurrentPath(getParentDir());
    }

    /**
     * Получить родительскую директорию для текущей директории
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

    public boolean isBackStackEmpty()
    {
        return backStack.isEmpty();
    }

    public boolean isForwardStackEmpty()
    {
        return forwardStack.isEmpty();
    }

    public void goBack()
    {
        if (!backStack.isEmpty())
        {
            forwardStack.push(getCurrentPath());
            changeCurrentPath(backStack.pop());
        }
    }

    public void goForward()
    {
        if (!forwardStack.isEmpty())
        {
            backStack.push(getCurrentPath());
            changeCurrentPath(forwardStack.pop());
        }
    }

    private void changeCurrentPath(String newPath)
    {
        this.currentPath.setValue(newPath);
        EventBus.publish(new PathChangedEvent());
    }
}
