package utils;


import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;
import resourceHandler.ResourceHandler;

import java.io.IOException;


public class ContextMenuManager
{
    // контекстное меню для панели
    private static ContextMenu panelContextMenu;

    /**
     * Получить контекстное меню дял панели. Если его ещё нет, создаст его
     * @return контекстное меню для панели
     * */
    public static ContextMenu getPanelContextMenu()
    {
        if (panelContextMenu == null)
            createPanelContextMenu();
        return panelContextMenu;
    }

    @Nullable
    public static MenuItem getOpenMenuItem()
    {
        if (panelContextMenu == null)
            return null;

        return panelContextMenu.getItems().stream()
                .filter(menuItem -> menuItem.getId().equals("openMenuItem"))
                .findFirst()
                .orElse(null);
    }

    /**
     * Создать контекстное меню для панели
     * */
    private static void createPanelContextMenu()
    {
        try
        {
            FXMLLoader contextMenuLoader = new FXMLLoader(ResourceHandler.getLayout("ContextMenu.fxml"));
            panelContextMenu = contextMenuLoader.load();
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось загрузить меню" + ex.getMessage());
        }
    }
}
