package utils.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.HashSet;
import java.util.Deque;
import java.util.ArrayDeque;

import app.AppContext;
import events.EventBus;
import events.PathChangedEvent;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.StringKeys;
import types.OSType;
import utils.i18n.LanguageManager;


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
     * @param asNames если true, выдаст имена файлов, содержащихся в текущей директории.
     *                Если false - выдаст их абсолютные пути
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

    /*TODO возможны ситуации, когда изменения в файловой системе будут происходить в другом месте (например, пользователь удалит папку из 
        другого файлового менеджера). И может сложиться ситуация, что будет происходить переход в папку, которая уже не существует.
        Стоит предусмотреть такой сценарий и выводить alert, если вдруг папка перестала существовать.
     */
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
            String valueFromForwardStack = forwardStack.pop();
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

    /**
     * Пуста ли история перемещений пользователя "назад"?
     * @return true, если пуста, иначе false
     */
    public boolean isBackStackEmpty()
    {
        return backStack.isEmpty();
    }

    /**
     * Пуста ли история перемещений пользователя "вперёд"?
     * @return true, если пуста, иначе false
     */
    public boolean isForwardStackEmpty()
    {
        return forwardStack.isEmpty();
    }

    /**
     * Переместиться "назад" по истории
     */
    public void goBack()
    {
        if (!backStack.isEmpty())
        {
            forwardStack.push(getCurrentPath());
            changeCurrentPath(backStack.pop());
        }
    }

    /**
     * Переместиться "вперёд" по истории
     */
    public void goForward()
    {
        if (!forwardStack.isEmpty())
        {
            backStack.push(getCurrentPath());
            changeCurrentPath(forwardStack.pop());
        }
    }

    /**
     * Создать папку в текущей директории
     * @return true, если папку удалось создать, иначе false
     */
    public boolean createFolderInCurrentDirectory()
    {
        String folderName = AppContext.getLanguageManager().getString(StringKeys.NEW_FOLDER_NAME);
        return FileSystemUtils.createDir(buildPath(buildFileName(folderName, "")));
    }

    /**
     * Создать текстовый файл в текущей директории
     * @return true, если файл удалось создать, иначе false
     */
    public boolean createTextFileInCurrentDirectory()
    {
        String fileName = AppContext.getLanguageManager().getString(StringKeys.NEW_TEXT_FILE_NAME);
        return FileSystemUtils.createFile(buildPath(buildFileName(fileName, ".txt")));
    }

    // Приватные методы

    /**
     * Сменить текущую директории и отправить событие об этом
     * @param newPath новый путь, на который будет указывать файловая система
     */
    private void changeCurrentPath(String newPath)
    {
        this.currentPath.setValue(newPath);
        EventBus.publish(new PathChangedEvent());
    }

    /**
     * Построить имя для создаваемого файла/папки
     * @param fileName имя файла/папки
     * @param fileFormat формат файла (для папки передать пустую строку)
     * @return имя нового файла/папки
     */
    private String buildFileName(String fileName, String fileFormat)
    {
        LanguageManager langManager = AppContext.getLanguageManager();
        File file = new File(getCurrentPath());
        Set<Integer> usedIndices = new HashSet<>();

        file.listFiles(new FileFilter() {
            Pattern pattern = Pattern.compile(langManager.getString(StringKeys.PATTERN_NEW_FILE,
                Pattern.quote(fileName),
                Pattern.quote(fileFormat)));

            @Override
            public boolean accept(File file)
            {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches())
                {
                    String group = matcher.group(1);
                    usedIndices.add(group == null ? 0 : Integer.parseInt(group));
                    return true;
                }

                return false;
            } 
        });
        
        String result = fileName;

        if (usedIndices.contains(0))
        {
            int nextIndex = 1;
            while (usedIndices.contains(nextIndex))
                nextIndex++;
            result = fileName + " (" + nextIndex + ")";
        }
        
        if (!fileFormat.isEmpty())
            result += fileFormat;
        
        return result;
    }
}
