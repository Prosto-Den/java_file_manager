package utils.ui;

import app.AppContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import models.FileData;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;

import java.io.IOException;
import java.util.Optional;

/**
 * Идентификаторы элементов контекстного меню
 * */
class ContextMenuItemId
{
    public static final String OPEN_ITEM = "openMenuItem";
    public static final String COPY_ITEM = "copyMenuItem";
    public static final String DELETE_ITEM = "deleteMenuItem";
    public static final String MOVE_TO_TRASH_ITEM = "moveToTrashMenuItem";
    public static final String OPEN_IN_TERMINAL_ITEM = "openInTerminalMenuItem";
    public static final String REFRESH_ITEM = "refreshMenuItem";
}

/**
 * Класс для работы с контекстным меню
 * */
public class ContextMenuManager
{
    private ContextMenu panelMenuTemplate;
    private static final String PANEL_CONTEXT_MENU = "ContextMenu.fxml";

    public interface PanelContextMenuActions
    {
        /**
         * Открыть файл
         * @param fileInfo информация о файле
         */
        void open(FileData fileInfo);
        /**
         * Скопировать файл
         * @param fileInfo информация о файле
         */
        void copy(FileData fileInfo);
        /**
         * Удалить файл
         * @param fileInfo информация о файле
         */
        void delete(FileData fileInfo);
        /**
         * Переместить файл в корзину
         * @param fileInfo информация о файле
         */
        void moveToTrash(FileData fileInfo);
        /**
         * Открыть в терминале
         * @param fileInfo информация о файле
         */
        void openInTerminal(FileData fileInfo);
        /**
         * Обновить 
         */
        void refresh();
    }

    public ContextMenuManager()
    {
        panelMenuTemplate = createContextMenuTemplate(PANEL_CONTEXT_MENU);
    }

    /**
     * Создать контекстное меню для панели.
     * @param fileInfo информация о файле, на который был вызвано контекстное меню
     * @param actions действия, которые будут выполнены при выборе соответствующего пункта меню
     * @return контекстное меню для панели
     */
    public ContextMenu createPanelContextMenu(FileData fileInfo, PanelContextMenuActions actions)
    {
        if (panelMenuTemplate == null)
            return null;

        ContextMenu menu = copyMenu(panelMenuTemplate);

        Optional<MenuItem> openItem = getMenuItem(menu, ContextMenuItemId.OPEN_ITEM);
        Optional<MenuItem> copyItem = getMenuItem(menu, ContextMenuItemId.COPY_ITEM);
        Optional<MenuItem> deleteItem = getMenuItem(menu, ContextMenuItemId.DELETE_ITEM);
        Optional<MenuItem> moveToTrashItem = getMenuItem(menu, ContextMenuItemId.MOVE_TO_TRASH_ITEM);
        Optional<MenuItem> openInTerminalItem = getMenuItem(menu, ContextMenuItemId.OPEN_IN_TERMINAL_ITEM);
        Optional<MenuItem> refreshMenuItem = getMenuItem(menu, ContextMenuItemId.REFRESH_ITEM);

        menu.setOnShowing(event -> {
            if (fileInfo == null)
                event.consume();
            else
            {
                openItem.ifPresent(menuItem -> configureOpenItem(menuItem, fileInfo));
                openInTerminalItem.ifPresent(menuItem -> menuItem.setDisable(!fileInfo.isDirectory()));
                setUserData(menu, fileInfo);
            }
        });

        openItem.ifPresent(item -> item.setOnAction(event -> {
            FileData selectedFile = getSelectedFile(event);
            if (selectedFile != null && actions != null)
                actions.open(selectedFile);
        }));

        copyItem.ifPresent(item -> item.setOnAction(event -> {
            FileData selectedFile = getSelectedFile(event);
            if (selectedFile != null && actions != null)
                actions.copy(selectedFile);
        }));

        deleteItem.ifPresent(item -> item.setOnAction(event -> {
            FileData selectedFile = getSelectedFile(event);
            if (selectedFile != null && actions != null)
                actions.delete(selectedFile);
        }));

        moveToTrashItem.ifPresent(item -> item.setOnAction(event -> {
            FileData selectedFile = getSelectedFile(event);
            if (selectedFile != null && actions != null)
                actions.moveToTrash(selectedFile);
        }));

        openInTerminalItem.ifPresent(item -> item.setOnAction(event -> {
            FileData selectedFile = getSelectedFile(event);
            if (selectedFile != null && actions != null)
                actions.openInTerminal(selectedFile);
        }));
        
        refreshMenuItem.ifPresent(item -> item.setOnAction(event -> {
            actions.refresh();
        }));

        return menu;
    }

    /**
     * Настроить кнопку "Открыть"
     * @param item кнопка "Открыть"
     * @param fileInfo информация о файле, на который был вызвано контекстное меню
     */
    private void configureOpenItem(MenuItem item, FileData fileInfo)
    {
        if (item != null && fileInfo != null && fileInfo.isDirectory())
            item.setGraphic(new ImageView(ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FOLDER)));
    }

    /**
     * Получить информацию о файле, на который был вызвано контекстное меню
     * @param event событие вызова контекстного меню
     * @return информация о файле, если найдена, иначе null
     */
    private FileData getSelectedFile(ActionEvent event)
    {
        MenuItem item = (MenuItem) event.getSource();
        Object userData = item.getUserData();
        return (userData != null && userData instanceof FileData) ? (FileData) userData : null;
    }

    /**
     * Установить пользовательские данные всем объектам в контекстном меню
     * @param menu контекстное меню
     * @param userData пользовательские данные
     */
    private void setUserData(ContextMenu menu, Object userData)
    {
        for (MenuItem item : menu.getItems())
            if (!(item instanceof SeparatorMenuItem))
                item.setUserData(userData);
    }

    /**
     * Получить item контекстного меню по его id
     * @param menu контекстное меню
     * @param id идентификатор item
     * @return item контекстного меню, если найдено, иначе null
     */
    private Optional<MenuItem> getMenuItem(ContextMenu menu, String id)
    {
        return menu.getItems().stream()
                .filter(item -> item.getId() != null && item.getId().equals(id))
                .findFirst();
    }

    /**
     * Создать шаблон контекстного меню для панели
     * @param layoutFilename имя файла fxml с разметкой контекстного меню
     * @return шаблон контекстного меню
     */
    private ContextMenu createContextMenuTemplate(String layoutFilename)
    {
        FXMLLoader loader = new FXMLLoader(ResourceHandler.getLayout(layoutFilename),
                AppContext.getLanguageManager().getBundle());

        try
        {
            return loader.load();
        }
        catch (IOException ex)
        {
            System.err.println("Не удалось загрузить контекстное меню панели :(");
            return null;
        }
    }

    /**
     * Создаёт новое контекстное меню на основе шаблона. Пользовательские данные из меню-шаблона скопированы не будут
     * @param template шаблон контекстного меню
     * @return новое контекстное меню
     */
    private ContextMenu copyMenu(ContextMenu template)
    {
        ContextMenu result = new ContextMenu();

        for (MenuItem item : template.getItems())
        {
            if (item instanceof SeparatorMenuItem)
                result.getItems().add(new SeparatorMenuItem());
            else
            {
                MenuItem newItem = new MenuItem(item.getText());
                newItem.setId(item.getId());
                newItem.setGraphic(item.getGraphic());

                result.getItems().add(newItem);
            }
        }

        return result;
    }
}
