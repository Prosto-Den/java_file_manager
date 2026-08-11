package utils.filesystem;

import types.OSType;
import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

//TODO некоторые утилиты не нужны для линукса. Возможно, стоит завезти отдельный класс WindowsFileSystemUtils?

/**
 * Статические методы для работы с файловой системой
 * */
public class FileSystemUtils
{
    /**
     * Существует ли файл (директория) по этому пути
     * @param path Путь к файлу/директории
     * @return True если существует, иначе False
     * */
    public static boolean isExist(String path)
    {
        return new File(path).exists();
    }

    /**
     * Возвращает список со всеми логическими дисками системы (C:\, D:\ и т.д).
     * Вызов функции актуален только для Windows.
     * @return Список со всеми логическими дисками системы для Windows, пустой список для Linux.
     * */
    public static List<String> getLogicalDrives()
    {
        List<String> logicalDrives = new ArrayList<>();

        if (OSType.is(OSType.WINDOWS))
        {
            for (char letter = 'A'; letter <= 'Z'; letter++)
            {
                String path = String.format("%s:", letter);
                if (isExist(path))
                    logicalDrives.add(path);
            }
        }

        return logicalDrives;
    }

    /**
     * Получить дату последнего изменения файла
     * */
    public static String lastModifiedDate(String filePath)
    {
        //TODO формат для даты вынести в строковые ресурсы
        long lastModified = new File(filePath).lastModified();
        Date date = new Date(lastModified);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);
    }

    /**
     * Получить имя файла из абсолютного пути к нему
     * @param filePath Абсолютный путь к файлу
     * @return Имя файла
     * */
    public static String getFilenameFromPath(String filePath)
    {
        return new File(filePath).getName();
    }

    //TODO возможно стоит сделать метод более гибким (например, задать возможность выбора размерности)
    /**
     * Получить строку с информацией о размере файла
     * @param filePath Абсолютный путь к файлу
     * @return Строка с размером файла
     * */
    public static String getFileSize(String filePath)
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
     * Является ли переданный путь директорией
     * @param path Путь
     * @return True - если переданный путь существует и является директорией, иначе False
     * */
    public static boolean isDir(String path)
    {
        return new File(path).isDirectory();
    }

    /**
     * Провести конкатенацию пути и имени файла / директории
     * @param path путь к родительской директории
     * @param filename название файла / директории в родительской директории
     * @return путь до файла / директории
     * */
    public static String adjustPath(String path, String filename)
    {
        return String.join(System.getProperty("file.separator"), path, filename);
    }

    /**
     * Удалить файл (директорию)
     * @param path путь к файлу (директории)
     * @return true если удаление прошло успешно, иначе false
     */
    public static boolean delete(String path)
    {
        if (!isDir(path))
            return new File(path).delete();
        else
            return deleteRecursively(path);
    }

    public static boolean createDir(String path)
    {
        File file = new File(path);
        boolean res = false;

        try
        {
            res = file.mkdir();
        }
        catch (SecurityException ex)
        {
            System.err.println("Ошибка доступа");
        }

        return res;
    }

    public static boolean createFile(String path)
    {
        File file = new File(path);
        boolean res = false;
        try
        {
            res = file.createNewFile();
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось создать файл");
        }

        return res;
    }

    // Приватные методы

    /**
     * Выдать корень файловой системы (C:\ для Windows и / для Linux)
     * @return Корень системы
     * */
    public static String getDefaultPath()
    {
        if (OSType.is(OSType.WINDOWS))
            return "C:\\";
        return "/";
    }

    //TODO директории с большим количеством файлов будут удаляться долго, поэтому
    // удаление надо вынести в отдельный поток + создать окно с индикацией удаления
    private static boolean deleteRecursively(String rawPath)
    {
        boolean res = false;

        Path path = Paths.get(rawPath);

        try
        {
            if (Files.isDirectory(path))
            {
                try (Stream<Path> stream = Files.walk(path))
                {
                    stream.sorted(Comparator.reverseOrder())
                            .forEach(p -> {
                                try
                                {
                                    if (Files.isDirectory(p))
                                        deleteRecursively(p.getParent().toString());
                                    else
                                        Files.delete(p);
                                }
                                catch (IOException ex)
                                {
                                    System.err.println("не удалось удалить: " + p);
                                }
                            });
                }
            }
            else
                Files.delete(path);

            res = true;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return res;
    }
}
