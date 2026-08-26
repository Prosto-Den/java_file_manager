package events;


public class FileSystemChangedEvent
{
    private final String fileSystemId;

    public FileSystemChangedEvent(String id)
    {
        fileSystemId = id;
    }

    public String getFileSystemId() { return fileSystemId; }
}