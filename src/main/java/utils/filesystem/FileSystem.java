package utils.filesystem;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.HashSet;
import java.util.Deque;
import java.util.ArrayDeque;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;

import app.AppContext;
import events.EventBus;
import events.PathChangedEvent;

import models.StringKeys;
import utils.i18n.LanguageManager;


/**
 * Класс для работы с файловой системой
 * */
public final class FileSystem
{   
    // Абсолютный путь к текущей директории
    private Path currentPath;

    // история перемещений пользователя
    // TODO добавить настройки с количеством файлов в истории
    private Deque<Path> backStack;
    private Deque<Path> forwardStack;

    private static interface Transfer
    {
        void execute(Path source, Path dest);
    }

    /**
     * Конструктор по умолчанию. После создания будет указывать на корень системы
     * (C:\ у Windows и / у Linux)
     * */
    public FileSystem()
    {
        currentPath = FileSystemUtils.getDefaultPath();
        backStack = new ArrayDeque<>();
        forwardStack = new ArrayDeque<>();
    }

    /**
     * Конструктор с передачей пути, на который объект будет указывать после создания.
     * Если директории по такому пути не существует, будет указывать на корень (C:\ для Windows и / для Linux)
     * */
    public FileSystem(Path path)
    {
        currentPath = FileSystemUtils.isDir(path) ? path : FileSystemUtils.getDefaultPath();
        backStack = new ArrayDeque<>();
        forwardStack = new ArrayDeque<>();
    }

    /**
     * Выдать всё содержимое текущей директории
     * @param asNames если true, выдаст имена файлов, содержащихся в текущей директории.
     *                Если false - выдаст их абсолютные пути
     * @return Список с файлами, содержащимися внутри текущей директории
     * */
    public List<Path> listCurrentPath(boolean asNames)
    {
        return FileSystemUtils.listDirectory(currentPath, asNames);
    }

    /**
     * Построить путь до файла. Путь строится путём конкатенации текущего пути с переданным именем файла
     * (с добавлением разделителя между ними). Не проверяет, существует ли файл по этому пути на самом деле
     * @return Путь к файлу
     * */
    public Path buildPath(String fileName)
    {
        return currentPath.resolve(fileName);
    }

    public Path buildPath(Path fileName)
    {
        return currentPath.resolve(fileName);
    }

    /*TODO возможны ситуации, когда изменения в файловой системе будут происходить в другом месте (например, пользователь удалит папку из 
        другого файлового менеджера). И может сложиться ситуация, что будет происходить переход в папку, которая уже не существует.
        Стоит предусмотреть такой сценарий и выводить alert, если вдруг переданный путь не существует
     */
    /**
     * Сменить текущую директорию
     * */
    public void setCurrentPath(Path path)
    {
        backStack.push(currentPath);
        // TODO размер истории вынести в настройки
        if (backStack.size() >= 10)
            backStack.removeLast();

        if (!forwardStack.isEmpty())
        {
            Path valueFromForwardStack = forwardStack.pop();
            if (!valueFromForwardStack.equals(path))
                forwardStack.clear();
        }

        changeCurrentPath(path);
    }

    /**
     * Выдать текущую директорию
     * @return текущая директория, на которую указывает объект
     * */
    public Path getCurrentPath() { return currentPath; }

    /**
     * Является ли текущая директория корнем системы?
     * */
    public boolean isCurrentPathRoot()
    {
        Pattern pattern = Pattern.compile("^([A-Z]:\\\\|/)$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(currentPath.toString()).matches();
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
        setCurrentPath(currentPath.getParent());
    }

    /**
     * Получить родительскую директорию для текущей директории
     * @return Абсолютный путь до родительской директории
     * */
    public Path getParentDir()
    {
        return currentPath.getParent();
    }

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
            forwardStack.push(currentPath);
            if (forwardStack.size() >= 10)
                forwardStack.removeLast();
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
            backStack.push(currentPath);
            if (backStack.size() >= 10)
                backStack.removeLast();
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

    /**
     * Переименовать файл
     * @param oldFileName старое имя файла
     * @param newFileName новое имя файла
     * @return true, если файл удалось переименовать, иначе false
     */
    public boolean renameFile(String oldFileName, String newFileName)
    {
        return FileSystemUtils.renameFile(buildPath(oldFileName), newFileName);
    }

    /**
     * Переместить файлы в текущую директорию
     * @param files файлы для перемещения
     */
    public void moveInto(List<File> files)
    {
        transferFiles(files, new Transfer() {
            @Override
            public void execute(Path source, Path dest)
            {
                FileSystemUtils.moveFile(source, dest);
            }
        });
    }

    /**
     * Скопирвоать файлы в текущую директорию
     * @param files файлы для копирования
     */
    public void copyInto(List<File> files)
    {
        transferFiles(files, new Transfer(){
            @Override
            public void execute(Path source, Path dest)
            {
                FileSystemUtils.copyFile(source, dest);
            }
        });
    }

    // Приватные методы

    private void transferFiles(List<File> files, Transfer command)
    {
        for (File file : files)
        {
            Path dest = buildPath(file.toPath());
            if (file.isDirectory() && !FileSystemUtils.isExist(dest))
                try
                {
                    Files.createDirectories(dest);
                    command.execute(file.toPath(), dest);
                }
                catch (IOException ex)
                {
                    System.err.println("Не удалось создать папку назначения " + ex.getMessage());
                    return;
                }
        }
    }

    /**
     * Сменить текущую директории и отправить событие об этом
     * @param newPath новый путь, на который будет указывать файловая система
     */
    private void changeCurrentPath(Path newPath)
    {
        currentPath = newPath;
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
        Set<Integer> usedIndices = new HashSet<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath))
        {
            Pattern pattern = Pattern.compile(langManager.getString(StringKeys.PATTERN_NEW_FILE,
                Pattern.quote(fileName),
                Pattern.quote(fileFormat)));

            stream.forEach(path -> {
                Matcher matcher = pattern.matcher(path.getFileName().toString());
                if (matcher.matches())
                {
                    String group = matcher.group(1);
                    usedIndices.add(group == null ? 0 : Integer.parseInt(group));
                }
            });
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось создать поток чтения директории: " + ex.getMessage());
            return fileName;
        }
        
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
