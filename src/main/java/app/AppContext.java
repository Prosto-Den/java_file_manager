package app;


import controllers.SettingsController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import models.StringKeys;
import resourceHandler.ResourceHandler;


public class AppContext
{
    private static Stage mainStage;
    private static final String APP_NAME = "Prosto File Manager";


    public static void setMainStage(Stage stage) {mainStage = stage;}

    //TODO лучше каждый раз создавать окно, или конфигурировать существующее? С одной стороны, окно настроек может
    // и не пригодиться и создавать его не нужно. С другой стороны, возвращать уже созданное быстрее,
    // чем каждый раз новое делать
    public static Stage getSettingsStage()
    {
        Stage settingsStage = new Stage();

        if (mainStage != null)
        {
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(mainStage);

            try
            {
                FXMLLoader settingsLoader = new FXMLLoader(ResourceHandler.getLayout("SettingsLayout.fxml"),
                        ResourceHandler.getStringBundle());
                Parent root = settingsLoader.load();
                SettingsController controller = settingsLoader.getController();
                controller.setStage(settingsStage);

                Scene scene = new Scene(root);
                settingsStage.setScene(scene);
                settingsStage.setTitle(ResourceHandler.getString(StringKeys.SETTINGS_TITLE));
            }
            catch (IOException ex)
            {
                System.err.println("gnegli");
            }
        }

        return settingsStage;
    }

    public static String getAppName() {return APP_NAME;}
}
