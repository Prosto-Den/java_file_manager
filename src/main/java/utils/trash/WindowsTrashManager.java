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
    private final String SEARCH_SCRIPT = "(New-Object -ComObject Shell.Application).NameSpace(0x0a).Items() | " +
            "Select-Object Name, " +
            "Path, " +
            "@{n='OriginalPath';e={$_.ExtendedProperty('{9B174B33-40FF-11D2-A27E-00C04FC30871} 2')}}, " +
            "@{n='DeletionDate';e={$_.ExtendedProperty('{9B174B33-40FF-11D2-A27E-00C04FC30871} 3')}} | " +
            "ConvertTo-Csv -NoTypeInformation";

    private final String RESTORE_SCRIPT = "$shell = New-Object -ComObject Shell.Application; " +
            "$recycleBin = $shell.NameSpace(0x0a); " +
            "$item = $recycleBin.Items() | Where-Object { $_.Path -eq '%s' } | Select-Object -First 1; " +
            "if ($item) { $item.InvokeVerb('Undelete'); Write-Output 'SUCCESS' } else { Write-Output 'NOT_FOUND' }";

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
                    String[] parts = line.split("\",\"");
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
        String path = item.getTrashFile().getPath().replace("'", "''");
        String script = String.format(RESTORE_SCRIPT, path);
        return executePowerShell(script);
    }

    @Override
    public boolean deletePermanently(TrashItem item)
    {
        return FileSystemUtils.delete(item.getTrashFile().toPath());
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
