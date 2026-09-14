package models;


import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 * Класс для отображение информации об файлах, помещённых в корзину
 * TrashItem
 */
public class TrashItem 
{
    private final File trashFile; // физический файл
    private final File infoFile; // файл метаданных
    
    private final StringProperty originalName;
    private final StringProperty originalPath;
    private final StringProperty deletionDate;

    public TrashItem(File trashFile, File infoFile, String originalPath, String deletionDate)
    {
        this.trashFile = trashFile;
        this.infoFile = infoFile;

        this.originalPath = new SimpleStringProperty(originalPath);
        this.originalName = new SimpleStringProperty(new File(originalPath).getName());
        this.deletionDate = new SimpleStringProperty(deletionDate);
    }

    public StringProperty originalNameProperty() { return originalName; }
    public StringProperty originalPathProperty() { return originalPath; }
    public StringProperty deletionDateProperty() { return deletionDate; }

    public String getOriginalName() { return originalName.get(); }
    public String getOriginalPath() { return originalPath.get(); }
    public String getDeletionDate() { return deletionDate.get(); }

    public File getTrashFile() { return trashFile; }
    public File getInfoFile() { return infoFile; }
}
