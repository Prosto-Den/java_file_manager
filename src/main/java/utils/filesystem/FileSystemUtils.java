package utils.filesystem;

import types.OSType;
import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

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
    @Deprecated(since = "0.0.8", forRemoval = true)
    public static boolean isExist(String path)
    {
        return new File(path).exists();
    }

    public static boolean isExist(Path path)
    {
        return Files.exists(path);
    }

    /**
     * Возвращает список со всеми логическими дисками системы (C:\, D:\ и т.д).
     * Вызов функции актуален только для Windows.
     * @return Список со всеми логическими дисками системы для Windows, пустой список для Linux.
     * */
    public static @Nullable List<String> getLogicalDrives()
    {
        if (OSType.is(OSType.WINDOWS))
        {
            List<String> logicalDrives = new ArrayList<>();
            for (char letter = 'A'; letter <= 'Z'; ++letter)
            {
                String path = String.format("%s:", letter);
                if (isExist(path))
                    logicalDrives.add(path);
            }

            return logicalDrives;
        }

        return null;
    }

    public static @Nullable List<Path> getLogicalDrivesV2()
    {
        if (OSType.is(OSType.WINDOWS))
        {
            List<Path> logicalDrives = new ArrayList<>();
            for (char letter = 'A'; letter <= 'Z'; ++letter)
            {
                Path path = Path.of(String.format("&s:", letter));
                if (isExist(path))
                    logicalDrives.add(path);
            }
        }

        return null;
    }

    /**
     * Получить дату последнего изменения файла
     * @param filePath путь к файлу
     * @return строку с датой последнего изменения файла
     * @deprecated Используйте {@link #getFileAttributes(Path)} для получения даты последней модификации файла и прочих атрибутов. А
     * для перевода даты в "человеческий" вид используйте {@link utils.Converter#convertDateTime(java.nio.file.attribute.FileTime)}
     * */
    @Deprecated(since="0.0.8", forRemoval = true)
    public static String lastModifiedDate(String filePath)
    {
        //TODO формат для даты вынести в строковые ресурсы
        long lastModified = new File(filePath).lastModified();
        Date date = new Date(lastModified);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);
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
     * Получить имя файла из абсолютного пути к нему
     * @param filePath Абсолютный путь к файлу
     * @return Имя файла
     * @deprecated Передача строк в качестве путей более не актуальна. Используйте метод класса {@link Path} для получения имена файла
     * */
    @Deprecated(since="0.0.8", forRemoval = true)
    public static String getFilenameFromPath(String filePath)
    {
        return new File(filePath).getName();
    }

    //TODO возможно стоит сделать метод более гибким (например, задать возможность выбора размерности)
    /**
     * Получить строку с информацией о размере файла
     * @param filePath Абсолютный путь к файлу
     * @return Строка с размером файла
     * @deprecated Используйте {@link #getFileAttributes(Path)} для получения размера файла и прочих атрибутов
     * */
    @Deprecated(since = "0.0.8", forRemoval = true)
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
     * @deprecated Передача строк в качестве пути болле не актуальна. Используйте метод {@link #isDir(Path)}
     * */
    @Deprecated(since="0.0.8", forRemoval = true)
    public static boolean isDir(String path)
    {
        return new File(path).isDirectory();
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

    public static boolean isFile(Path path)
    {
        return !isDir(path);
    }

    /**
     * Пуста ли директория?
     * @param path путь к директории
     * @return true, если жиректоряи пуста, иначе false
     * @deprecated Передача строк в качестве пути более не пктуальна. Используйте метод {@link #isDirEmpty(Path)}
     */
    @Deprecated(since = "0.0.8", forRemoval = true)
    public static boolean isDirEmpty(String path)
    {
        boolean res = false;
        File dir = new File(path);

        if (dir.isDirectory())
            res = dir.list().length == 0;

        return res;
    }

    /**
     * Пуста ли директория?
     * @param path путь к директории
     * @return true, если удалось считать содержимое директории и в директории есть хотя бы один файл. false, если путь ведёт не к директории или не удалось считать содержимое
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
     * Провести конкатенацию пути и имени файла / директории
     * @param path путь к родительской директории
     * @param filename название файла / директории в родительской директории
     * @return путь до файла / директории
     * @deprecated Более не актуален. Используйте {@link java.nio.file.Path#resolve(String)}
     * */
    @Deprecated(since = "0.0.8", forRemoval = true)
    public static String adjustPath(String path, String filename)
    {
        return String.join(System.getProperty("file.separator"), path, filename);
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
     * @deprecated Используйте {@link #createDir(Path)}
     */
    @Deprecated(since = "0.0.8", forRemoval = true)
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
     * Создать файл по указанному пути
     * @param path путь к файлу
     * @return true, если файл удалось создать, иначе false
     * @deprecated Используйте {@link #createFile(Path)}
     */
    @Deprecated(since = "0.0.8", forRemoval = true)
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
     * Переименовать файл
     * @param oldFilePath старый путь к файлу
     * @param newFilePath новый путь к файлу
     * @return true, если файл удалось переименовать, иначе false
     * @deprecated Используйте {@link #renameFile(Path, String)}
     */
    @Deprecated(since = "0.0.8", forRemoval = true)
    public static boolean renameFile(String oldFilePath, String newFilePath)
    {
        File file = new File(oldFilePath);
        return file.renameTo(new File(newFilePath));
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
     * Переместить файл
     * @param source изначальный файл
     * @param dest новый файл, который будет хранить данные изначального файла
     */
    public static void moveFile(File source, File dest)
    {
        transferFile(source.toPath(), dest.toPath(), true);
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

    /**
     * Скопирвоать файл
     * @param source изначальный файл
     * @param dest новый файл, в который будут скопированы данные изначального файла
     */
    public static void copyFile(File source, File dest)
    {
        transferFile(source.toPath(), dest.toPath(), false);
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
