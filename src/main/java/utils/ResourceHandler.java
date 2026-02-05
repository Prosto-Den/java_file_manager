package utils;


import java.net.URL;
import javafx.scene.image.Image;
import types.IconTypes;


public class ResourceHandler
{
    static private class ResourceManager
    {
        private final String fileViewerIconPath = "/icons/file_viewer";
        private final String controlPanelIconPath = "/icons/control_panel";
        private final String systemIconPath = "/icons/system";
        private final String toolbarIconPath = "/icons/toolbar";

        public URL buildPath(IconTypes type, String size, String iconName)
        {
            String path;

            switch (type)
            {
                case FILE_VIEWER -> path = String.join("/", fileViewerIconPath, size, iconName);
                case CONTROL_PANEL -> path = String.join("/", controlPanelIconPath, size, iconName);
                case SYSTEM -> path = String.join("/", systemIconPath, size, iconName);
                case TOOLBAR -> path = String.join("/", toolbarIconPath, size, iconName);
                default -> path = "";
            }

            return getClass().getResource(path);
        }
    }

    static public Image getIcon(IconTypes type, String size, String iconName)
    {
        ResourceManager manager = new ResourceManager();
        URL url = manager.buildPath(type, size, iconName);

        if (url != null)
            return new Image(url.toString());
        return null;
    }
}
