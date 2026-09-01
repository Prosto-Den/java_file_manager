package utils.platform;

import java.io.File;
import java.io.IOException;
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
    private final String WINDOWS_OPEN_IN_TERMINAL_COMMAND = "cd /d %s";

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
    public void openFile(String path)
    {
        File file = new File(path);

        if (!file.exists() || !file.isFile())
            return;
        
        FileSystemErrors res = openDesktop(file);
        if (res != FileSystemErrors.OK)
            openSystemCommands(osType, path);
    }

    /**
     * Открыть директорию в терминале. Терминал запуститься в отдельнои процессе и не заблокирует работу файлового менеджера
     * @param path путь к директории
     */
    public void openInTerminal(String path)
    {
        String command = OSType.is(OSType.LINUX) ? settings.get(SettingKeys.LINUX_CONSOLE) : WINDOWS_OPEN_IN_TERMINAL_COMMAND;
        try
        {   
            new ProcessBuilder(prepareCommand(command, path)).start();
        }
        catch (Exception ex)
        {
            // TODO сюда логгирование
        }
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

    private void openSystemCommands(OSType osType, String path)
    {
        switch (osType)
        {
            case OSType.WINDOWS -> runCommand(WINDOWS_OPEN_COMMAND, path);
            case OSType.LINUX -> runCommand(settings.get(SettingKeys.LINUX_OPEN_COMMAND), path);
        }
    }

    private void runCommand(String command, String arg)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(command, arg);
            processBuilder.redirectErrorStream(true);

            processBuilder.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
            if (desktop.isSupported(Desktop.Action.APP_OPEN_FILE))
            {
                desktop.open(file);
                return FileSystemErrors.OK;
            }
            return FileSystemErrors.DESKTOP_NOT_SUPPORTED;
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось открыть файл: " + ex.getMessage());
            return FileSystemErrors.OPEN_FILE_ERROR;
        }
    }
}
