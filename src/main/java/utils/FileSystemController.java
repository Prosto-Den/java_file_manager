package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Класс для работы с объектами файловой системы.
 * */
public class FileSystemController
{
    private static final Map<String, FileSystem> instances = new HashMap<>();

    public static String create()
    {
        String id = UUID.randomUUID().toString();
        instances.put(id, new FileSystem());
        return id;
    }

    public static String create(String path)
    {
        String id = UUID.randomUUID().toString();
        instances.put(id, new FileSystem(path));
        return id;
    }

    public static FileSystem get(String id)
    {
        FileSystem fs = instances.get(id);

        //TODO создавать новую или выбрасывать исключение?
        if (fs == null)
            fs = instances.put(id, new FileSystem());

        return fs;
    }
}
