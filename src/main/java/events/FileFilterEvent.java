package events;

import java.util.UUID;

public class FileFilterEvent 
{
    private final UUID fileSystemId;
    private final String filter;

    public FileFilterEvent(UUID fileSystemId, String filter)
    {
        this.fileSystemId = fileSystemId;
        this.filter = filter;
    }

    public UUID getFileSystemId() { return fileSystemId; }
    public String getFilter() { return filter; }
}
