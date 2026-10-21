package events;

import java.util.UUID;


/**
 * Событие изменений в директории. Присылается, если в текущей директории произошли какие-то изменения
 * FileSystemChanged
 */
public final class FileSystemChanged 
{
    private final UUID fileSystemId;

    public FileSystemChanged(UUID fileSystemId)
    {
        this.fileSystemId = fileSystemId;
    }

    /**
     * Получить идентификатор файловой системы, в которой произошли изменения
     * @return идентификатор файловой системы
     */
    public UUID getFileSystemId() { return fileSystemId; }
}
