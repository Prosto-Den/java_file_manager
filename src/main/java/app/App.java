package app;

import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import models.StringKeys;
import monitors.ClipboardMonitor;
import resourceHandler.ResourceHandler;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;


/**
 * Класс приложения. Тут происходит основная настройка
 * */
public class App extends Application
{
    @Override
    public void start(Stage stage)
    {
        try
        {
            AppContext.setMainStage(stage);

            // TODO локаль должна указываться в настройках
            ResourceHandler.setLocale(Locale.of("ru", "RU"));

            ClipboardMonitor.start();

            FXMLLoader mainLoader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/layouts/MainLayout.fxml")));
            VBox layout = mainLoader.load();

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