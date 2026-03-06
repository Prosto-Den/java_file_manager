package utils;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import models.FileData;
import models.StringKeys;
import org.jetbrains.annotations.Nullable;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import models.ContextMenuItemId;

import java.io.File;
import java.io.IOException;


/**
 * Класс для работы с контекстным меню
 * */
public class ContextMenuManager
{
    public static ContextMenu createPanelContextMenu(FileSystem fileSystem, TableRow<FileData> row,
                                                     Runnable onRefresh)
    {
        ContextMenu menu = null;

        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceHandler.getLayout("ContextMenu.fxml"));
            menu = loader.load();

            commonItemConfiguration(menu);

            MenuItem openItem = (MenuItem) loader.getNamespace().get(ContextMenuItemId.OPEN_ITEM);

            ContextMenu tempVar = menu;
            menu.setOnShowing(event -> {
                FileData fileInfo = row.getItem();
                if (fileInfo == null)
                    event.consume();
                else
                {
                    configureOpenItem(openItem, fileInfo);
                    setUserData(tempVar, fileInfo);
                }
            });

            openItem.setOnAction(event -> onOpenItemClick(event, fileSystem, onRefresh));
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось загрузить меню" + ex.getMessage());
        }

        return menu;
    }

    private static void configureOpenItem(MenuItem item, FileData fileInfo)
    {
        if (item != null)
        {
            if (fileInfo.isDirectory())
                item.setGraphic(new ImageView(ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FOLDER)));
            else
                item.setGraphic(new ImageView(ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FILE)));
        }
    }

    private static void onOpenItemClick(ActionEvent event, FileSystem fileSystem, Runnable onRefresh)
    {
        MenuItem item = (MenuItem) event.getSource();
        FileData fileInfo = (FileData) item.getUserData();

        if (fileInfo.isDirectory())
        {
            fileSystem.goForward(fileInfo.getNameValue());
            onRefresh.run();
        }
        else
            FileSystemUtils.openFile(fileSystem.buildPath(fileInfo.getNameValue()));
    }

    private static void setUserData(ContextMenu menu, Object userData)
    {
        for (MenuItem item : menu.getItems())
            if (!(item instanceof SeparatorMenuItem))
                item.setUserData(userData);
    }

    private static void commonItemConfiguration(ContextMenu menu)
    {
        for (MenuItem item : menu.getItems())
        {
            String text = "";

            switch (item.getId())
            {
                case ContextMenuItemId.OPEN_ITEM -> text = ResourceHandler.getString(StringKeys.CONTEXT_MENU_OPEN_ITEM);
                case ContextMenuItemId.COPY_ITEM -> text = ResourceHandler.getString(StringKeys.CONTEXT_MENU_COPY_ITEM);
                case ContextMenuItemId.DELETE_ITEM -> text = ResourceHandler.getString(
                        StringKeys.CONTEXT_MENU_DELETE_ITEM);
            }

            if (!text.isEmpty())
                item.setText(text);
        }
    }
}
