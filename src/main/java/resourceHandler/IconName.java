package resourceHandler;

public enum IconName
{
    // control_panel
    ADD("add.png"),
    BACK_ARROW("back_arrow.png"),
    DISK("disk.png"),
    FORWARD_ARROW("forward_arrow.png"),
    INSERT("insert.png"),

    // file_viewer
    BACK("back.png"),
    FILE("file.png"),
    FOLDER("folder.png"),
    TEXT_FILE("text_file.png"),
    WORD("word.png"),

    // system
    ERROR("error.png"),
    LOADING("loading.gif"),

    // toolbar
    FIND_DUPLICATES("find_duplicates.png"),
    SETTINGS("settings.png");

    private final String iconName;

    IconName(String name) {iconName = name;}

    public String getValue() {return iconName;}
}
