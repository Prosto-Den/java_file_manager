package utils.filesystem;

import types.OSType;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.Nullable;

//TODO некоторые утилиты не нужны для линукса. Возможно, стоит завезти отдельный класс WindowsFileSystemUtils?

/**
 * Статические методы для работы с файловой системой
 * */
public class FileSystemUtils
{
    /**
     * Возвращает список со всеми логическими дисками системы (C:\, D:\ и т.д).
     * Вызов функции актуален только для Windows.
     * @return Список со всеми логическими дисками системы для Windows, пустой список для Linux.
     * */
    // TODO пока работает на String, так как кроме комбобокса больше нигде не используется
    public static @Nullable List<String> getLogicalDrives()
    {
        if (OSType.is(OSType.WINDOWS))
        {
            List<String> logicalDrives = new ArrayList<>();
            for (char letter = 'A'; letter <= 'Z'; ++letter)
            {
                Path path = Path.of(String.format("%s:", letter));
                if (isExist(path))
                    logicalDrives.add(path.toString());

            }

            return logicalDrives;
        }

        return null;
    }

    /**
     * Существует ли путь?
     * @param path путь к файле/директории
     * @return true, если путь существует (ведёт к существующему файлу, существующей директории), иначе false
     */
    public static boolean isExist(Path path)
    {
        return Files.exists(path);
    }

    /**
     * Получить атрибуты файла по пути к нему
     * @param path путь к файлу
     * @return атрибуты файла, если их удалось получить, иначе null
     */
    public static @Nullable BasicFileAttributes getFileAttributes(Path path)
    {
        try
        {
            return Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        }
        catch (IOException ex)
        {
            System.err.println("Ошибка чтения пути: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Является ли переданный путь директорией
     * @param path путь
     * @return true, если путь указывает на директорию, иначе false
     */
    public static boolean isDir(Path path)
    {
        return Files.isDirectory(path);
    }

    /**
     * Является ли переданный путь файлом?
     * @param path путь
     * @return true, если путь указывает на файл, иначе false
     */
    public static boolean isFile(Path path)
    {
        return !isDir(path);
    }

    /**
     * Пуста ли директория?
     * @param path путь к директории
     * @return true, если удалось считать содержимое директории и в директории есть хотя бы один файл. false, 
     * если путь ведёт не к директории или не удалось считать содержимое
     */
    public static boolean isDirEmpty(Path path)
    {
        if (path == null || !Files.isDirectory(path))
            return false;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path))
        {
            return stream.iterator().hasNext();
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось создать поток для чтения директории: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Удалить файл/директорию
     * @param path путь к файлу/директории
     * @return true, если удалось удалить файл/директорию, иначе false
     */
    public static boolean delete(Path path)
    {
        try
        {
            if (Files.deleteIfExists(path))
                return true;
            else
                return deleteRecursively(path);
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось удалить: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Создать директорию по указанному пути
     * @param path путь к директории
     * @return true, если директорию удалось создать, иначе false
     */
    public static boolean createDir(Path path)
    {
        try 
        {
            // TODO подумать над проверкой
            Files.createDirectory(path);
            return true;
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось создать директорию: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Создать файл по переданному пути
     * @param path путь к будущему файлу
     * @return true, если файл удалось создать, иначе false
     */
    public static boolean createFile(Path path)
    {
        try
        {
            // TODO подумать над проверкой
            Files.createFile(path);
            return true;
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось создать файл: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Переименовать файл/директорию
     * @param path путь к файлу
     * @param newName новое имя для файла/директории
     * @return true, если файл/директорию удалось переименовать, иначе false
     */
    public static boolean renameFile(Path path, String newName)
    {
        if (path == null || !Files.exists(path) || newName == null || newName.isBlank())
            return false;

        Path newPath = path.getParent().resolve(newName);

        try
        {
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось переименовать файл: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Выдать корень файловой системы (C:\ для Windows и / для Linux)
     * @return Корень системы
     * */
    public static Path getDefaultPath()
    {
        if (OSType.is(OSType.WINDOWS))
            return Path.of("C:\\");
        return Path.of("/");
    }

    /**
     * Переместить файл
     * @param sourcePath путь к файлу
     * @param destPath новый путь к файлу
     */
    public static void moveFile(Path sourcePath, Path destPath)
    {
        transferFile(sourcePath, destPath, true);
    }

    /**
     * Скопирвоать файл
     * @param sourcePath путь к файлу
     * @param destPath новый путь к файлу
     */
    public static void copyFile(Path sourcePath, Path destPath)
    {
        transferFile(sourcePath, destPath, false);
    }

    public static List<Path> listDirectory(Path dirPath, boolean asNames)
    {
        List<Path> res = new ArrayList<>();

        if (!isDir(dirPath))
            return res;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath))
        {
            for (Path path : stream)
                if (asNames)
                    res.add(path.getFileName());
                else
                    res.add(path);
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось создать поток для чтения директории: " + ex.getMessage());
        }

        return res;
    }

    // Приватный методы
    /**
     * Общий метод для перемещения файла
     * @param source файл источник
     * @param dest файл назначения
     * @param isMove флаг перемещения. true, если файлы надо переместить и false, если их нужно скопировать.
     */
    private static void transferFile(Path source, Path dest, boolean isMove)
    {
        try
        {
            if (isDir(source))
            {
                if (!isExist(dest))
                    return;
                
                Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                    {
                        if (isMove)
                            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                        else
                            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);  

                        return FileVisitResult.CONTINUE;
                    }
                });

                if (isMove)
                    Files.delete(source);
            }
            else
            {
                if (isMove)
                    Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                else
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    //TODO директории с большим количеством файлов будут удаляться долго, поэтому
    // удаление надо вынести в отдельный поток + создать окно с индикацией удаления
    private static boolean deleteRecursively(Path path)
    {
        boolean res = false;
        try
        {
            if (isDir(path) && !isDirEmpty(path))
            {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                    {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException
                    {
                        if (ex != null)
                            throw ex;

                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
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
