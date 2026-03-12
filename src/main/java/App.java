import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import models.StringKeys;
import monitors.ClipboardMonitor;
import resourceHandler.ResourceHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;


public class App extends Application
{
    @Override
    public void start(Stage stage)
    {
        try
        {
            // TODO локаль должна указываться в настройках
            ResourceHandler.setLocale(Locale.of("ru", "RU"));

            ClipboardMonitor.start();

            FXMLLoader mainLoader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/layouts/MainLayout.fxml")));
            HBox layout = mainLoader.load();


            Scene scene = new Scene(layout);
            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setScene(scene);
            stage.setTitle(ResourceHandler.getString(StringKeys.TITLE));
            stage.show();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.err.println("Не удалось загрузить приложение :(");
        }
    }

    public static void Launch(String[] args)
    {
        launch(args);
    }
}