package watchers;


import java.util.UUID;

import events.FileSystemChanged;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent;
import java.nio.file.ClosedWatchServiceException;

import events.EventBus;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * Смотритель за директорией. Реагирует на любые изменения файлов в директории
 * FolderWatcher
 */
public class FolderWatcher implements Runnable
{
    private final UUID fileSystemId;
    private final Path directory;
    private WatchService watchService;
    private volatile boolean isRunning = true;
    private final Logger logger = Logger.getLogger(FolderWatcher.class.getName());

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
            logger.log(Level.SEVERE, "Не удалось запустить наблюдение для папки: " + ex);
            isRunning = false;
        }
    }

    /**
     * Запустить смотрителя
     * @see java.lang.Runnable#run()
     */
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
                    logger.info("Сервис закрыт программно. Остановка мониторинга для: " + directory);
                    break;
                }
                catch (InterruptedException ex)
                {
                    logger.info("Мониторинг для папки " + directory.toString() + " прерван");
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                        continue;
                
                    EventBus.publish(new FileSystemChanged(fileSystemId));

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

        logger.info("[Watcher]: Поток полностью завершил работу для папки: " + directory);
    }

    /**
     * Остановить смотрителя
     */
    public void stop()
    {
        isRunning = false;
        cleanup();
    }

    // Приватные методы

    /**
     * Произвести очистку после остановки
     */
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
