package utils.filesystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import app.AppContext;
import watchers.FolderWatcher;

import java.nio.file.Path;

/**
 * Класс для работы с объектами файловой системы.
 * */
public class FileSystemController
{
    private static final Map<UUID, FileSystem> instances = new HashMap<>();
    private static final Map<UUID, FolderWatcher> watchers = new HashMap<>();
    
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
        updateWatcher(id);
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
        stopWatcher(id);
        instances.remove(id);
    }

    /**
     * Обновить наблюдателя за файловой системой
     * @param id идентификатор файловой системы
     */
    public static void updateWatcher(UUID id)
    {
        stopWatcher(id);
        FolderWatcher watcher = new FolderWatcher(id, instances.get(id).getCurrentPath());
        watchers.put(id, watcher);
        AppContext.getThreadPool().execute(watcher);
    }

    /**
     * Остановить наблюдателя за файловой системой
     * @param id идентификатор файловой системы
     */
    public static void stopWatcher(UUID id)
    {
        FolderWatcher watcher = watchers.remove(id);
        if (watcher != null)
            watcher.stop();
    }
}
