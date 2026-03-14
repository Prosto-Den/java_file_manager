package utils;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
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
}


/**
 * Класс для работы с контекстным меню
 * */
public class ContextMenuManager
{
    // шаблон контекстного меню для панели
    private static ContextMenu panelMenuTemplate = null;

    /**
     * Создать контекстное меню для панели
     * @param fileSystem файловая система, используемая данной панелью
     * @param row ряд с данными по файлу
     * @param onRefresh функция перерисовки панели. Нужно для некоторых действий
     * */
    //TODO заменить передачу объекта файловой системы на ID файловой системы
    //TODO заменить ряд на объекта FileInfo?
    public static ContextMenu createPanelContextMenu(FileSystem fileSystem, TableRow<FileData> row,
                                                     Runnable onRefresh)
    {
        createPanelMenuTemplate();

        ContextMenu menu = copyMenu(panelMenuTemplate);

        Optional<MenuItem> openItem = getMenuItem(menu, ContextMenuItemId.OPEN_ITEM);
        Optional<MenuItem> copyItem = getMenuItem(menu, ContextMenuItemId.COPY_ITEM);
        Optional<MenuItem> deleteItem = getMenuItem(menu, ContextMenuItemId.DELETE_ITEM);
        Optional<MenuItem> moveToTrashItem = getMenuItem(menu, ContextMenuItemId.MOVE_TO_TRASH_ITEM);

        menu.setOnShowing(event -> {
            FileData fileInfo = row.getItem();
            if (fileInfo == null)
                event.consume();
            else
            {
                openItem.ifPresent(menuItem -> configureOpenItem(menuItem, fileInfo));
                setUserData(menu, fileInfo);
            }
        });

        openItem.ifPresent(item -> item.setOnAction(event ->
                onOpenItemClick(event, fileSystem, onRefresh)));

        copyItem.ifPresent(item -> item.setOnAction(event -> onCopyItemClick(event, fileSystem)));

        deleteItem.ifPresent(item -> item.setOnAction(event ->
                onDeleteItemClick(event, fileSystem, onRefresh)));

        moveToTrashItem.ifPresent(item -> item.setOnAction(event ->
                onMoveToTrashItemClick(event, fileSystem, onRefresh)));

        return menu;
    }

    /**
     * Настроить кнопку "Открыть"
     * @param item кнопка из контекстного меню
     * @param fileInfo информация ою открываемом файле
     * */
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

    /**
     * Поведение при нажатии на кнопку "Открыть"
     * @param event событие нажатия на кнопку
     * @param fileSystem объект файловой системы, связанный с текущей панелью
     * @param onRefresh действия по обновлению панели
     * */
    private static void onOpenItemClick(ActionEvent event, FileSystem fileSystem, Runnable onRefresh)
    {
        MenuItem item = (MenuItem) event.getSource();
        FileData fileInfo = (FileData) item.getUserData();

        if (fileInfo.isDirectory())
        {
            fileSystem.goForward(fileInfo.getNameValue());
            //TODO точно ли хорошая идея передавать сюда Runnable? Возможно стоит подключить шину событий
            onRefresh.run();
        }
        else
            FileSystemUtils.openFile(fileSystem.buildPath(fileInfo.getNameValue()));
    }

    /**
     * Поведение при нажатии кнопки "Скопировать"
     * @param event событие нажатия на кнопку
     * @param fileSystem объект файловой системы, связанный с текущей панелью
     * */
    private static void onCopyItemClick(ActionEvent event, FileSystem fileSystem)
    {
        MenuItem item = (MenuItem) event.getSource();
        FileData fileInfo = (FileData) item.getUserData();
        String path = fileSystem.buildPath(fileInfo.getNameValue());
        FileSystemUtils.copyToClipboard(path);
    }

    /**
     * Поведение при нажатии на кнопку "Удалить"
     * @param event событие нажатия на кнопку
     * @param fileSystem объект файловой системы, связанный с текущей панелью
     * @param onRefresh действия по обновлению панели
     * */
    private static void onDeleteItemClick(ActionEvent event, FileSystem fileSystem, Runnable onRefresh)
    {
        MenuItem item = (MenuItem) event.getSource();
        FileData fileInfo = (FileData) item.getUserData();
        String path = fileSystem.buildPath(fileInfo.getNameValue());
        FileSystemUtils.delete(path);
        //TODO точно ли хорошая идея передавать сюда Runnable? Возможно стоит подключить шину событий
        onRefresh.run();
    }

    /**
     * Поведение при нажатии на кнопку "Переместить в корзину"
     * @param event события нажатия на кнопку
     * @param fileSystem объект файловой системы, связанный с текущей панелью
     * @param onRefresh действия по обновлению панели
     * */
    private static void onMoveToTrashItemClick(ActionEvent event, FileSystem fileSystem, Runnable onRefresh)
    {
        MenuItem item = (MenuItem) event.getSource();
        FileData fileInfo = (FileData) item.getUserData();
        String path = fileSystem.buildPath(fileInfo.getNameValue());
        FileSystemUtils.moveToTrash(path);
        //TODO точно ли хорошая идея передавать сюда Runnable? Возможно стоит подключить шину событий
        onRefresh.run();
    }

    /**
     * Установить пользовательские данные всем объектам в контекстном меню
     * @param menu контекстное меню
     * @param userData пользовательские данные
     * */
    private static void setUserData(ContextMenu menu, Object userData)
    {
        for (MenuItem item : menu.getItems())
            if (!(item instanceof SeparatorMenuItem))
                item.setUserData(userData);
    }

    /**
     * Получить item контекстного меню по его id
     * @param menu меню, в котором ищется item
     * @param id item id
     * @return MenuItem, если он был найден
     * */
    private static Optional<MenuItem> getMenuItem(ContextMenu menu, String id)
    {
        return menu.getItems().stream()
                .filter(item -> item.getId() != null && item.getId().equals(id))
                .findFirst();
    }

    /**
     * Создать шаблон контекстного меню для панели
     * */
    private static void createPanelMenuTemplate()
    {
        if (panelMenuTemplate == null)
        {
            FXMLLoader loader = new FXMLLoader(ResourceHandler.getLayout("ContextMenu.fxml"),
                    ResourceHandler.getStringBundle());
            try
            {
                panelMenuTemplate = loader.load();
            }
            catch (IOException ex)
            {
                System.err.println("Не удалось загрузить контекстное меню панели :(");
            }
        }
    }

    /**
     * Создаёт новое контекстное меню на основе шаблона. Пользовательские данные из меню-шаблона скопированы не будут
     * @param template меню, выступающее в качестве шаблона
     * @return копия меню-шаблона
     * */
    private static ContextMenu copyMenu(ContextMenu template)
    {
        ContextMenu result = new ContextMenu();

        for (MenuItem item : template.getItems())
        {
            if (item instanceof  SeparatorMenuItem)
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
