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
    
    private final StringProperty originalName; // оригинальное имя файла
    private final StringProperty originalPath; // путь, по которому находился файл до удаления
    private final StringProperty deletionDate; // дата удаления

    public TrashItem(File trashFile, File infoFile, String fileName, String originalPath, String deletionDate)
    {
        this.trashFile = trashFile;
        this.infoFile = infoFile;

        this.originalPath = new SimpleStringProperty(originalPath);
        this.originalName = new SimpleStringProperty(fileName);
        this.deletionDate = new SimpleStringProperty(deletionDate);
    }

    /**
     * Получить проперти с именем файла
     * @return проперти с именем файла
     */
    public StringProperty originalNameProperty() { return originalName; }

    /**
     * Получить проперти с оригинальным путём к файлу
     * @return проперти с путём к файлу
     */
    public StringProperty originalPathProperty() { return originalPath; }

    /**
     * Получить проперти с датой удаления файла
     * @return проперти с датой удаления
     */
    public StringProperty deletionDateProperty() { return deletionDate; }

    /**
     * Получить имя файла
     * @return имя файла
     */
    public String getOriginalName() { return originalName.get(); }

    /**
     * Получить оригинальный путь файла
     * @return оригинальный путь
     */
    public String getOriginalPath() { return originalPath.get(); }

    /**
     * Получить дату удаления файла
     * @return дата удаления файла
     */
    public String getDeletionDate() { return deletionDate.get(); }

    /**
     * Получить физический файл
     * @return физический файл
     */
    public File getTrashFile() { return trashFile; }

    /**
     * Получить файл метаданных
     * @return файл метаданных
     */
    public File getInfoFile() { return infoFile; }
}
