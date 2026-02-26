package utils;

import types.FileSystemErrors;
import types.OSType;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;


public class FileOpener
{
    private static final String windowsExecCommand = "explorer %s";
    private static final String[] linuxExecCommands = {"gnome-open %s", "kde-open %s", "xdg-open %s"};

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

        // если не получилось, использует java.awt.Desktop
        if (res != FileSystemErrors.OK)
            res = openDesktop(file);

        return res;
    }

    // TODO необходимы тесты на Linux
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

    private static String[] prepareCommand(String command, String arg)
    {
        String readyCommand = String.format(command, arg);
        return readyCommand.split(" ");
    }

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
