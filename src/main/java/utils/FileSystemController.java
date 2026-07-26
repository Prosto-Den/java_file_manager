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

    /**
     * Создать файловую систему. После создания будет указывать на корень системы (C:\ у Windows и / у Linux)
     * @return UUID созданной файловой системы
     * */
    public static String create()
    {
        return create("");
    }

    /**
     * Создать файловую систему. После создания будет указывать на переданный путь
     * @param path путь, на который файловая системы должна указывать
     * @return UUID созданной файловой системы
     * */
    public static String create(String path)
    {
        String id = UUID.randomUUID().toString();
        instances.put(id, new FileSystem(path));
        return id;
    }

    /**
     * Получить файловую систему по её UUID
     * */
    public static FileSystem get(String id)
    {
        return instances.get(id);
    }
}
