package models;

//"614e7340-84cc-4f87-9411-c5d19d2bded0"
//"fc51e601-d291-46fb-abb7-52bfc7848b38"

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
