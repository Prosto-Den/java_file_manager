package utils.trash;

import models.TrashItem;
import java.util.List;

public interface ITrashManager 
{
    List<TrashItem> getTrashItems();
    boolean restoreItem(TrashItem item);
    boolean deletePermanently(TrashItem item);
}
