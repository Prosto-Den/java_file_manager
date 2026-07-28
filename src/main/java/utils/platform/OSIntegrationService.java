package utils.platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Desktop;

import models.SettingKeys;
import types.FileSystemErrors;
import types.OSType;
import utils.settings.SettingsManager;

public class OSIntegrationService
{
    private final SettingsManager settings;
    private final OSType osType;
    private final String WINDOWS_OPEN_COMMAND = "explorer %s";

    public OSIntegrationService(OSType osType, SettingsManager settingsManager)
    {
        this.osType = osType;
        settings = settingsManager;
    }

    /**
     * Открыть файл соответствующей для него программой
     * @param osType тип операционной системы
     * @param path путь к файлу
     * @return код ошибки открытия файла
     * */
    public FileSystemErrors openFile(String path)
    {
        File file = new File(path);

        if (!file.exists())
            return FileSystemErrors.FILE_NOT_FOUND;

        if (!file.isFile())
            return FileSystemErrors.NOT_A_FILE;

        FileSystemErrors res = openSystemCommands(osType, path);

        if (res != FileSystemErrors.OK)
            res = openDesktop(file);

        return res;
    }

    /**
     * Переместить файл (директорию) в корзину
     * @param filePath путь к файлу(директории)
     * @return true если перемещенеи в корзину прошло успешно, иначе false
     */
    public boolean moveToTrash(String filePath)
    {
        boolean res = false;

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH))
        {
            File file = new File(filePath);
            res = Desktop.getDesktop().moveToTrash(file);
        }
        else
            res = moveToTrashViaGio(filePath);

        return res;
    }

    /**
     * Переместить файл (директорию) в корзину посредством gio. Актуально для Arch Linux
     * @param filePath - путь к файлу
     * @return true, если удалось переместить файл в корзину, иначе false
     */
    public boolean moveToTrashViaGio(String filePath)
    {
        boolean result = false;

        try
        {
            Process process = new ProcessBuilder("gio", "trash", filePath).start();
            int exitCode = process.waitFor();
            result = exitCode == 0;
        }
        catch (IOException | InterruptedException ex)
        {
        }

        return result;
    }

    private FileSystemErrors openSystemCommands(OSType osType, String path)
    {
        FileSystemErrors res = FileSystemErrors.UNKNOWN_ERROR;

        switch (osType)
        {
            case OSType.WINDOWS -> res = runCommand(WINDOWS_OPEN_COMMAND, path);
            case OSType.LINUX -> res = runCommand(settings.get(SettingKeys.LINUX_OPEN_COMMAND), path);
        }

        return res;
    }

    private FileSystemErrors runCommand(String command, String arg)
    {
        FileSystemErrors result = FileSystemErrors.UNKNOWN_ERROR;

        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(prepareCommand(command, arg));
            processBuilder.redirectErrorStream(true);

            Process proc = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println(line);
            }

            int exitCode = proc.waitFor();
            if (exitCode == 0)
                result = FileSystemErrors.OK;
            System.out.println("Код ошибки: " + exitCode);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }

    private String[] prepareCommand(String command, String arg)
    {
        String readyCommand = String.format(command, arg);
        return readyCommand.split(" ");
    }

    private FileSystemErrors openDesktop(File file)
    {
        if (!Desktop.isDesktopSupported())
        {
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
