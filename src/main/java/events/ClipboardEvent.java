package events;


/**
 * Событие изменения содержимого буфера обмена
 * ClipboardEvent
 */
public final class ClipboardEvent
{
    private final boolean hasFiles;

    public ClipboardEvent(boolean hasFiles)
    {
        this.hasFiles = hasFiles;
    }

    /**
     * Получить флаг наличия файлов в буфере обмена
     * @return true, если в буфере обмена есть файлы, иначе false
     */
    public boolean isHasFiles() {return hasFiles;}
}
