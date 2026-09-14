package utils.ui.context;

import javafx.scene.Node;

/**
 * Интерфейс для обмена информацией между виджетом и контекстным меню
 */
public abstract class IContextMenuConfig 
{   
    protected Object data;

    /**
     * Получить информацию по файлу
     * @return информация по файлу
     */
    public Object getUserData() { return data; };
    /**
     * Выполнить действие при нажатии на кнопку меню
     * @param actionID ID кнопки меню
     */
    abstract public void executeAction(String actionID);
    /**
     * Доступность кнопки меню
     * @param actionID ID кнопки меню
     * @return true, если кнопка доступна, иначе false
     */
    abstract public boolean isActionEnabled(String actionID);

    /**
     * Получить иконку для кнопки меню
     * @param actionID ID кнопки меню
     * @return Возвращает Null, если не удалось получить информацию о файле или если для переданного действия нужно оставить иконку из fxml файла. 
     * Иначе возвращает иконку
     */
    abstract public Node getActionGraphic(String actionID);
}
