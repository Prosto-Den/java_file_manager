package events;

public class ClipboardEvent
{
    private final boolean hasFiles;

    public ClipboardEvent(boolean hasFiles)
    {
        this.hasFiles = hasFiles;
    }

    public boolean isHasFiles() {return hasFiles;}
}
