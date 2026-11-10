package utils.platform;

import java.awt.Desktop;
import java.nio.file.Path;

public class WindowsIntegrationService extends BaseIntergationService
{
    private final String WINDOWS_OPEN_COMMAND = "explorer %s";
    private final String WINDOWS_OPEN_IN_TERMINAL_COMMAND = "cd /d %s";

    @Override
    public void openFile(Path path)
    {
        runCommand(WINDOWS_OPEN_COMMAND, path.toString());
    }

    @Override
    public void openInTerminal(Path path)
    {
        runCommand(WINDOWS_OPEN_IN_TERMINAL_COMMAND, path.toString());
    }

    // TODO переделать без использования Desktop?
    @Override 
    public boolean moveToTrash(Path filePath)
    {
        boolean result = false;

        if (Desktop.isDesktopSupported())
        {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.MOVE_TO_TRASH))
                result = desktop.moveToTrash(filePath.toFile());
        }

        return result;
    }
}
