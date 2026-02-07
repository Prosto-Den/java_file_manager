package resourceHandler;


import java.net.URL;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import types.IconTypes;


public class ResourceHandler
{
    static private class ResourceManager
    {
        private final String fileViewerIconPath = "/icons/file_viewer";
        private final String controlPanelIconPath = "/icons/control_panel";
        private final String systemIconPath = "/icons/system";
        private final String toolbarIconPath = "/icons/toolbar";

        //TODO наверное, распихивать иконки по виджетам всё-таки не очень хорошая идея. Надо будет переделать
        // размещение ресурсов
        // TODO также надо переделать этот метод с построения путей для иконок на вообще все ресурсы
        //  (потом ещё будут строковые)
        /**
         * Метод для построения пути к ресурсу иконки
         * @param type тип иконки (к какому виджету относится)
         * @param size размер иконки
         * @param iconName имя иконки
         * @return URL, если такая иконка существует, иначе null
         * */
        @Nullable
        public URL buildPath(@NotNull IconTypes type,
                             @NotNull String size,
                             @NotNull IconName iconName)
        {
            String path;

            switch (type)
            {
                case FILE_VIEWER -> path = String.join("/", fileViewerIconPath, size, iconName.getValue());
                case CONTROL_PANEL -> path = String.join("/", controlPanelIconPath, size, iconName.getValue());
                case SYSTEM -> path = String.join("/", systemIconPath, size, iconName.getValue());
                case TOOLBAR -> path = String.join("/", toolbarIconPath, size, iconName.getValue());
                default -> path = "";
            }

            return getClass().getResource(path);
        }
    }

    /**
     * Метод для получения иконки
     * @param type тип иконки
     * @param size размер иконки
     * @param iconName имя иконки
     * @return Image, если ресурс для иконки удалось найти, иначе null
     * */
    @Nullable
    static public Image getIcon(@NotNull IconTypes type,
                                @NotNull String size,
                                @NotNull IconName iconName)
    {
        ResourceManager manager = new ResourceManager();
        URL url = manager.buildPath(type, size, iconName);
        if (url != null)
            return new Image(url.toString());
        return null;
    }
}
