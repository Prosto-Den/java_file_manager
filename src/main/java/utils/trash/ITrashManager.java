package utils.trash;

import models.TrashItem;
import java.util.List;

/**
 * Интерфейс для менеджера корзины
 * ITrashManager
 */
public interface ITrashManager 
{   
    /**
     * Получить объекты с информацией по удалённым файлам
     * @return список объектов с информацией по удалённым файлам
     */
    List<TrashItem> getTrashItems();

    /**
     * Восстановить файл
     * @param item файл для восстановления
     * @return true, если удалось выполнить восстановление, иначе false
     */
    boolean restoreItem(TrashItem item);

    /**
     * Удалить файл навсегда
     * @param item файл для удаления
     * @return true, если удалось удалить файл, инае false
     */
    boolean deletePermanently(TrashItem item);
}
