package utils.trash;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.File;

import models.TrashItem;
import utils.filesystem.FileSystemUtils;

public final class WindowsTrashManager implements ITrashManager
{
    private final String SEARCH_SCRIPT = "(New-Object -ComObject Shell.Application).Namespace(0x0a).Items() | " +
                        "Select-Object Name, Path, @{n='OrigPath';e={$_.ExtendedProperty('System.Recycle.DeletedFrom')}}, " +
                        "@{n='DelDate';e={$_.ExtendedProperty('System.Recycle.DateDeleted')}} | ConvertTo-Csv -NoTypeInformation";

    private final String RESTORE_SCRIPT = "(New-Object -ComObject Shell.Application).Namespace(0x0a).Items() | " + 
                        "Where-Object {$_.Path -eq '%s'} | ForEach-Object {$_.InvokeVerb('Window.Recycle.Restore')}";

    @Override
    public List<TrashItem> getTrashItems()
    {
        List<TrashItem> items = new ArrayList<>();

        try
        {
            Process process = new ProcessBuilder("powershell.exe", "-Command", SEARCH_SCRIPT).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
            {
                String line;
                reader.readLine();

                while ((line = reader.readLine()) != null)
                {
                    String[] parts = line.split("\", \"");
                    if (parts.length >= 4)
                    {
                        String name = parts[0].replace("\"", "");
                        String trashPath = parts[1].replace("\"", "");
                        String originalPath = parts[2].replace("\"", "");
                        String delDate = parts[3].replace("\"", "");

                        items.add(new TrashItem(new File(trashPath), new File(trashPath), originalPath, delDate));
                    }
                }
            }

            process.waitFor();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return items;
    }

    @Override
    public boolean restoreItem(TrashItem item)
    {
        String script = String.format(RESTORE_SCRIPT, item.getTrashFile().getAbsolutePath().replace("\\", "\\\\"));
        return executePowerShell(script);
    }

    @Override
    public boolean deletePermanently(TrashItem item)
    {
        return FileSystemUtils.delete(item.getTrashFile());
    }

    private boolean executePowerShell(String script)
    {
        try
        {
            Process p = new ProcessBuilder("powershell.exe", "-Command", script).start();
            return p.waitFor() == 0;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
}
