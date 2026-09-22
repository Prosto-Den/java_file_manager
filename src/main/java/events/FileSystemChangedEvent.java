package events;

import java.nio.file.Path;


public class FileSystemChangedEvent
{
    private final Path path;

    public FileSystemChangedEvent(Path path)
    {
        this.path = path;
    }

    public Path getPath() { return path; }
}