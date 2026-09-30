package utils.filesystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Path;

/**
 * Класс для работы с объектами файловой системы.
 * */
public class FileSystemController
{
    private static final Map<UUID, FileSystem> instances = new HashMap<>();

    /**
     * Создать файловую систему. После создания будет указывать на корень системы (C:\ у Windows и / у Linux)
     * @return UUID созданной файловой системы
     * */
    public static UUID create()
    {
        return create(Path.of(""));
    }

    /**
     * Создать файловую систему. После создания будет указывать на переданный путь
     * @param path путь, на который файловая системы должна указывать
     * @return UUID созданной файловой системы
     * */
    public static UUID create(Path path)
    {
        UUID id = UUID.randomUUID();
        instances.put(id, new FileSystem(path, id));
        return id;
    }

    /**
     * Получить файловую систему по её UUID
     * */
    public static FileSystem get(UUID id)
    {
        return instances.get(id);
    }

    /**
     * Удалить файловую систему
     * @param id идентефикатор файловой системы
     */
    public static void delete(UUID id)
    {
        instances.remove(id);
    }
}
