package utils;

import models.FileSystemSide;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Класс для работы с объектами файловой системы.
 * */
public class FileSystemController
{
    private static final Map<String, FileSystem> instances = new HashMap<>();
    private static final Map<String, FileSystemSide> instancesSides = new HashMap<>();

    public static String create(FileSystemSide side)
    {
        String id = UUID.randomUUID().toString();
        instances.put(id, new FileSystem());
        instancesSides.put(id, side);
        return id;
    }

    public static String create(FileSystemSide side, String path)
    {
        String id = UUID.randomUUID().toString();
        instances.put(id, new FileSystem(path));
        instancesSides.put(id, side);
        return id;
    }

    public static FileSystem get(String id)
    {
        return instances.get(id);
    }

    public static FileSystemSide getSide(String id)
    {
        return instancesSides.get(id);
    }
}
