package context;

import models.FileData;
import javafx.scene.Node;

public interface IMenuContext 
{
    /**
     * Получить инфомрацию по файлу
     * @return информация по файлу
     */
    FileData getFileData();
    /**
     * Выполнить действие при нажатии на кнопку иеню
     * @param actionID ID кнопки меню
     */
    void executeAction(String actionID);
    /**
     * Можно ли нажать на кнопку меню
     * @param actionID ID кнопки меню
     * @return true, если кнопка доступна, иначе false
     */
    boolean isActionEnabled(String actionID);

    /**
     * Получить иконку для конпки меню
     * @param actionID ID кнопки меню
     * @return Возвращает Null, если не удалось получить информацию о файле или если для переданного действия нужно оставить иконку из fxml файла. 
     * Иначе возвращает иконку
     */
    Node getActionGraphic(String actionID);
}
