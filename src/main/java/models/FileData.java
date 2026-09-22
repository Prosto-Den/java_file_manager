package models;


import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.nio.file.Path;

/**
 * Класс для хранения информации по файлу. Необходимо для отображения данных в менеджере
 *
 */
public record FileData(Path filePath, StringProperty nameProperty, StringProperty sizeProperty, StringProperty dateProperty)
{
    public FileData(Path filePath, String size, String date)
    {
        this(filePath, 
            new ReadOnlyStringWrapper(filePath.getFileName() != null ? filePath.getFileName().toString() : ""),
            new ReadOnlyStringWrapper(size),
            new ReadOnlyStringWrapper(date)
        );
    }

    public StringProperty getName()
    {
        return new SimpleStringProperty(getNameValue());
    }

    public String getNameValue() 
    {
       return nameProperty.getValue();
    }

    public Path getPath()
    {
        return filePath;
    }

    public String getSizeValue() {
        return sizeProperty.getValue();
    }

    public String getDateValue() {
        return dateProperty.getValue();
    }
}