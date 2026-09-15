package utils.trash;


import models.TrashItem;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import utils.filesystem.FileSystemUtils;

public class LinuxTrashManager implements ITrashManager
{
    private final File trashHome = new File(System.getProperty("user.home"), ".local/share/Trash");
    private final File filesDir = new File(trashHome, "files");
    private final File infoDir = new File(trashHome, "info");

    private record TrashMetaData(String originalPath, String deletionDate) {}

    @Override
    public List<TrashItem> getTrashItems()
    {
        List<TrashItem> items = new ArrayList<>();

        if (!infoDir.exists() || !infoDir.isDirectory())
            return items;

        File[] infoFiles = infoDir.listFiles((dir, name) -> name.endsWith(".trashinfo"));
        if (infoFiles == null)
            return items;

        for (File infoFile : infoFiles)
        {
            String baseName = infoFile.getName().substring(0, infoFile.getName().length() - 10);
            File trashFile = new File(filesDir, baseName);

            // TODO стоит добавить логгирование (+ окно с предупреждением?) что данный файл не удалось найти
            if (!trashFile.exists())
                continue;

            parseTrashInfo(infoFile).ifPresent(metadata -> {
                TrashItem item = new TrashItem(trashFile, infoFile, metadata.originalPath, metadata.deletionDate);
                items.add(item);
            });
        }

        return items;
    }

    @Override
    public boolean restoreItem(TrashItem item)
    {
        File source = item.getTrashFile();
        File target = new File(item.getOriginalPath());

        try
        {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();

            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.delete(item.getInfoFile().toPath());

            return true;
        }
        // TODO логгирование
        catch (IOException ex)
        {
            System.err.println(ex);
            return false;
        }
    }

    @Override
    public boolean deletePermanently(TrashItem item)
    {
        return FileSystemUtils.delete(item.getTrashFile()) && FileSystemUtils.delete(item.getInfoFile());
    }

    private Optional<TrashMetaData> parseTrashInfo(File file)
    {
        String path = null, date = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.startsWith("Path="))
                {
                    path = line.substring(5);
                    path = URLDecoder.decode(path, StandardCharsets.UTF_8);
                }
                else if (line.startsWith("DeletionDate="))
                {
                    date = line.substring(13);
                    date = date.replace("T", " ");
                }
            }

            if (path != null || date != null)
                return Optional.of(new TrashMetaData(path, date));
        }
        // TODO логгирование
        catch (IOException ex)
        {
            System.err.println(ex);
        }

        return Optional.empty();
    } 
}
