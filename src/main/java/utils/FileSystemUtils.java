package utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import types.FileSystemErrors;
import types.OSType;

import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.*;
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
    private static final OSType osType = calcOSType(); // тип ОС
    private static final String delimiter = calcDelimiter();

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
     * Проверить тип операционной системы
     * @param type тип ОС
     * @return True, если переданный тип совпадает с типом ОС компьютера, иначе False
     * */
    public static boolean checkOS(OSType type) { return osType.equals(type); }

    // TODO после вызова метода могут возникать ошибки, нужно реализовать возврат кода ошибки,
    //  чтобы потом можно было показывать диалоговые окна с предупреждением
    /**
     * Открыть файл соответствующей программой на ПК
     * @param filePath путь к файлу
     * @return код ошибки
     * */
    public static FileSystemErrors openFile(String filePath)
    {
        // логику работы с открытием файла вынес в отдельную утилиту, так как там много нюансов
        return FileOpener.openFile(osType, filePath);
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
        return String.join(delimiter, path, filename);
    }


    public static boolean delete(String path)
    {
        if (!isDir(path))
            return new File(path).delete();
        else
            return deleteRecursively(path);
    }

    //TODO на Linux корзины нет (не на всех рабочих столах). Нужно проверить, как эта функция себя поведёт
    public static boolean moveToTrash(String filePath)
    {
        boolean res = false;

        File file = new File(filePath);

        if (Desktop.isDesktopSupported())
            res = Desktop.getDesktop().moveToTrash(file);

        return res;
    }

    public static void copyToClipboard(String path)
    {
        File file = new File(path);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putFiles(Collections.singletonList(file));
        clipboard.setContent(content);
    }

    public static boolean isClipBoardEmpty()
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        return !clipboard.hasFiles();
    }

    public static void insert(String path)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasFiles())
        {
            List<File> files = clipboard.getFiles();

            for (File file : files)
            {
                File destFile = new File(FileSystemUtils.adjustPath(path, file.getName()));

                try
                {
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException ex)
                {
                    System.err.println("gneg :)");
                }
            }
        }
    }


    // Приватные методы

    /**
     * Определить операционную систему. Пока все ОС делятся на Windows и Linux (всё что не Windows, то Linux)
     * @return Тип ОС
     * */
    private static OSType calcOSType()
    {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows"))
            return OSType.WINDOWS;
        return OSType.LINUX;
    }

    /**
     * Выдать корень файловой системы (C:\ для Windows и / для Linux)
     * @return Корень системы
     * */
    public static String getDefaultPath()
    {
        if (osType == OSType.WINDOWS)
            return "C:\\";
        return "/";
    }

    /**
     * Определить разделитель для текущей операционной системы
     * @return используемый в ОС этого компьютера разделитель
     * */
    private static String calcDelimiter()
    {
        if (osType == OSType.WINDOWS)
            return "\\";
        return "/";
    }

    //TODO подозреваю, что директории с большим количеством файлов будут удаляться долго, поэтому думаю, что
    // удаление всего надо вынести в отдельный поток + создать окно с индикацией удаления
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
