package utils.ui;

import java.io.File;
import java.util.Collections;
import java.nio.file.Path;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import utils.filesystem.FileSystemUtils;

/**
 * Утилитный класс для работы с буфером обмена
 */
public final class ClipboardUtil
{
    private static final Clipboard clipboard = Clipboard.getSystemClipboard();

    /**
     * Скопировать содержимое в буфер обмена
     * @param path - путь к файлу
     */
    public static void copyToClipboard(Path path)
    {
        ClipboardContent content = new ClipboardContent();

        content.putFiles(Collections.singletonList(path.toFile()));
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

    public static void insert(Path path)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasFiles())
        {
            for (File file : clipboard.getFiles())
            {
                Path destFile = path.resolve(file.getName());
                FileSystemUtils.copyFile(file.toPath(), destFile);
            }
        }
    }
}
