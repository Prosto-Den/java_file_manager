package events;


import java.nio.file.Path;


public final class NewFileInDirEvent 
{
    private final Path dirPath;

    public NewFileInDirEvent(Path path)
    {
        dirPath = path;
    }

    public Path getPath() { return dirPath; }
}
