package utils.platform;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.nio.file.Path;
import java.awt.Desktop;
import java.io.IOException;

/**
 * Базовые методы для классов взаимодействия с ОС
 * BaseIntergationService
 */
public abstract class BaseIntergationService implements IIntegrationService
{   
    @Override
    public boolean runCommandAndWait(String command, String... args)
    {
        try
        {
            Process proc = runCommand(command, args);
            return proc.waitFor() == 0;
        }
        catch (InterruptedException ex)
        {
            // TODO добавить логгирование
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Process runCommand(String command, String... args)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(prepareCommand(command, args));
            processBuilder.redirectErrorStream(true);
            return processBuilder.start();
        }
        catch (Exception ex)
        {
            // TODO добавить логгирование
            ex.printStackTrace();
            return null;
        }
    }

    private List<String> prepareCommand(String command, String... args)
    {
        String[] commandParts = command.trim().split("\\s+");
        List<String> fullArgs = new ArrayList<>(Arrays.asList(commandParts));
        for (String arg : args)
            fullArgs.add(arg);
        return fullArgs;
    }

    // TODO возможно не нужен
    protected boolean openViaDesktop(Path path)
    {
        boolean result = Desktop.isDesktopSupported();

        if (result)
        {
            Desktop desktop = Desktop.getDesktop();
            try
            {
                result = desktop.isSupported(Desktop.Action.APP_OPEN_FILE);
                if (result)
                    desktop.open(path.toFile());
            }
            catch (IOException ex)
            {
                // TODO сюда логгирование
                result = false;
            }
        }

        return result;
    }
}
