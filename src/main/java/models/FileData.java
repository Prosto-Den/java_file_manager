package models;


import javafx.beans.property.StringProperty;
import utils.filesystem.FileSystemUtils;
import javafx.beans.property.SimpleStringProperty;


/**
 * Класс для хранения информации по файлу. Необходимо для отображения данных в менеджере
 *
 */
public record FileData(StringProperty absolutePath, StringProperty size, StringProperty date, boolean isDirectory)
{
    public FileData(String absolutePath, String size, String date, boolean isDirectory)
    {
        this(new SimpleStringProperty(absolutePath), new SimpleStringProperty(size), new SimpleStringProperty(date),
                isDirectory);
    }

    public StringProperty getName()
    {
        return new SimpleStringProperty(getNameValue());
    }

    public String getNameValue() 
    {
        String absolutePathStr = absolutePath.getValue();
        return FileSystemUtils.getFilenameFromPath(absolutePathStr);
    }

    public String getAbsolutePath()
    {
        return absolutePath.getValue();
    }

    public String getSizeValue() {
        return size.getValue();
    }

    public String getDateValue() {
        return date.getValue();
    }
}