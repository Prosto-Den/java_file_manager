package models;


import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;


public class FileData
{
    private final StringProperty name;
    private final StringProperty size;
    private final StringProperty date;
    private final boolean isDirectory;

    public FileData(String name, String size, String date, boolean isDirectory)
    {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.date = new SimpleStringProperty(date);
        this.isDirectory = isDirectory;
    }

    public StringProperty getName() {return name;}
    public StringProperty getSize() {return size;}
    public StringProperty getDate() {return date;}

    public String getNameValue() {return name.getValue();}
    public String getSizeValue() {return size.getValue();}
    public String getDateValue() {return date.getValue();}

    public boolean isDirectory() {return this.isDirectory;}
}
