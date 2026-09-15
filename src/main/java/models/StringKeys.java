package models;


/**
 * Коды строковых ресурсов из xml файла строковых ресурсов
 * */
public class StringKeys
{
    // заголовок приложения
    public static final String TITLE = "title";

    // названия колонок панели
    public static final String PANEL_COLUMN_FILENAME = "panel.column.filename";
    public static final String PANEL_COLUMN_FILE_SIZE = "panel.column.fileSize";
    public static final String PANEL_COLUMN_EDIT_DATE = "panel.column.fileEditDate";

    // подсказки для элементов управления
    public static final String COMBOBOX_DISK_TOOLTIP = "combobox.disk.tooltip";
    public static final String BUTTON_ADD_TOOLTIP = "button.add.tooltip";
    public static final String BUTTON_BACK_TOOLTIP = "button.back.tooltip";
    public static final String BUTTON_FORWARD_TOOLTIP = "button.forward.tooltip";
    public static final String BUTTON_INSERT_TOOLTIP = "button.insert.tooltip";

    // текста кнопок
    public static final String BUTTON_ADD_TEXT = "button.add.text";

    // кнопки контекстного меню
    public static final String CONTEXT_MENU_OPEN_ITEM = "contextmenu.item.open";
    public static final String CONTEXT_MENU_COPY_ITEM = "contextmenu.item.copy";
    public static final String CONTEXT_MENU_DELETE_ITEM = "contextmenu.item.delete";
    public static final String CONTEXT_MENU_MOVE_TO_TRASH_ITEM = "contextmenu.item.moveToTrash";
    public static final String CONTEXT_MENU_RESTORE_ITEM = "contextmenu.item.restore";
    public static final String CONTEXT_MENU_DELETE_PERMANENTLY_ITEM = "contextmenu.item.deletePermanently";

    // "Назад"
    public static final String FILEVIEWER_ROW_BACK = "fileviewer.row.back";

    public static final String TRASH_TITLE = "trash.title";

    // названия колонок корзины
    public static final String TRASHVIEWER_COLUMN_FILENAME = "trashviewer.column.filename";
    public static final String TRASHVIEWER_COLUMN_PATH = "trashviewer.column.path";
    public static final String TRASHVIEWER_COLUMN_DATE = "trashviewer.column.date";

    // настройки
    public static final String SETTINGS_TITLE = "settings.title";
    public static final String SETTINGS_LANGUAGE_LABEL = "settings.label.language";
    public static final String SETTINGS_BUTTON_SAVE = "settings.button.save";
    public static final String SETTINGS_BUTTON_CANCEL = "settings.button.cancel";
    public static final String SETTINGS_BUTTON_APPLY = "settings.button.apply";
    public static final String SETTINGS_LANGUAGE_TITLE = "settings.language.title";
    public static final String SETTINGS_LINUX_TITLE = "settings.linux.title";
    public static final String SETTINGS_LINUX_USED_TERMINAL = "settings.linux.used_terminal";
    public static final String SETTINGS_LINUX_OPEN_COMMAND = "settings.linux.open_command";
    public static final String SETTINGS_LINUX_MOVE_TO_TRASH_COMMAND = "settings.linux.move_to_trash_command";

    // элеменыт выпадающего меню создания файлов
    public static final String CREATE_FOLDER_ITEM = "addmenu.item.folder";
    public static final String CREATE_TEXT_FILE_ITEM = "addmenu.item.textfile";

    // паттерн создания новых файлов
    public static final String PATTERN_NEW_FILE = "pattern.newfile";

    // имена новых файлов
    public static final String NEW_FOLDER_NAME = "new.folder.name";
    public static final String NEW_TEXT_FILE_NAME = "new.textfile.name";

    // текста предупреждающих окон
    public static final String ALERT_DELETE_TITLE = "alert.delete.title";
    public static final String ALERT_DELETE_HEADER = "alert.delete.header";
    public static final String ALERT_DELETE_TEXT = "alert.delete.text";
    public static final String ALERT_RESTORE_ERROR_TITLE = "alert.restore.error.title";
    public static final String ALERT_RESTORE_ERROR_TEXT = "alert.restore.error.text";

    // текста ошибок
    public static final String ERROR_LOAD_USER_SETTINGS = "error.load_user_settings";
    public static final String ERROR_SAVE_USER_SETTINGS = "error.save_user_settings";
    public static final String ERROR_LOAD_DEFAULT_SETTINGS = "error.load_default_settings";
}
