package watchers;


import java.util.UUID;

import events.NewFileInDirEvent;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent;
import java.nio.file.ClosedWatchServiceException;

import events.EventBus;


public class FolderWatcher implements Runnable
{
    private final UUID fileSystemId;
    private final Path directory;
    private WatchService watchService;
    private volatile boolean isRunning = true;

    public FolderWatcher(UUID fileSystemId, Path directory)
    {
        this.fileSystemId = fileSystemId;
        this.directory = directory;

        try
        {
            watchService = FileSystems.getDefault().newWatchService();
            this.directory.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, 
                                                       StandardWatchEventKinds.ENTRY_DELETE, 
                                                       StandardWatchEventKinds.ENTRY_MODIFY);
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось заустить наблюдение для папки: " + ex.getMessage()); 
            isRunning = false;
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (isRunning && !Thread.currentThread().isInterrupted())
            {
                WatchKey key;
                try 
                {
                    key = watchService.take();
                }
                catch (ClosedWatchServiceException ex)
                {
                    System.out.println("Сервис закрыт программно. Остановка мониторинга для: " + directory);
                    break;
                }
                catch (InterruptedException ex)
                {
                    System.out.println("Мониторинг для папки " + directory.toString() + " прерван");
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                        continue;
                
                    EventBus.publish(new NewFileInDirEvent(fileSystemId));

                    break;
                }

                if (!key.reset())
                    break;
            }
        }
        finally
        {
            cleanup();
        }

        System.out.println("[Watcher]: Поток полностью завершил работу для папки: " + directory);
    }

    public void stop()
    {
        isRunning = false;
        cleanup();
    }

    private void cleanup()
    {
        if (watchService != null)
        {
            try
            {
                watchService.close();
            }
            catch (IOException ex)
            {

            }
        }
    }
}
