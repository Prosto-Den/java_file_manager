package context;

import models.FileData;
import javafx.scene.Node;

/**
 * Интерфейс для лбмена инфомрацией между виджетом и контекстным меню
 */
public interface IMenuContext 
{
    /**
     * Получить информацию по файлу
     * @return информация по файлу
     */
    FileData getFileData();
    /**
     * Выполнить действие при нажатии на кнопку меню
     * @param actionID ID кнопки меню
     */
    void executeAction(String actionID);
    /**
     * Доступность кнопки меню
     * @param actionID ID кнопки меню
     * @return true, если кнопка доступна, иначе false
     */
    boolean isActionEnabled(String actionID);

    /**
     * Получить иконку для кнопки меню
     * @param actionID ID кнопки меню
     * @return Возвращает Null, если не удалось получить информацию о файле или если для переданного действия нужно оставить иконку из fxml файла. 
     * Иначе возвращает иконку
     */
    Node getActionGraphic(String actionID);
}
