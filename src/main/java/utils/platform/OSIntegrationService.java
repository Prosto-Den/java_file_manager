package utils.platform;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
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
        runCommand(command, path);
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
        String command = settings.get(SettingKeys.LINUX_MOVE_TO_TRASH_COMMAND);
        return runCommandWithWait(command, filePath);
    }

    /**
     * Открыть файл при помощи системных команд
     * @param osType тип ОС
     * @param path путь к файлу
     */
    private void openSystemCommands(OSType osType, String path)
    {
        switch (osType)
        {
            case OSType.WINDOWS -> runCommand(WINDOWS_OPEN_COMMAND, path);
            case OSType.LINUX -> runCommand(settings.get(SettingKeys.LINUX_OPEN_COMMAND), path);
        }
    }

    /**
     * Запустить выполнение команды отдельным процессов без ожидания
     * @param command команда
     * @param arg аргумент команды
     */
    private void runCommand(String command, String arg)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(prepareCommand(command, arg));
            processBuilder.redirectErrorStream(true);

            processBuilder.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Запустить выполнение команды отдельным процесом и дождаться его завершения
     * @param command команда
     * @param arg аргумент к команде
     * @return true, если команда завершилась успешно, иначе false
     */
    private boolean runCommandWithWait(String command, String arg)
    {
        boolean result = false;

        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(prepareCommand(command, arg));
            processBuilder.redirectErrorStream(true);
            Process proc = processBuilder.start();
            // TODO некоторые команды могут быть долгими. Выставить время ожидания? Показать диалоговое окно во время выполнения?
            result = proc.waitFor() == 0;
        }
        catch (IOException | InterruptedException ex)
        {
            System.err.println(ex);   
        }

        return result;
    }

    /**
     * Подготовить команду к выполнению. Объекты Process и ProcessBuilder принимают команду в виде списка, где первй элемент - сама команда,
     * а все остальные - ключи и аргументы к ней. Соотвественно, необходимо привезти команду к нужному виду
     * @param command команда
     * @param arg аргумент команды
     * @return список из команды и её ключей/аргументов
     */
    private List<String> prepareCommand(String command, String arg)
    {
        String[] commandParts = command.trim().split("\\s+");
        List<String> fullArgs = new ArrayList<>(Arrays.asList(commandParts));
        fullArgs.add(arg);
        return fullArgs;
    }

    /**
     * Открыть файл при помощи Desktop API
     * @param file файл для октрытия
     * @return код ошибки
     */
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
