package events;

import java.util.UUID;

public final class PathChangedEvent 
{
    private UUID fileSystemId;

    public PathChangedEvent(UUID fileSystemId)
    {
        this.fileSystemId = fileSystemId;
    }

    public UUID getFileSystemId() { return fileSystemId; }
}
