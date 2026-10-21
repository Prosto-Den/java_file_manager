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

    /**
     * Получить имя файла в виде проперти
     * @return проперти имени файла
     */
    public StringProperty getName()
    {
        return new SimpleStringProperty(getNameValue());
    }

    /**
     * Получить имя файла в виде строки
     * @return имя файла в виде строки
     */
    public String getNameValue() 
    {
       return nameProperty.getValue();
    }

    /**
     * Получить путь к файлу
     * @return путь к файлу
     */
    public Path getPath()
    {
        return filePath;
    }

    /**
     * Получить размер файла в виде строки
     * @return размер файла в виде строки
     */
    public String getSizeValue() {
        return sizeProperty.getValue();
    }

    /**
     * Получить дату последней модификации файла в виде строки
     * @return дата последней модификации файла в виде строки
     */
    public String getDateValue() {
        return dateProperty.getValue();
    }
}