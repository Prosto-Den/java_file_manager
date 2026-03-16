package app;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import resourceHandler.ResourceHandler;


public class AppContext
{
    private static Stage mainStage;
    private static Stage settingsStage;

    public static void setMainStage(Stage stage) {mainStage = stage;}
    public static Optional<Stage> getMainStage() {return Optional.ofNullable(mainStage);}

    public static Stage getSettingsStage()
    {
        if (settingsStage == null)
            createSettingsStage();
        return settingsStage;
    }

    private static void createSettingsStage()
    {
        try
        {
            if (mainStage != null)
            {
                settingsStage = new Stage();
                settingsStage.initModality(Modality.WINDOW_MODAL);
                settingsStage.initOwner(mainStage);

                FXMLLoader settingsLoader = new FXMLLoader(ResourceHandler.getLayout("SettingsLayout.fxml"),
                        ResourceHandler.getStringBundle());
                Parent root = settingsLoader.load();

                Scene scene = new Scene(root);
                settingsStage.setScene(scene);
            }
        }
        catch (IOException ex)
        {
            System.err.println("lol");
        }
    }
}
