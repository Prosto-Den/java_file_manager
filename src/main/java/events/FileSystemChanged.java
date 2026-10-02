package events;

import java.util.UUID;

public final class FileSystemChanged 
{
    private final UUID fileSystemId;

    public FileSystemChanged(UUID fileSystemId)
    {
        this.fileSystemId = fileSystemId;
    }

    public UUID getFileSystemId() { return fileSystemId; }
}
