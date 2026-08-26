package utils.ui.context;

import app.AppContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import resourceHandler.ResourceHandler;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.Node;

/**
 * Класс для работы с контекстным меню
 * */
public final class ContextMenuManager
{   
    private ContextMenu panelContextMenu;
    private static final String PANEL_CONTEXT_MENU = "ContextMenu.fxml";

    /**
     * Создать контекстное меню для панели или получить его, если оно уже было создано
     * @return контекстное меню для панели
     */
    public ContextMenu createOrGetPanelContextMenu()
    {
        if (panelContextMenu == null)
            panelContextMenu = loadContextMenuLayout(PANEL_CONTEXT_MENU);
        return panelContextMenu;
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
    public Optional<MenuItem> getMenuItem(ContextMenu menu, String id)
    {
        return menu.getItems().stream()
                .filter(item -> item.getId() != null && item.getId().equals(id))
                .findFirst();
    }

    /**
     * Настроить контекстное меню согласно конфигурации
     * @param menu контекстное меню
     * @param context конфигурация
     */
    public void configureContextMenu(ContextMenu menu, IContextMenuConfig context)
    {
        for (MenuItem item : menu.getItems())
        {
            String actionID = item.getId();

            if (actionID == null)
                continue;

            item.setDisable(!context.isActionEnabled(actionID));
            item.setOnAction(event -> context.executeAction(actionID));

            Node icon = context.getActionGraphic(actionID);
            if (icon != null)
                item.setGraphic(icon);
        }

        setUserData(menu, context.getFileData());
    }


    /**
     * Загрузить интерфейс контекстного меню из fxml файла
     * @param layoutFilename имя файла fxml с разметкой контекстного меню
     * @return шаблон контекстного меню
     */
    private ContextMenu loadContextMenuLayout(String layoutFilename)
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
}
