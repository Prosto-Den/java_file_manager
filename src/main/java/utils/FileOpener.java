package utils;

import types.FileSystemErrors;
import types.OSType;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;


/**
 * Утилита для открытия файлов
 * */
public class FileOpener
{
    // TODO вынести команды в отдельный класс
    // команда открытия файла для Windows
    private static final String windowsExecCommand = "explorer %s";
    // возможные команды для открытия файла на Linux
    private static final String[] linuxExecCommands = {"gnome-open %s", "kde-open %s", "xdg-open %s"};

    /**
     * Открыть файл соответствующей для него программой
     * @param osType тип операционной системы
     * @param path путь к файлу
     * @return код ошибки открытия файла
     * */
    public static FileSystemErrors openFile(OSType osType, String path)
    {
        File file = new File(path);

        // сначала проверяем, что файл существует и вообще является файлом
        if (!file.exists())
            return FileSystemErrors.FILE_NOT_FOUND;

        if (!file.isFile())
            return FileSystemErrors.NOT_A_FILE;

        // потом пробуем открыть файл системными средствами
        FileSystemErrors res = openSystemCommands(osType, path);

        // если не получилось, пробуем java.awt.Desktop
        if (res != FileSystemErrors.OK)
            res = openDesktop(file);

        return res;
    }

    // TODO необходимы тесты на Linux
    /**
     * Открытие файла средствами операционной системы
     * @param osType тип операционной системы
     * @param path путь к файлу
     * @return код ошибки открытия файла
     * */
    private static FileSystemErrors openSystemCommands(OSType osType, String path)
    {
        FileSystemErrors res = FileSystemErrors.UNKNOWN_ERROR;

        switch (osType)
        {
            case OSType.WINDOWS -> res = runCommand(windowsExecCommand, path);

            case OSType.LINUX -> {
                for (String command : linuxExecCommands)
                {
                    res = runCommand(command, path);
                    if (res == FileSystemErrors.OK)
                        break;
                }
            }
        }

        return res;
    }

    // TODO в будущем команд может стать больше, нужно создать отдельную утилиту для их запуска
    /**
     * Запустить команду на выполнение
     * @param command команда
     * @param arg аргумент команды
     * @return код ошибки открытия файла
     * */
    private static FileSystemErrors runCommand(String command, String arg)
    {
        String[] commandParts = prepareCommand(command, arg);
        Runtime rt = Runtime.getRuntime();
        FileSystemErrors res;

        try
        {
            Process proc = rt.exec(commandParts);

            try
            {
                proc.exitValue();
                res = FileSystemErrors.PROCESS_CLOSE_ERROR;
            }
            catch (IllegalThreadStateException ex) // если поймали такое исключение, значит процесс
            // не закрылся сразу после создания - хороший знак
            {
                res = FileSystemErrors.OK;
            }
        }
        catch (IOException ex)
        {
            res = FileSystemErrors.PROCESS_IO_ERROR;
        }

        return res;
    }

    /**
     * Подготовить команду к выполнению
     * @param command команда
     * @param arg аргумент команды
     * @return команду, разделённую по словам
     * */
    private static String[] prepareCommand(String command, String arg)
    {
        String readyCommand = String.format(command, arg);
        return readyCommand.split(" ");
    }

    /**
     * Открыть файл с помощью DesktopAPI
     * @param file объект открываемого файла
     * @return код ошибки открытия файла
     * */
    private static FileSystemErrors openDesktop(File file)
    {
        if (!Desktop.isDesktopSupported())
        {
            //TODO добавить логгер
            System.err.println("Desktop API не поддерживается на данной системе");
            return FileSystemErrors.DESKTOP_NOT_SUPPORTED;
        }

        Desktop desktop = Desktop.getDesktop();

        try
        {
            desktop.open(file);
            return FileSystemErrors.OK;
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось открыть файл: " + ex.getMessage());
            return FileSystemErrors.OPEN_FILE_ERROR;
        }
    }
}
