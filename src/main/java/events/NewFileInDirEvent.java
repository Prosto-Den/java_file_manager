package events;


import java.nio.file.Path;
import java.util.UUID;

public final class NewFileInDirEvent 
{
    private final UUID fileSystemId;

    public NewFileInDirEvent(UUID fileSystemId)
    {
        this.fileSystemId = fileSystemId;
    }

    public UUID getFileSystemId() { return fileSystemId; }
}
