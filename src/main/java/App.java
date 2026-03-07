import controllers.MainController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import models.StringKeys;
import monitors.ClipboardMonitor;
import resourceHandler.ResourceHandler;
import utils.FileSystemUtils;

import java.util.Locale;


public class App extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        // TODO локаль должна указываться в настройках
        ResourceHandler.setLocale(Locale.of("ru", "RU"));

        //FileSystemUtils.clearClipboard();
        ClipboardMonitor.start();

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/layouts/MainLayout.fxml"));
        Parent layout = mainLoader.load();

        Scene scene = new Scene(layout);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setScene(scene);
        stage.setTitle(ResourceHandler.getString(StringKeys.TITLE));
        stage.show();
    }

    public static void Launch(String[] args)
    {
        launch(args);
    }
}