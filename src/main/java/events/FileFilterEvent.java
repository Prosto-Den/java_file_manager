package events;

import java.util.UUID;


/**
 * Событие фильтрации таблицы
 * FileFilterEvent
 */
public class FileFilterEvent 
{
    private final UUID fileSystemId;
    private final String filter;

    public FileFilterEvent(UUID fileSystemId, String filter)
    {
        this.fileSystemId = fileSystemId;
        this.filter = filter;
    }

    /**
     * Получить идентификатор файловой системы
     * @return идентификатор файловой системы
     */
    public UUID getFileSystemId() { return fileSystemId; }

    /**
     * Получить фильтр для настройки отображения файлов
     * @return фильтр
     */
    public String getFilter() { return filter; }
}
