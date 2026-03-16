package models;

/**
 * Идентификаторы для определения стороны файловой системы
 * */
public enum FileSystemSide
{
    LEFT("left"),
    RIGHT("right");

    private final String id;
    FileSystemSide(String id) {this.id = id;}

    public String getValue() {return id;}
}
