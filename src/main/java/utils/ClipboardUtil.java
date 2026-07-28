package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Утилитный класс для работы с буфером обмена
 */
public class ClipboardUtil
{
    private static final Clipboard clipboard = Clipboard.getSystemClipboard();

    // TODO проверить на директории
    // TODO реализовать возможность записи нескольких файлов
    /**
     * Скопировать содержимое в буфер обмена
     * @param path - путь к файлу
     */
    public static void copyToClipboard(String path)
    {
        File file = new File(path);
        ClipboardContent content = new ClipboardContent();

        content.putFiles(Collections.singletonList(file));
        clipboard.setContent(content);
    }

    /**
     * Проверить есть ли содержимое в буфере обмена
     * @return true, если есть, иначе false
     */
    public static boolean isClipBoardEmpty()
    {
        return !clipboard.hasFiles();
    }

    /**
     * Вставить содержимое из буфера обмена в директорию
     * @param path - путь к диренктории
     */
    public static void insert(String path)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasFiles())
        {
            List<File> files = clipboard.getFiles();

            for (File file : files)
            {
                File destFile = new File(FileSystemUtils.adjustPath(path, file.getName()));

                try
                {
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException ex)
                {
                    System.err.println("gneg :)");
                }
            }
        }
    }
}
