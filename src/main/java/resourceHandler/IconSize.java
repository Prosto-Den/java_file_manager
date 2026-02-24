package resourceHandler;

public enum IconSize
{
    SMALL("16x16"),
    BIG("24x24"),
    NO_SIZE("");

    private final String size;

    IconSize(String size) {this.size = size;}

    public String getValue() {return this.size;}
}
