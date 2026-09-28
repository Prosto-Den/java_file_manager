package events;


import java.util.UUID;


/**
 * Событие закрытия вкладки
 * CloseTabEvent
 */
public class CloseTabEvent 
{   
    private final UUID fileSystemId; // идентификатор файловой системы

    /**
     * Конструктор
     * @param id идентификатор файловой системы, который был связан с данной вкладкой
     */
    public CloseTabEvent(UUID id)
    {
        fileSystemId = id;
    }

    /**
     * Выдать идентификатор файловой системы вкладки
     * @return идентификатор файловой системы вкладки
     */
    public UUID getFileSystemId() { return fileSystemId; }
}
