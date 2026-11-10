package utils.platform;

import models.AppSettings;
import java.nio.file.Path;

public class LinuxIntegrationService extends BaseIntergationService
{
    public final AppSettings.LinuxCommands commands; 

    public LinuxIntegrationService(AppSettings settings)
    {
        commands = settings.settings.linuxCommands;
    }

    @Override 
    public void openFile(Path path)
    {
        runCommand(commands.open, path.toString());
    }

    @Override
    public void openInTerminal(Path path)
    {
        runCommand(commands.console, path.toString());
    }

    @Override 
    public boolean moveToTrash(Path filePath)
    {
        return runCommandAndWait(commands.moveToTrash, filePath.toString());
    }
}
