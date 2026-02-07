import javafx.application.Application;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;



public class App extends Application
{
    //private AnchorPane layout;

    @Override
    public void start(Stage stage) throws Exception
    {
        //TODO вынести строковые пути к fxml в файлик
        HBox layout = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/layouts/MainLayout.fxml")));

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public static void Launch(String[] args)
    {
        launch(args);
    }
}