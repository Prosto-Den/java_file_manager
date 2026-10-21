package events;

import java.util.UUID;


/**
 * Событие смены текущей директории
 * PathChangedEvent
 */
public final class PathChangedEvent 
{
    private UUID fileSystemId;

    public PathChangedEvent(UUID fileSystemId)
    {
        this.fileSystemId = fileSystemId;
    }

    /**
     * Получить идентификатор файловой системы, 
     * в которой произошла смена директории
     * @return идентификатор файловой системы
     */
    public UUID getFileSystemId() { return fileSystemId; }
}
