package models;


import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 * Класс для хранения информации по файлу. Необходимо для отображения данных в менеджере
 *
 */
public record FileData(StringProperty name, StringProperty size, StringProperty date, boolean isDirectory)
{
    public FileData(String name, String size, String date, boolean isDirectory)
    {
        this(new SimpleStringProperty(name), new SimpleStringProperty(size), new SimpleStringProperty(date),
                isDirectory);
    }

    public String getNameValue() {
        return name.getValue();
    }

    public String getSizeValue() {
        return size.getValue();
    }

    public String getDateValue() {
        return date.getValue();
    }
}